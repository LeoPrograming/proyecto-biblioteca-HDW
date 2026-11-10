document.addEventListener("DOMContentLoaded", () => {
    if (document.getElementById("tablaPrestamos")) {
        inicializarListadoPrestamos();
    }

    if (document.getElementById("formPrestamo")) {
        inicializarNuevoPrestamo();
    }

    if (document.getElementById("tablaDetallePrestamo")) {
        inicializarDetallePrestamo();
    }
});

let prestamoSeleccionado = null;

function inicializarListadoPrestamos() {
    const buscador = document.getElementById("buscarPrestamo");
    const btnBuscar = document.getElementById("btnBuscarPrestamo");
    const btnLimpiar = document.getElementById("btnLimpiarPrestamo");
    const btnDetalle = document.getElementById("btnDetallePrestamo");

    cargarPrestamos();

    btnBuscar.addEventListener("click", () => cargarPrestamos(buscador.value.trim()));
    btnLimpiar.addEventListener("click", () => {
        buscador.value = "";
        cargarPrestamos();
    });
    buscador.addEventListener("keydown", event => {
        if (event.key === "Enter") cargarPrestamos(buscador.value.trim());
    });
    btnDetalle.addEventListener("click", () => {
        if (prestamoSeleccionado !== null) {
            window.location.href = `/prestamos/detalle?id=${prestamoSeleccionado}`;
        }
    });
}

async function cargarPrestamos(buscar = "") {
    const tabla = document.getElementById("tablaPrestamos");
    const mensaje = document.getElementById("mensajePrestamos");
    const btnDetalle = document.getElementById("btnDetallePrestamo");

    prestamoSeleccionado = null;
    btnDetalle.disabled = true;
    tabla.innerHTML = `<tr><td colspan="9">Cargando préstamos...</td></tr>`;
    mensaje.textContent = "";

    try {
        let url = "/api/prestamos";
        if (buscar !== "") url += `?buscar=${encodeURIComponent(buscar)}`;

        const response = await fetch(url);
        if (!response.ok) throw new Error(await obtenerMensajeError(response));

        const prestamos = await response.json();
        if (prestamos.length === 0) {
            tabla.innerHTML = `<tr><td colspan="9">No se encontraron préstamos.</td></tr>`;
            return;
        }

        tabla.innerHTML = prestamos.map(p => {
            const usuario = p.usuario
                ? `${p.usuario.nombres ?? ""} ${p.usuario.apellidos ?? ""}`.trim()
                : "";
            const comprobante = p.comprobante?.tipo ?? "";
            const estadoClase = String(p.estado).toLowerCase() === "abierto"
                ? "estado-activo"
                : "estado-inactivo";

            return `
                <tr>
                    <td><input type="radio" name="prestamoSeleccionado" value="${p.idPrestamo}"></td>
                    <td>${p.idPrestamo}</td>
                    <td>${escaparHtml(usuario)}</td>
                    <td>${formatearFechaHora(p.fechaPrestamo)}</td>
                    <td>${escaparHtml(comprobante)}</td>
                    <td>${escaparHtml(p.serie ?? "")}</td>
                    <td>${escaparHtml(p.numero ?? "")}</td>
                    <td>S/ ${numero(p.montoTotal).toFixed(2)}</td>
                    <td><span class="${estadoClase}">${escaparHtml(p.estado ?? "")}</span></td>
                </tr>`;
        }).join("");

        tabla.querySelectorAll('input[name="prestamoSeleccionado"]').forEach(radio => {
            radio.addEventListener("change", () => {
                prestamoSeleccionado = Number(radio.value);
                btnDetalle.disabled = false;
            });
        });
    } catch (error) {
        tabla.innerHTML = `<tr><td colspan="9">No se pudo cargar la información.</td></tr>`;
        mensaje.textContent = error.message;
    }
}

async function inicializarNuevoPrestamo() {
    const form = document.getElementById("formPrestamo");
    const comprobante = document.getElementById("comprobantePrestamo");

    await Promise.all([cargarUsuariosPrestamo(), cargarComprobantes(), cargarLibrosDisponibles()]);

    comprobante.addEventListener("change", cargarDatosComprobante);
    form.addEventListener("submit", registrarPrestamo);
}

async function cargarUsuariosPrestamo() {
    const select = document.getElementById("usuarioPrestamo");
    try {
        const response = await fetch("/api/usuarios");
        if (!response.ok) throw new Error(await obtenerMensajeError(response));
        const usuarios = await response.json();

        usuarios.filter(u => u.estado !== false).forEach(u => {
            const option = document.createElement("option");
            option.value = u.idUsuario;
            option.textContent = `${u.dni ?? ""} - ${u.nombres ?? ""} ${u.apellidos ?? ""}`.trim();
            select.appendChild(option);
        });
    } catch (error) {
        mostrarMensaje("mensajeNuevoPrestamo", error.message, true);
    }
}

async function cargarComprobantes() {
    const select = document.getElementById("comprobantePrestamo");
    try {
        const response = await fetch("/api/prestamos/comprobantes");
        if (!response.ok) throw new Error(await obtenerMensajeError(response));
        const comprobantes = await response.json();

        comprobantes.forEach(c => {
            const option = document.createElement("option");
            option.value = c.idComprobante;
            option.textContent = c.tipo;
            select.appendChild(option);
        });
    } catch (error) {
        mostrarMensaje("mensajeNuevoPrestamo", error.message, true);
    }
}

async function cargarDatosComprobante() {
    const id = document.getElementById("comprobantePrestamo").value;
    document.getElementById("seriePrestamo").value = "";
    document.getElementById("numeroPrestamo").value = "";
    if (!id) return;

    try {
        const response = await fetch(`/api/prestamos/comprobante?idComprobante=${encodeURIComponent(id)}`);
        if (!response.ok) throw new Error(await obtenerMensajeError(response));
        const datos = await response.json();
        document.getElementById("seriePrestamo").value = datos.serie ?? "";
        document.getElementById("numeroPrestamo").value = datos.numero ?? "";
    } catch (error) {
        mostrarMensaje("mensajeNuevoPrestamo", error.message, true);
    }
}

async function cargarLibrosDisponibles() {
    const tabla = document.getElementById("tablaLibrosPrestamo");
    try {
        const response = await fetch("/api/libros");
        if (!response.ok) throw new Error(await obtenerMensajeError(response));
        const libros = (await response.json()).filter(l => l.estado === true);

        if (libros.length === 0) {
            tabla.innerHTML = `<tr><td colspan="7">No hay libros disponibles.</td></tr>`;
            return;
        }

        tabla.innerHTML = libros.map(l => `
            <tr data-id-libro="${l.idLibro}" data-precio="${numero(l.precioTiempo)}">
                <td><input type="checkbox" class="seleccionar-libro"></td>
                <td>${l.idLibro}</td>
                <td>${escaparHtml(l.titulo ?? "")}</td>
                <td>${escaparHtml(l.isbn ?? "")}</td>
                <td>S/ ${numero(l.precioTiempo).toFixed(2)}</td>
                <td><input type="number" class="dias-libro" min="1" value="1" style="width: 90px;"></td>
                <td class="subtotal-libro">S/ ${numero(l.precioTiempo).toFixed(2)}</td>
            </tr>`).join("");

        tabla.querySelectorAll(".seleccionar-libro, .dias-libro").forEach(control => {
            control.addEventListener("change", actualizarTotalesPrestamo);
            control.addEventListener("input", actualizarTotalesPrestamo);
        });

        actualizarTotalesPrestamo();
    } catch (error) {
        tabla.innerHTML = `<tr><td colspan="7">No se pudieron cargar los libros.</td></tr>`;
        mostrarMensaje("mensajeNuevoPrestamo", error.message, true);
    }
}

function actualizarTotalesPrestamo() {
    let total = 0;
    document.querySelectorAll("#tablaLibrosPrestamo tr[data-id-libro]").forEach(fila => {
        const precio = numero(fila.dataset.precio);
        const diasInput = fila.querySelector(".dias-libro");
        const dias = Math.max(1, Number(diasInput.value) || 1);
        const subtotal = precio * dias;
        fila.querySelector(".subtotal-libro").textContent = `S/ ${subtotal.toFixed(2)}`;
        if (fila.querySelector(".seleccionar-libro").checked) total += subtotal;
    });
    document.getElementById("totalPrestamo").value = total.toFixed(2);
}

async function registrarPrestamo(event) {
    event.preventDefault();
    const mensajeId = "mensajeNuevoPrestamo";
    const idUsuario = Number(document.getElementById("usuarioPrestamo").value);
    const idComprobante = Number(document.getElementById("comprobantePrestamo").value);

    const filas = [...document.querySelectorAll("#tablaLibrosPrestamo tr[data-id-libro]")]
        .filter(fila => fila.querySelector(".seleccionar-libro").checked);

    if (!idUsuario) return mostrarMensaje(mensajeId, "Debe seleccionar un usuario.", true);
    if (!idComprobante) return mostrarMensaje(mensajeId, "Debe seleccionar un comprobante.", true);
    if (filas.length === 0) return mostrarMensaje(mensajeId, "Debe seleccionar al menos un libro.", true);

    const idsLibros = [];
    const dias = [];
    const precios = [];

    for (const fila of filas) {
        const tiempo = Number(fila.querySelector(".dias-libro").value);
        if (!Number.isInteger(tiempo) || tiempo <= 0) {
            return mostrarMensaje(mensajeId, "El tiempo de préstamo debe ser mayor a cero.", true);
        }
        idsLibros.push(Number(fila.dataset.idLibro));
        dias.push(tiempo);
        precios.push(numero(fila.dataset.precio));
    }

    const payload = {
        idUsuario,
        idComprobante,
        serie: document.getElementById("seriePrestamo").value,
        numero: document.getElementById("numeroPrestamo").value,
        observaciones: document.getElementById("observacionesPrestamo").value.trim(),
        idsLibros,
        dias,
        precios
    };

    try {
        mostrarMensaje(mensajeId, "Registrando préstamo...", false);
        const response = await fetch("/api/prestamos", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(payload)
        });
        if (!response.ok) throw new Error(await obtenerMensajeError(response));
        const prestamo = await response.json();
        window.location.href = `/prestamos/detalle?id=${prestamo.idPrestamo}`;
    } catch (error) {
        mostrarMensaje(mensajeId, error.message, true);
    }
}

async function inicializarDetallePrestamo() {
    const id = Number(new URLSearchParams(window.location.search).get("id"));
    if (!id) {
        document.getElementById("mensajeDetallePrestamo").textContent = "Préstamo no válido.";
        return;
    }
    await cargarDetallePrestamo(id);
}

async function cargarDetallePrestamo(id) {
    const resumen = document.getElementById("resumenPrestamo");
    const tabla = document.getElementById("tablaDetallePrestamo");
    const mensaje = document.getElementById("mensajeDetallePrestamo");
    mensaje.textContent = "";

    try {
        const [prestamoResponse, detallesResponse] = await Promise.all([
            fetch(`/api/prestamos/${id}`),
            fetch(`/api/prestamos/${id}/detalles`)
        ]);
        if (!prestamoResponse.ok) throw new Error(await obtenerMensajeError(prestamoResponse));
        if (!detallesResponse.ok) throw new Error(await obtenerMensajeError(detallesResponse));

        const prestamo = await prestamoResponse.json();
        const detalles = await detallesResponse.json();
        const usuario = prestamo.usuario
            ? `${prestamo.usuario.nombres ?? ""} ${prestamo.usuario.apellidos ?? ""}`.trim()
            : "";

        resumen.innerHTML = `
            <strong>Préstamo #${prestamo.idPrestamo}</strong><br>
            Usuario: ${escaparHtml(usuario)}<br>
            Fecha: ${formatearFechaHora(prestamo.fechaPrestamo)}<br>
            Estado: ${escaparHtml(prestamo.estado ?? "")}<br>
            Total: S/ ${numero(prestamo.montoTotal).toFixed(2)}<br>
            Observaciones: ${escaparHtml(prestamo.observaciones ?? "-")}`;

        tabla.innerHTML = detalles.map(d => {
            const prestado = String(d.estado).toLowerCase() === "prestado";
            return `
                <tr>
                    <td>${d.idDetallePrestamo}</td>
                    <td>${escaparHtml(d.tituloLibro ?? "")}</td>
                    <td>S/ ${numero(d.precioTiempo).toFixed(2)}</td>
                    <td>${d.tiempo}</td>
                    <td>${formatearFecha(d.fechaCompromiso)}</td>
                    <td>${formatearFecha(d.fechaDevolucion)}</td>
                    <td>S/ ${numero(d.subtotal).toFixed(2)}</td>
                    <td>${escaparHtml(d.estado ?? "")}</td>
                    <td>${prestado ? `<button class="btn btn-blue" data-devolver="${d.idDetallePrestamo}">Devolver</button>` : "Devuelto"}</td>
                </tr>`;
        }).join("");

        tabla.querySelectorAll("[data-devolver]").forEach(btn => {
            btn.addEventListener("click", async () => registrarDevolucionDetalle(Number(btn.dataset.devolver), id));
        });
    } catch (error) {
        resumen.textContent = "No se pudo cargar el préstamo.";
        tabla.innerHTML = `<tr><td colspan="9">Sin información.</td></tr>`;
        mensaje.textContent = error.message;
    }
}

async function registrarDevolucionDetalle(idDetalle, idPrestamo) {
    try {
        const response = await fetch(`/api/prestamos/devolucion/${idDetalle}`, {method: "PUT"});
        if (!response.ok) throw new Error(await obtenerMensajeError(response));
        await cargarDetallePrestamo(idPrestamo);
    } catch (error) {
        document.getElementById("mensajeDetallePrestamo").textContent = error.message;
    }
}

async function obtenerMensajeError(response) {
    try {
        const data = await response.json();
        return data.mensaje || data.message || "Ocurrió un error.";
    } catch (_) {
        return "Ocurrió un error.";
    }
}

function mostrarMensaje(id, texto, error) {
    const elemento = document.getElementById(id);
    if (!elemento) return;
    elemento.textContent = texto;
    elemento.className = error ? "form-message error" : "form-message success";
}

function numero(valor) {
    const n = Number(valor);
    return Number.isFinite(n) ? n : 0;
}

function formatearFechaHora(valor) {
    if (!valor) return "-";
    const fecha = new Date(valor);
    return Number.isNaN(fecha.getTime()) ? valor : fecha.toLocaleString("es-PE");
}

function formatearFecha(valor) {
    if (!valor) return "-";
    const fecha = new Date(`${valor}T00:00:00`);
    return Number.isNaN(fecha.getTime()) ? valor : fecha.toLocaleDateString("es-PE");
}

function escaparHtml(valor) {
    return String(valor ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

// =========================
// REPORTES DEL ADMINISTRADOR
// Usa únicamente los endpoints existentes:
// /api/usuarios, /api/libros,
// /api/categorias y /api/prestamos
// =========================

document.addEventListener("DOMContentLoaded", () => {

    if (!document.getElementById("tablaReporteUsuarios")) {
        return;
    }

    cargarReportes();

    const btnActualizar =
        document.getElementById("btnActualizarReportes");

    if (btnActualizar) {
        btnActualizar.addEventListener("click", cargarReportes);
    }
});


async function obtenerJsonReporte(url) {

    const response = await fetch(url);

    if (!response.ok) {
        throw new Error("Error al consultar " + url);
    }

    return response.json();
}


async function cargarReportes() {

    const mensaje =
        document.getElementById("mensajeReportes");

    mensaje.textContent = "";

    try {

        const [usuarios, libros, categorias, prestamos] =
            await Promise.all([
                obtenerJsonReporte("/api/usuarios"),
                obtenerJsonReporte("/api/libros"),
                obtenerJsonReporte("/api/categorias"),
                obtenerJsonReporte("/api/prestamos")
            ]);

        mostrarResumenReportes(usuarios, libros, prestamos);
        mostrarReporteUsuarios(usuarios);
        mostrarReporteLibros(libros, categorias);
        mostrarReportePrestamos(prestamos);
        mostrarUltimosPrestamos(prestamos);

    } catch (error) {

        console.error(error);

        mensaje.textContent =
            "No se pudieron cargar los reportes.";
    }
}


function formatearMonto(valor) {

    return Number(valor || 0).toFixed(2);
}


function escaparReporte(texto) {

    const div = document.createElement("div");

    div.textContent = texto == null ? "" : String(texto);

    return div.innerHTML;
}


function mostrarResumenReportes(usuarios, libros, prestamos) {

    const abiertos = prestamos.filter(p =>
        String(p.estado || "").toLowerCase() === "abierto"
    ).length;

    const montoTotal = prestamos.reduce(
        (suma, p) => suma + Number(p.montoTotal || 0),
        0
    );

    document.getElementById("totalUsuarios").textContent =
        usuarios.length;

    document.getElementById("totalLibros").textContent =
        libros.length;

    document.getElementById("totalPrestamos").textContent =
        prestamos.length;

    document.getElementById("totalPrestamosAbiertos").textContent =
        abiertos;

    document.getElementById("montoTotalPrestamos").textContent =
        formatearMonto(montoTotal);
}


function mostrarReporteUsuarios(usuarios) {

    const tabla =
        document.getElementById("tablaReporteUsuarios");

    const roles = {};

    usuarios.forEach(u => {

        const rol = obtenerNombreRol(u.idTipoUsuario);

        if (!roles[rol]) {
            roles[rol] = { total: 0, activos: 0, inactivos: 0 };
        }

        roles[rol].total++;

        if (u.estado) {
            roles[rol].activos++;
        } else {
            roles[rol].inactivos++;
        }
    });

    const filas = Object.keys(roles);

    if (filas.length === 0) {
        tabla.innerHTML =
            '<tr><td colspan="4">No hay usuarios registrados.</td></tr>';
        return;
    }

    tabla.innerHTML = filas.map(rol => `
        <tr>
            <td>${escaparReporte(rol)}</td>
            <td>${roles[rol].total}</td>
            <td>${roles[rol].activos}</td>
            <td>${roles[rol].inactivos}</td>
        </tr>
    `).join("");
}


function mostrarReporteLibros(libros, categorias) {

    const tabla =
        document.getElementById("tablaReporteLibros");

    const nombresCategoria = {};

    categorias.forEach(c => {
        nombresCategoria[c.idCategoria] = c.nombre;
    });

    const grupos = {};

    libros.forEach(libro => {

        const nombre =
            nombresCategoria[libro.idCategoria] || "Sin categoría";

        if (!grupos[nombre]) {
            grupos[nombre] = { total: 0, activos: 0, inactivos: 0 };
        }

        grupos[nombre].total++;

        if (libro.estado) {
            grupos[nombre].activos++;
        } else {
            grupos[nombre].inactivos++;
        }
    });

    const filas = Object.keys(grupos).sort();

    if (filas.length === 0) {
        tabla.innerHTML =
            '<tr><td colspan="4">No hay libros registrados.</td></tr>';
        return;
    }

    tabla.innerHTML = filas.map(nombre => `
        <tr>
            <td>${escaparReporte(nombre)}</td>
            <td>${grupos[nombre].total}</td>
            <td>${grupos[nombre].activos}</td>
            <td>${grupos[nombre].inactivos}</td>
        </tr>
    `).join("");
}


function mostrarReportePrestamos(prestamos) {

    const tabla =
        document.getElementById("tablaReportePrestamos");

    const estados = {};

    prestamos.forEach(p => {

        const estado = p.estado || "Sin estado";

        if (!estados[estado]) {
            estados[estado] = { cantidad: 0, monto: 0 };
        }

        estados[estado].cantidad++;
        estados[estado].monto += Number(p.montoTotal || 0);
    });

    const filas = Object.keys(estados);

    if (filas.length === 0) {
        tabla.innerHTML =
            '<tr><td colspan="3">No hay préstamos registrados.</td></tr>';
        return;
    }

    tabla.innerHTML = filas.map(estado => `
        <tr>
            <td>${escaparReporte(estado)}</td>
            <td>${estados[estado].cantidad}</td>
            <td>${formatearMonto(estados[estado].monto)}</td>
        </tr>
    `).join("");
}


function mostrarUltimosPrestamos(prestamos) {

    const tabla =
        document.getElementById("tablaUltimosPrestamos");

    const ultimos = [...prestamos]
        .sort((a, b) =>
            String(b.fechaPrestamo || "").localeCompare(
                String(a.fechaPrestamo || "")
            )
        )
        .slice(0, 10);

    if (ultimos.length === 0) {
        tabla.innerHTML =
            '<tr><td colspan="6">No hay préstamos registrados.</td></tr>';
        return;
    }

    tabla.innerHTML = ultimos.map(p => {

        const lector = p.usuario
            ? `${p.usuario.nombres || ""} ${p.usuario.apellidos || ""}`.trim()
            : "-";

        const comprobante =
            p.serie || p.numero
                ? `${p.serie || ""}-${p.numero || ""}`
                : "-";

        const fecha = p.fechaPrestamo
            ? String(p.fechaPrestamo).replace("T", " ").substring(0, 16)
            : "-";

        return `
            <tr>
                <td>${p.idPrestamo}</td>
                <td>${escaparReporte(comprobante)}</td>
                <td>${escaparReporte(lector)}</td>
                <td>${escaparReporte(fecha)}</td>
                <td>${escaparReporte(p.estado)}</td>
                <td>${formatearMonto(p.montoTotal)}</td>
            </tr>
        `;
    }).join("");
}

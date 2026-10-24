document.addEventListener("DOMContentLoaded", () => {

    const loginForm = document.getElementById("loginForm");

    if (loginForm) {

        loginForm.addEventListener("submit", async (event) => {

            event.preventDefault();

            const usuario =
                document.getElementById("usuario").value.trim();

            const contrasena =
                document.getElementById("contrasena").value;

            const mensaje =
                document.getElementById("loginMensaje");

            mensaje.textContent = "Validando...";

            try {

                const response = await fetch("/api/auth/login", {

                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify({
                        usuario: usuario,
                        contrasena: contrasena
                    })
                });

                if (!response.ok) {
                    throw new Error("Credenciales incorrectas");
                }

                const data = await response.json();

                localStorage.setItem(
                    "bibliotecaUsuario",
                    usuario
                );

                localStorage.setItem(
                    "bibliotecaSesion",
                    JSON.stringify(data)
                );

                window.location.href = "/dashboard";

            } catch (error) {

                mensaje.textContent =
                    "Usuario o contraseña incorrectos.";

            }

        });

    }


    const usuarioActual =
        document.getElementById("usuarioActual");

    if (usuarioActual) {

        const usuario =
            localStorage.getItem("bibliotecaUsuario");

        if (usuario) {
            usuarioActual.textContent = usuario;
        }

    }


    const btnCerrarSesion =
        document.getElementById("btnCerrarSesion");

    if (btnCerrarSesion) {

        btnCerrarSesion.addEventListener("click", () => {

            localStorage.removeItem("bibliotecaUsuario");
            localStorage.removeItem("bibliotecaSesion");

            window.location.href = "/login";

        });

    }


    const tablaUsuarios =
        document.getElementById("tablaUsuarios");

    if (tablaUsuarios) {

        cargarUsuarios();

        const btnBuscar =
            document.getElementById("btnBuscarUsuario");

        const btnLimpiar =
            document.getElementById("btnLimpiarBusqueda");

        const buscador =
            document.getElementById("buscarUsuario");


        if (btnBuscar) {

            btnBuscar.addEventListener("click", () => {

                cargarUsuarios(
                    buscador.value.trim()
                );

            });

        }


        if (btnLimpiar) {

            btnLimpiar.addEventListener("click", () => {

                buscador.value = "";

                cargarUsuarios();

            });

        }


        if (buscador) {

            buscador.addEventListener("keydown", (event) => {

                if (event.key === "Enter") {

                    cargarUsuarios(
                        buscador.value.trim()
                    );

                }

            });

        }

    }

});


let usuarioSeleccionado = null;


async function cargarUsuarios(buscar = "") {

    const tabla =
        document.getElementById("tablaUsuarios");

    const mensaje =
        document.getElementById("mensajeUsuarios");

    usuarioSeleccionado = null;

    const btnEditar =
        document.getElementById("btnEditarUsuario");

    const btnEstado =
        document.getElementById("btnEstadoUsuario");

    if (btnEditar) {
        btnEditar.disabled = true;
    }

    if (btnEstado) {
        btnEstado.disabled = true;
    }

    tabla.innerHTML = `
        <tr>
            <td colspan="11">
                Cargando usuarios...
            </td>
        </tr>
    `;

    try {

        let url = "/api/usuarios";

        if (buscar !== "") {

            url +=
                "?buscar=" +
                encodeURIComponent(buscar);

        }

        const response = await fetch(url);

        if (response.status === 401) {

            window.location.href = "/login";

            return;

        }

        if (response.status === 403) {

            throw new Error(
                "No tiene permisos para administrar usuarios."
            );

        }

        if (!response.ok) {

            throw new Error(
                "No se pudieron obtener los usuarios."
            );

        }

        const usuarios =
            await response.json();

        mostrarUsuarios(usuarios);

        if (mensaje) {
            mensaje.textContent = "";
        }

    } catch (error) {

        tabla.innerHTML = `
            <tr>
                <td colspan="11">
                    No se pudieron cargar los usuarios.
                </td>
            </tr>
        `;

        if (mensaje) {
            mensaje.textContent = error.message;
        }

    }

}


function mostrarUsuarios(usuarios) {

    const tabla =
        document.getElementById("tablaUsuarios");

    if (!usuarios || usuarios.length === 0) {

        tabla.innerHTML = `
            <tr>
                <td colspan="11">
                    No se encontraron usuarios.
                </td>
            </tr>
        `;

        return;

    }

    tabla.innerHTML = "";

    usuarios.forEach(usuario => {

        const fila =
            document.createElement("tr");

        const rol =
            obtenerNombreRol(
                usuario.idTipoUsuario
            );

        const estado =
            usuario.estado
                ? '<span class="estado-activo">Activo</span>'
                : '<span class="estado-inactivo">Inactivo</span>';

        fila.innerHTML = `

            <td>
                <input
                    type="radio"
                    name="usuarioSeleccion"
                    value="${usuario.idUsuario}">
            </td>

            <td>${usuario.idUsuario ?? ""}</td>

            <td>${usuario.dni ?? ""}</td>

            <td>${usuario.usuario ?? ""}</td>

            <td>${usuario.nombres ?? ""}</td>

            <td>${usuario.apellidos ?? ""}</td>

            <td>${rol}</td>

            <td>${usuario.correoE ?? ""}</td>

            <td>${usuario.telefono ?? ""}</td>

            <td>${usuario.direccion ?? ""}</td>

            <td>${estado}</td>
        `;

        tabla.appendChild(fila);

    });

    configurarSeleccionUsuario();

}


function configurarSeleccionUsuario() {

    const radios =
        document.querySelectorAll(
            'input[name="usuarioSeleccion"]'
        );

    radios.forEach(radio => {

        radio.addEventListener(
            "change",
            () => {

                usuarioSeleccionado =
                    Number(radio.value);

                const btnEditar =
                    document.getElementById(
                        "btnEditarUsuario"
                    );

                const btnEstado =
                    document.getElementById(
                        "btnEstadoUsuario"
                    );

                if (btnEditar) {
                    btnEditar.disabled = false;
                }

                if (btnEstado) {
                    btnEstado.disabled = false;
                }

            }
        );

    });

}


function obtenerNombreRol(idTipoUsuario) {

    switch (Number(idTipoUsuario)) {

        case 1:
            return "Administrador";

        case 2:
            return "Bibliotecario";

        case 3:
            return "Lector";

        default:
            return "Sin definir";

    }

}


// =========================
// BOTONES GESTIÓN USUARIOS
// =========================

document.addEventListener("DOMContentLoaded", () => {

    const btnAgregarUsuario =
        document.getElementById("btnAgregarUsuario");

    if (btnAgregarUsuario) {

        btnAgregarUsuario.addEventListener("click", () => {
            window.location.href = "/usuarios/nuevo";
        });

    }


    const btnEditarUsuario =
        document.getElementById("btnEditarUsuario");

    if (btnEditarUsuario) {

        btnEditarUsuario.addEventListener("click", () => {

            if (!usuarioSeleccionado) {
                return;
            }

            localStorage.setItem(
                "usuarioEditarId",
                usuarioSeleccionado
            );

            window.location.href = "/usuarios/editar";

        });

    }


    const btnEstadoUsuario =
        document.getElementById("btnEstadoUsuario");

    if (btnEstadoUsuario) {

        btnEstadoUsuario.addEventListener(
            "click",
            cambiarEstadoUsuario
        );

    }


    const formNuevo =
        document.getElementById("formNuevoUsuario");

    if (formNuevo) {

        formNuevo.addEventListener(
            "submit",
            registrarUsuario
        );

    }


    const formEditar =
        document.getElementById("formEditarUsuario");

    if (formEditar) {

        cargarUsuarioEditar();

        formEditar.addEventListener(
            "submit",
            actualizarUsuario
        );

    }

});


async function registrarUsuario(event) {

    event.preventDefault();

    const mensaje =
        document.getElementById("mensajeFormulario");

    const datos = {

        dni:
            document.getElementById("dni").value.trim(),

        usuario:
            document.getElementById("usuarioNuevo").value.trim(),

        contrasena:
        document.getElementById("contrasenaNueva").value,

        nombres:
            document.getElementById("nombres").value.trim(),

        apellidos:
            document.getElementById("apellidos").value.trim(),

        correoE:
            document.getElementById("correoE").value.trim(),

        telefono:
            document.getElementById("telefono").value.trim(),

        direccion:
            document.getElementById("direccion").value.trim(),

        idTipoUsuario:
            Number(
                document.getElementById("idTipoUsuario").value
            )
    };

    try {

        const response = await fetch(
            "/api/usuarios",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(datos)
            }
        );

        const respuesta =
            await response.json();

        if (!response.ok) {

            throw new Error(
                respuesta.mensaje ||
                "No se pudo registrar el usuario."
            );

        }

        mensaje.className =
            "form-message success";

        mensaje.textContent =
            "Usuario registrado correctamente.";

        setTimeout(() => {

            window.location.href =
                "/usuarios";

        }, 700);

    } catch (error) {

        mensaje.className =
            "form-message error";

        mensaje.textContent =
            error.message;

    }

}


async function cargarUsuarioEditar() {

    const id =
        localStorage.getItem("usuarioEditarId");

    if (!id) {

        window.location.href = "/usuarios";

        return;

    }

    try {

        const response =
            await fetch(`/api/usuarios/${id}`);

        if (!response.ok) {

            throw new Error(
                "No se pudo cargar el usuario."
            );

        }

        const usuario =
            await response.json();

        document.getElementById(
            "idUsuarioEditar"
        ).value =
            usuario.idUsuario ?? "";

        document.getElementById(
            "dniEditar"
        ).value =
            usuario.dni ?? "";

        document.getElementById(
            "usuarioEditar"
        ).value =
            usuario.usuario ?? "";

        document.getElementById(
            "nombresEditar"
        ).value =
            usuario.nombres ?? "";

        document.getElementById(
            "apellidosEditar"
        ).value =
            usuario.apellidos ?? "";

        document.getElementById(
            "correoEditar"
        ).value =
            usuario.correoE ?? "";

        document.getElementById(
            "telefonoEditar"
        ).value =
            usuario.telefono ?? "";

        document.getElementById(
            "direccionEditar"
        ).value =
            usuario.direccion ?? "";

        document.getElementById(
            "tipoUsuarioEditar"
        ).value =
            usuario.idTipoUsuario ?? "";

    } catch (error) {

        const mensaje =
            document.getElementById("mensajeEditar");

        mensaje.className =
            "form-message error";

        mensaje.textContent =
            error.message;

    }

}


async function actualizarUsuario(event) {

    event.preventDefault();

    const id =
        document.getElementById(
            "idUsuarioEditar"
        ).value;

    const mensaje =
        document.getElementById(
            "mensajeEditar"
        );

    const datos = {

        dni:
            document.getElementById(
                "dniEditar"
            ).value.trim(),

        usuario:
            document.getElementById(
                "usuarioEditar"
            ).value.trim(),

        nombres:
            document.getElementById(
                "nombresEditar"
            ).value.trim(),

        apellidos:
            document.getElementById(
                "apellidosEditar"
            ).value.trim(),

        correoE:
            document.getElementById(
                "correoEditar"
            ).value.trim(),

        telefono:
            document.getElementById(
                "telefonoEditar"
            ).value.trim(),

        direccion:
            document.getElementById(
                "direccionEditar"
            ).value.trim(),

        idTipoUsuario:
            Number(
                document.getElementById(
                    "tipoUsuarioEditar"
                ).value
            )
    };

    const nuevaContrasena =
        document.getElementById(
            "contrasenaEditar"
        ).value;

    if (nuevaContrasena.trim() !== "") {
        datos.contrasena = nuevaContrasena;
    }

    try {

        const response =
            await fetch(
                `/api/usuarios/${id}`,
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(datos)
                }
            );

        const respuesta =
            await response.json();

        if (!response.ok) {

            throw new Error(
                respuesta.mensaje ||
                "No se pudo actualizar el usuario."
            );

        }

        mensaje.className =
            "form-message success";

        mensaje.textContent =
            "Usuario actualizado correctamente.";

        localStorage.removeItem(
            "usuarioEditarId"
        );

        setTimeout(() => {

            window.location.href =
                "/usuarios";

        }, 700);

    } catch (error) {

        mensaje.className =
            "form-message error";

        mensaje.textContent =
            error.message;

    }

}


async function cambiarEstadoUsuario() {

    if (!usuarioSeleccionado) {
        return;
    }

    const confirmar =
        window.confirm(
            "¿Desea cambiar el estado del usuario seleccionado?"
        );

    if (!confirmar) {
        return;
    }

    try {

        const response =
            await fetch(
                `/api/usuarios/${usuarioSeleccionado}/estado`,
                {
                    method: "PATCH"
                }
            );

        if (!response.ok) {

            const respuesta =
                await response.json();

            throw new Error(
                respuesta.mensaje ||
                "No se pudo cambiar el estado."
            );

        }

        usuarioSeleccionado = null;

        await cargarUsuarios();

    } catch (error) {

        const mensaje =
            document.getElementById(
                "mensajeUsuarios"
            );

        mensaje.textContent =
            error.message;

    }

}
// =====================================================
// MÓDULO LECTORES
// =====================================================

let lectorSeleccionado = null;


// =====================================================
// INICIALIZACIÓN DEL MÓDULO
// =====================================================

document.addEventListener("DOMContentLoaded", () => {

    // -------------------------
    // LISTADO
    // -------------------------

    const tablaLectores =
        document.getElementById("tablaLectores");

    const buscarLector =
        document.getElementById("buscarLector");

    const btnBuscarLector =
        document.getElementById("btnBuscarLector");

    const btnLimpiarBusquedaLector =
        document.getElementById(
            "btnLimpiarBusquedaLector"
        );

    if (tablaLectores) {

        cargarLectores();

        if (btnBuscarLector) {

            btnBuscarLector.addEventListener(
                "click",
                () => {

                    cargarLectores(
                        buscarLector.value.trim()
                    );

                }
            );
        }

        if (buscarLector) {

            buscarLector.addEventListener(
                "keydown",
                (event) => {

                    if (event.key === "Enter") {

                        cargarLectores(
                            buscarLector.value.trim()
                        );

                    }

                }
            );
        }

        if (btnLimpiarBusquedaLector) {

            btnLimpiarBusquedaLector.addEventListener(
                "click",
                () => {

                    buscarLector.value = "";

                    cargarLectores();

                }
            );
        }
    }


    // -------------------------
    // BOTÓN AGREGAR
    // -------------------------

    const btnAgregarLector =
        document.getElementById(
            "btnAgregarLector"
        );

    if (btnAgregarLector) {

        btnAgregarLector.addEventListener(
            "click",
            () => {

                window.location.href =
                    "/lectores/nuevo";

            }
        );
    }


    // -------------------------
    // BOTÓN EDITAR
    // -------------------------

    const btnEditarLector =
        document.getElementById(
            "btnEditarLector"
        );

    if (btnEditarLector) {

        btnEditarLector.addEventListener(
            "click",
            () => {

                if (!lectorSeleccionado) {
                    return;
                }

                localStorage.setItem(
                    "lectorEditarId",
                    lectorSeleccionado
                );

                window.location.href =
                    "/lectores/editar";

            }
        );
    }


    // -------------------------
    // BOTÓN CAMBIAR ESTADO
    // -------------------------

    const btnEstadoLector =
        document.getElementById(
            "btnEstadoLector"
        );

    if (btnEstadoLector) {

        btnEstadoLector.addEventListener(
            "click",
            cambiarEstadoLector
        );
    }


    // -------------------------
    // FORMULARIO NUEVO LECTOR
    // -------------------------

    const formNuevoLector =
        document.getElementById(
            "formNuevoLector"
        );

    if (formNuevoLector) {

        formNuevoLector.addEventListener(
            "submit",
            registrarLector
        );
    }


    // -------------------------
    // FORMULARIO EDITAR LECTOR
    // -------------------------

    const formEditarLector =
        document.getElementById(
            "formEditarLector"
        );

    if (formEditarLector) {

        cargarLectorEditar();

        formEditarLector.addEventListener(
            "submit",
            actualizarLector
        );
    }

});


// =====================================================
// CARGAR LECTORES
// =====================================================

async function cargarLectores(busqueda = "") {

    const tabla =
        document.getElementById(
            "tablaLectores"
        );

    const mensaje =
        document.getElementById(
            "mensajeLectores"
        );

    if (!tabla) {
        return;
    }

    lectorSeleccionado = null;

    const btnEditar =
        document.getElementById(
            "btnEditarLector"
        );

    const btnEstado =
        document.getElementById(
            "btnEstadoLector"
        );

    if (btnEditar) {
        btnEditar.disabled = true;
    }

    if (btnEstado) {
        btnEstado.disabled = true;
    }

    tabla.innerHTML = `
        <tr>
            <td colspan="10">
                Cargando lectores...
            </td>
        </tr>
    `;

    try {

        let url = "/api/usuarios";

        if (busqueda !== "") {

            url +=
                "?buscar=" +
                encodeURIComponent(busqueda);

        }

        const response =
            await fetch(url);

        if (response.status === 401) {

            window.location.href =
                "/login";

            return;
        }

        if (response.status === 403) {

            throw new Error(
                "No tiene permisos para administrar lectores."
            );
        }

        if (!response.ok) {

            throw new Error(
                "No se pudieron obtener los lectores."
            );
        }

        const usuarios =
            await response.json();

        const lectores =
            usuarios.filter(
                usuario =>
                    Number(
                        usuario.idTipoUsuario
                    ) === 3
            );

        mostrarLectores(lectores);

        if (mensaje) {
            mensaje.textContent = "";
        }

    } catch (error) {

        tabla.innerHTML = `
            <tr>
                <td colspan="10">
                    No se pudieron cargar los lectores.
                </td>
            </tr>
        `;

        if (mensaje) {
            mensaje.textContent =
                error.message;
        }

        console.error(error);
    }
}


// =====================================================
// MOSTRAR LECTORES
// =====================================================

function mostrarLectores(lectores) {

    const tabla =
        document.getElementById(
            "tablaLectores"
        );

    if (!tabla) {
        return;
    }

    if (
        !lectores ||
        lectores.length === 0
    ) {

        tabla.innerHTML = `
            <tr>
                <td colspan="10">
                    No se encontraron lectores.
                </td>
            </tr>
        `;

        return;
    }

    tabla.innerHTML = "";

    lectores.forEach(lector => {

        const fila =
            document.createElement("tr");

        const estado =
            lector.estado
                ? '<span class="estado-activo">Activo</span>'
                : '<span class="estado-inactivo">Inactivo</span>';

        fila.innerHTML = `

            <td>
                <input
                    type="radio"
                    name="lectorSeleccionado"
                    value="${lector.idUsuario}">
            </td>

            <td>
                ${lector.idUsuario ?? ""}
            </td>

            <td>
                ${lector.dni ?? ""}
            </td>

            <td>
                ${lector.usuario ?? ""}
            </td>

            <td>
                ${lector.nombres ?? ""}
            </td>

            <td>
                ${lector.apellidos ?? ""}
            </td>

            <td>
                ${lector.correoE ?? ""}
            </td>

            <td>
                ${lector.telefono ?? ""}
            </td>

            <td>
                ${lector.direccion ?? ""}
            </td>

            <td>
                ${estado}
            </td>
        `;

        tabla.appendChild(fila);

    });

    configurarSeleccionLector();
}


// =====================================================
// SELECCIONAR LECTOR
// =====================================================

function configurarSeleccionLector() {

    const radios =
        document.querySelectorAll(
            'input[name="lectorSeleccionado"]'
        );

    radios.forEach(radio => {

        radio.addEventListener(
            "change",
            () => {

                lectorSeleccionado =
                    Number(radio.value);

                const btnEditar =
                    document.getElementById(
                        "btnEditarLector"
                    );

                const btnEstado =
                    document.getElementById(
                        "btnEstadoLector"
                    );

                if (btnEditar) {
                    btnEditar.disabled = false;
                }

                if (btnEstado) {
                    btnEstado.disabled = false;
                }

            }
        );

    });
}


// =====================================================
// REGISTRAR LECTOR
// =====================================================

async function registrarLector(event) {

    event.preventDefault();

    const mensaje =
        document.getElementById(
            "mensajeLectorFormulario"
        );

    const datos = {

        dni:
            document.getElementById(
                "dniLector"
            ).value.trim(),

        usuario:
            document.getElementById(
                "usuarioLector"
            ).value.trim(),

        contrasena:
        document.getElementById(
            "contrasenaLector"
        ).value,

        nombres:
            document.getElementById(
                "nombresLector"
            ).value.trim(),

        apellidos:
            document.getElementById(
                "apellidosLector"
            ).value.trim(),

        correoE:
            document.getElementById(
                "correoLector"
            ).value.trim(),

        telefono:
            document.getElementById(
                "telefonoLector"
            ).value.trim(),

        direccion:
            document.getElementById(
                "direccionLector"
            ).value.trim(),

        // El usuario creado desde este módulo
        // siempre será Lector
        idTipoUsuario: 3
    };

    try {

        const response =
            await fetch(
                "/api/usuarios",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(datos)
                }
            );

        const respuesta =
            await response.json();

        if (!response.ok) {

            throw new Error(
                respuesta.mensaje ||
                "No se pudo registrar el lector."
            );
        }

        mensaje.className =
            "form-message success";

        mensaje.textContent =
            "Lector registrado correctamente.";

        setTimeout(() => {

            window.location.href =
                "/lectores";

        }, 700);

    } catch (error) {

        mensaje.className =
            "form-message error";

        mensaje.textContent =
            error.message;
    }
}


// =====================================================
// CARGAR LECTOR PARA EDITAR
// =====================================================

async function cargarLectorEditar() {

    const id =
        localStorage.getItem(
            "lectorEditarId"
        );

    if (!id) {

        window.location.href =
            "/lectores";

        return;
    }

    try {

        const response =
            await fetch(
                `/api/usuarios/${id}`
            );

        if (response.status === 401) {

            window.location.href =
                "/login";

            return;
        }

        if (!response.ok) {

            throw new Error(
                "No se pudo cargar el lector."
            );
        }

        const lector =
            await response.json();

        // Seguridad:
        // solo permitimos editar lectores
        if (
            Number(
                lector.idTipoUsuario
            ) !== 3
        ) {

            throw new Error(
                "El usuario seleccionado no es un lector."
            );
        }

        document.getElementById(
            "idLectorEditar"
        ).value =
            lector.idUsuario ?? "";

        document.getElementById(
            "dniLectorEditar"
        ).value =
            lector.dni ?? "";

        document.getElementById(
            "usuarioLectorEditar"
        ).value =
            lector.usuario ?? "";

        document.getElementById(
            "nombresLectorEditar"
        ).value =
            lector.nombres ?? "";

        document.getElementById(
            "apellidosLectorEditar"
        ).value =
            lector.apellidos ?? "";

        document.getElementById(
            "correoLectorEditar"
        ).value =
            lector.correoE ?? "";

        document.getElementById(
            "telefonoLectorEditar"
        ).value =
            lector.telefono ?? "";

        document.getElementById(
            "direccionLectorEditar"
        ).value =
            lector.direccion ?? "";

    } catch (error) {

        const mensaje =
            document.getElementById(
                "mensajeEditarLector"
            );

        if (mensaje) {

            mensaje.className =
                "form-message error";

            mensaje.textContent =
                error.message;
        }
    }
}


// =====================================================
// ACTUALIZAR LECTOR
// =====================================================

async function actualizarLector(event) {

    event.preventDefault();

    const id =
        document.getElementById(
            "idLectorEditar"
        ).value;

    const mensaje =
        document.getElementById(
            "mensajeEditarLector"
        );

    const datos = {

        dni:
            document.getElementById(
                "dniLectorEditar"
            ).value.trim(),

        usuario:
            document.getElementById(
                "usuarioLectorEditar"
            ).value.trim(),

        nombres:
            document.getElementById(
                "nombresLectorEditar"
            ).value.trim(),

        apellidos:
            document.getElementById(
                "apellidosLectorEditar"
            ).value.trim(),

        correoE:
            document.getElementById(
                "correoLectorEditar"
            ).value.trim(),

        telefono:
            document.getElementById(
                "telefonoLectorEditar"
            ).value.trim(),

        direccion:
            document.getElementById(
                "direccionLectorEditar"
            ).value.trim(),

        idTipoUsuario: 3
    };

    const nuevaContrasena =
        document.getElementById(
            "contrasenaLectorEditar"
        ).value;

    if (
        nuevaContrasena.trim() !== ""
    ) {

        datos.contrasena =
            nuevaContrasena;
    }

    try {

        const response =
            await fetch(
                `/api/usuarios/${id}`,
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(datos)
                }
            );

        const respuesta =
            await response.json();

        if (!response.ok) {

            throw new Error(
                respuesta.mensaje ||
                "No se pudo actualizar el lector."
            );
        }

        mensaje.className =
            "form-message success";

        mensaje.textContent =
            "Lector actualizado correctamente.";

        localStorage.removeItem(
            "lectorEditarId"
        );

        setTimeout(() => {

            window.location.href =
                "/lectores";

        }, 700);

    } catch (error) {

        mensaje.className =
            "form-message error";

        mensaje.textContent =
            error.message;
    }
}


// =====================================================
// CAMBIAR ESTADO DEL LECTOR
// =====================================================

async function cambiarEstadoLector() {

    if (!lectorSeleccionado) {
        return;
    }

    const confirmar =
        window.confirm(
            "¿Desea cambiar el estado del lector seleccionado?"
        );

    if (!confirmar) {
        return;
    }

    try {

        const response =
            await fetch(
                `/api/usuarios/${lectorSeleccionado}/estado`,
                {
                    method: "PATCH"
                }
            );

        if (!response.ok) {

            let mensajeError =
                "No se pudo cambiar el estado del lector.";

            try {

                const respuesta =
                    await response.json();

                if (respuesta.mensaje) {
                    mensajeError =
                        respuesta.mensaje;
                }

            } catch (error) {
                // Se mantiene el mensaje por defecto
            }

            throw new Error(
                mensajeError
            );
        }

        lectorSeleccionado = null;

        await cargarLectores();

    } catch (error) {

        const mensaje =
            document.getElementById(
                "mensajeLectores"
            );

        if (mensaje) {
            mensaje.textContent =
                error.message;
        }
    }
}

// =====================================================
// MÓDULO AUTORES
// =====================================================

let autorSeleccionado = null;
let autoresCargados = [];


// =====================================================
// INICIALIZACIÓN AUTORES
// =====================================================

document.addEventListener("DOMContentLoaded", () => {

    const tablaAutores =
        document.getElementById("tablaAutores");

    const buscarAutor =
        document.getElementById("buscarAutor");

    const btnBuscarAutor =
        document.getElementById("btnBuscarAutor");

    const btnLimpiarBusquedaAutor =
        document.getElementById(
            "btnLimpiarBusquedaAutor"
        );


    // LISTADO
    if (tablaAutores) {

        cargarAutores();

        if (btnBuscarAutor) {

            btnBuscarAutor.addEventListener(
                "click",
                () => {

                    buscarAutores(
                        buscarAutor.value.trim()
                    );

                }
            );
        }

        if (buscarAutor) {

            buscarAutor.addEventListener(
                "keydown",
                (event) => {

                    if (event.key === "Enter") {

                        buscarAutores(
                            buscarAutor.value.trim()
                        );

                    }

                }
            );
        }

        if (btnLimpiarBusquedaAutor) {

            btnLimpiarBusquedaAutor.addEventListener(
                "click",
                () => {

                    buscarAutor.value = "";

                    mostrarAutores(
                        autoresCargados
                    );

                }
            );
        }
    }


    // AGREGAR
    const btnAgregarAutor =
        document.getElementById(
            "btnAgregarAutor"
        );

    if (btnAgregarAutor) {

        btnAgregarAutor.addEventListener(
            "click",
            () => {

                window.location.href =
                    "/autores/nuevo";

            }
        );
    }


    // EDITAR
    const btnEditarAutor =
        document.getElementById(
            "btnEditarAutor"
        );

    if (btnEditarAutor) {

        btnEditarAutor.addEventListener(
            "click",
            () => {

                if (!autorSeleccionado) {
                    return;
                }

                localStorage.setItem(
                    "autorEditarId",
                    autorSeleccionado
                );

                window.location.href =
                    "/autores/editar";

            }
        );
    }


    // CAMBIAR ESTADO
    const btnEstadoAutor =
        document.getElementById(
            "btnEstadoAutor"
        );

    if (btnEstadoAutor) {

        btnEstadoAutor.addEventListener(
            "click",
            cambiarEstadoAutor
        );
    }


    // NUEVO AUTOR
    const formNuevoAutor =
        document.getElementById(
            "formNuevoAutor"
        );

    if (formNuevoAutor) {

        formNuevoAutor.addEventListener(
            "submit",
            registrarAutor
        );
    }


    // EDITAR AUTOR
    const formEditarAutor =
        document.getElementById(
            "formEditarAutor"
        );

    if (formEditarAutor) {

        cargarAutorEditar();

        formEditarAutor.addEventListener(
            "submit",
            actualizarAutor
        );
    }

});


// =====================================================
// CARGAR AUTORES
// =====================================================

async function cargarAutores() {

    const tabla =
        document.getElementById(
            "tablaAutores"
        );

    const mensaje =
        document.getElementById(
            "mensajeAutores"
        );

    if (!tabla) {
        return;
    }

    autorSeleccionado = null;

    deshabilitarBotonesAutor();

    tabla.innerHTML = `
        <tr>
            <td colspan="5">
                Cargando autores...
            </td>
        </tr>
    `;

    try {

        const response =
            await fetch("/api/autores");

        if (response.status === 401) {

            window.location.href =
                "/login";

            return;
        }

        if (!response.ok) {

            throw new Error(
                "No se pudieron obtener los autores."
            );
        }

        autoresCargados =
            await response.json();

        mostrarAutores(
            autoresCargados
        );

        if (mensaje) {
            mensaje.textContent = "";
        }

    } catch (error) {

        tabla.innerHTML = `
            <tr>
                <td colspan="5">
                    No se pudieron cargar los autores.
                </td>
            </tr>
        `;

        if (mensaje) {
            mensaje.textContent =
                error.message;
        }

        console.error(error);
    }
}


// =====================================================
// MOSTRAR AUTORES
// =====================================================

function mostrarAutores(autores) {

    const tabla =
        document.getElementById(
            "tablaAutores"
        );

    if (!tabla) {
        return;
    }

    autorSeleccionado = null;

    deshabilitarBotonesAutor();

    if (
        !autores ||
        autores.length === 0
    ) {

        tabla.innerHTML = `
            <tr>
                <td colspan="5">
                    No se encontraron autores.
                </td>
            </tr>
        `;

        return;
    }

    tabla.innerHTML = "";

    autores.forEach(autor => {

        const fila =
            document.createElement("tr");

        const estado =
            autor.estado
                ? '<span class="estado-activo">Activo</span>'
                : '<span class="estado-inactivo">Inactivo</span>';

        fila.innerHTML = `

            <td>
                <input
                    type="radio"
                    name="autorSeleccionado"
                    value="${autor.idAutor}">
            </td>

            <td>
                ${autor.idAutor ?? ""}
            </td>

            <td>
                ${autor.nombres ?? ""}
            </td>

            <td>
                ${autor.apellidos ?? ""}
            </td>

            <td>
                ${estado}
            </td>
        `;

        tabla.appendChild(fila);

    });

    configurarSeleccionAutor();
}


// =====================================================
// BUSCAR AUTORES
// =====================================================

function buscarAutores(texto) {

    if (!texto) {

        mostrarAutores(
            autoresCargados
        );

        return;
    }

    const busqueda =
        texto.toLowerCase();

    const resultado =
        autoresCargados.filter(
            autor => {

                const nombres =
                    (autor.nombres ?? "")
                        .toLowerCase();

                const apellidos =
                    (autor.apellidos ?? "")
                        .toLowerCase();

                const nombreCompleto =
                    `${nombres} ${apellidos}`;

                return (
                    nombres.includes(busqueda) ||
                    apellidos.includes(busqueda) ||
                    nombreCompleto.includes(busqueda)
                );

            }
        );

    mostrarAutores(resultado);
}


// =====================================================
// SELECCIONAR AUTOR
// =====================================================

function configurarSeleccionAutor() {

    const radios =
        document.querySelectorAll(
            'input[name="autorSeleccionado"]'
        );

    radios.forEach(radio => {

        radio.addEventListener(
            "change",
            () => {

                autorSeleccionado =
                    Number(radio.value);

                const btnEditar =
                    document.getElementById(
                        "btnEditarAutor"
                    );

                const btnEstado =
                    document.getElementById(
                        "btnEstadoAutor"
                    );

                if (btnEditar) {
                    btnEditar.disabled = false;
                }

                if (btnEstado) {
                    btnEstado.disabled = false;
                }

            }
        );

    });
}


function deshabilitarBotonesAutor() {

    const btnEditar =
        document.getElementById(
            "btnEditarAutor"
        );

    const btnEstado =
        document.getElementById(
            "btnEstadoAutor"
        );

    if (btnEditar) {
        btnEditar.disabled = true;
    }

    if (btnEstado) {
        btnEstado.disabled = true;
    }
}


// =====================================================
// REGISTRAR AUTOR
// =====================================================

async function registrarAutor(event) {

    event.preventDefault();

    const mensaje =
        document.getElementById(
            "mensajeAutorFormulario"
        );

    const datos = {

        nombres:
            document.getElementById(
                "nombresAutor"
            ).value.trim(),

        apellidos:
            document.getElementById(
                "apellidosAutor"
            ).value.trim(),

        estado: true
    };

    try {

        const response =
            await fetch(
                "/api/autores",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(datos)
                }
            );

        const respuesta =
            await response.json();

        if (!response.ok) {

            throw new Error(
                respuesta.mensaje ||
                "No se pudo registrar el autor."
            );
        }

        mensaje.className =
            "form-message success";

        mensaje.textContent =
            "Autor registrado correctamente.";

        setTimeout(() => {

            window.location.href =
                "/autores";

        }, 700);

    } catch (error) {

        mensaje.className =
            "form-message error";

        mensaje.textContent =
            error.message;
    }
}


// =====================================================
// CARGAR AUTOR PARA EDITAR
// =====================================================

async function cargarAutorEditar() {

    const id =
        localStorage.getItem(
            "autorEditarId"
        );

    if (!id) {

        window.location.href =
            "/autores";

        return;
    }

    try {

        const response =
            await fetch(
                `/api/autores/${id}`
            );

        if (!response.ok) {

            throw new Error(
                "No se pudo cargar el autor."
            );
        }

        const autor =
            await response.json();

        document.getElementById(
            "idAutorEditar"
        ).value =
            autor.idAutor ?? "";

        document.getElementById(
            "nombresAutorEditar"
        ).value =
            autor.nombres ?? "";

        document.getElementById(
            "apellidosAutorEditar"
        ).value =
            autor.apellidos ?? "";

    } catch (error) {

        const mensaje =
            document.getElementById(
                "mensajeEditarAutor"
            );

        mensaje.className =
            "form-message error";

        mensaje.textContent =
            error.message;
    }
}


// =====================================================
// ACTUALIZAR AUTOR
// =====================================================

async function actualizarAutor(event) {

    event.preventDefault();

    const id =
        document.getElementById(
            "idAutorEditar"
        ).value;

    const mensaje =
        document.getElementById(
            "mensajeEditarAutor"
        );

    try {

        // Primero recuperamos el estado actual
        const actualResponse =
            await fetch(
                `/api/autores/${id}`
            );

        if (!actualResponse.ok) {

            throw new Error(
                "No se pudo obtener el autor."
            );
        }

        const autorActual =
            await actualResponse.json();

        const datos = {

            nombres:
                document.getElementById(
                    "nombresAutorEditar"
                ).value.trim(),

            apellidos:
                document.getElementById(
                    "apellidosAutorEditar"
                ).value.trim(),

            estado:
            autorActual.estado
        };


        const response =
            await fetch(
                `/api/autores/${id}`,
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(datos)
                }
            );

        const respuesta =
            await response.json();

        if (!response.ok) {

            throw new Error(
                respuesta.mensaje ||
                "No se pudo actualizar el autor."
            );
        }

        mensaje.className =
            "form-message success";

        mensaje.textContent =
            "Autor actualizado correctamente.";

        localStorage.removeItem(
            "autorEditarId"
        );

        setTimeout(() => {

            window.location.href =
                "/autores";

        }, 700);

    } catch (error) {

        mensaje.className =
            "form-message error";

        mensaje.textContent =
            error.message;
    }
}


// =====================================================
// CAMBIAR ESTADO AUTOR
// =====================================================

async function cambiarEstadoAutor() {

    if (!autorSeleccionado) {
        return;
    }

    const confirmar =
        window.confirm(
            "¿Desea cambiar el estado del autor seleccionado?"
        );

    if (!confirmar) {
        return;
    }

    try {

        // Recuperamos primero los datos actuales
        const responseActual =
            await fetch(
                `/api/autores/${autorSeleccionado}`
            );

        if (!responseActual.ok) {

            throw new Error(
                "No se pudo obtener el autor."
            );
        }

        const autor =
            await responseActual.json();


        // Como el backend no tiene PATCH,
        // usamos PUT conservando sus datos.
        const datos = {

            nombres:
            autor.nombres,

            apellidos:
            autor.apellidos,

            estado:
                !autor.estado
        };


        const response =
            await fetch(
                `/api/autores/${autorSeleccionado}`,
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(datos)
                }
            );

        if (!response.ok) {

            let mensajeError =
                "No se pudo cambiar el estado del autor.";

            try {

                const respuesta =
                    await response.json();

                if (respuesta.mensaje) {
                    mensajeError =
                        respuesta.mensaje;
                }

            } catch (error) {
                // Mantener mensaje por defecto
            }

            throw new Error(
                mensajeError
            );
        }

        autorSeleccionado = null;

        await cargarAutores();

    } catch (error) {

        const mensaje =
            document.getElementById(
                "mensajeAutores"
            );

        if (mensaje) {
            mensaje.textContent =
                error.message;
        }
    }
}
// =====================================================
// MÓDULO EDITORIALES
// =====================================================

let editorialSeleccionada = null;
let editorialesCargadas = [];


document.addEventListener(
    "DOMContentLoaded",
    () => {

        const tablaEditoriales =
            document.getElementById(
                "tablaEditoriales"
            );

        const buscarEditorial =
            document.getElementById(
                "buscarEditorial"
            );

        const btnBuscarEditorial =
            document.getElementById(
                "btnBuscarEditorial"
            );

        const btnLimpiarBusquedaEditorial =
            document.getElementById(
                "btnLimpiarBusquedaEditorial"
            );


        // LISTADO
        if (tablaEditoriales) {

            cargarEditoriales();

            if (btnBuscarEditorial) {

                btnBuscarEditorial.addEventListener(
                    "click",
                    () => {

                        buscarEditoriales(
                            buscarEditorial
                                .value
                                .trim()
                        );

                    }
                );
            }


            if (buscarEditorial) {

                buscarEditorial.addEventListener(
                    "keydown",
                    (event) => {

                        if (
                            event.key === "Enter"
                        ) {

                            buscarEditoriales(
                                buscarEditorial
                                    .value
                                    .trim()
                            );

                        }

                    }
                );
            }


            if (
                btnLimpiarBusquedaEditorial
            ) {

                btnLimpiarBusquedaEditorial
                    .addEventListener(
                        "click",
                        () => {

                            buscarEditorial.value =
                                "";

                            mostrarEditoriales(
                                editorialesCargadas
                            );

                        }
                    );
            }
        }


        // AGREGAR
        const btnAgregarEditorial =
            document.getElementById(
                "btnAgregarEditorial"
            );

        if (btnAgregarEditorial) {

            btnAgregarEditorial
                .addEventListener(
                    "click",
                    () => {

                        window.location.href =
                            "/editoriales/nuevo";

                    }
                );
        }


        // EDITAR
        const btnEditarEditorial =
            document.getElementById(
                "btnEditarEditorial"
            );

        if (btnEditarEditorial) {

            btnEditarEditorial
                .addEventListener(
                    "click",
                    () => {

                        if (
                            !editorialSeleccionada
                        ) {
                            return;
                        }

                        localStorage.setItem(
                            "editorialEditarId",
                            editorialSeleccionada
                        );

                        window.location.href =
                            "/editoriales/editar";

                    }
                );
        }


        // CAMBIAR ESTADO
        const btnEstadoEditorial =
            document.getElementById(
                "btnEstadoEditorial"
            );

        if (btnEstadoEditorial) {

            btnEstadoEditorial
                .addEventListener(
                    "click",
                    cambiarEstadoEditorial
                );
        }


        // FORMULARIO NUEVO
        const formNuevaEditorial =
            document.getElementById(
                "formNuevaEditorial"
            );

        if (formNuevaEditorial) {

            formNuevaEditorial
                .addEventListener(
                    "submit",
                    registrarEditorial
                );
        }


        // FORMULARIO EDITAR
        const formEditarEditorial =
            document.getElementById(
                "formEditarEditorial"
            );

        if (formEditarEditorial) {

            cargarEditorialEditar();

            formEditarEditorial
                .addEventListener(
                    "submit",
                    actualizarEditorial
                );
        }

    }
);


// =====================================================
// CARGAR EDITORIALES
// =====================================================

async function cargarEditoriales() {

    const tabla =
        document.getElementById(
            "tablaEditoriales"
        );

    const mensaje =
        document.getElementById(
            "mensajeEditoriales"
        );

    if (!tabla) {
        return;
    }

    editorialSeleccionada = null;

    deshabilitarBotonesEditorial();

    tabla.innerHTML = `
        <tr>
            <td colspan="4">
                Cargando editoriales...
            </td>
        </tr>
    `;


    try {

        const response =
            await fetch(
                "/api/editoriales"
            );


        if (response.status === 401) {

            window.location.href =
                "/login";

            return;
        }


        if (!response.ok) {

            throw new Error(
                "No se pudieron obtener las editoriales."
            );
        }


        editorialesCargadas =
            await response.json();


        mostrarEditoriales(
            editorialesCargadas
        );


        if (mensaje) {

            mensaje.textContent =
                "";

        }

    } catch (error) {

        tabla.innerHTML = `
            <tr>
                <td colspan="4">
                    No se pudieron cargar las editoriales.
                </td>
            </tr>
        `;

        if (mensaje) {

            mensaje.textContent =
                error.message;

        }

        console.error(error);
    }
}


// =====================================================
// MOSTRAR EDITORIALES
// =====================================================

function mostrarEditoriales(
    editoriales
) {

    const tabla =
        document.getElementById(
            "tablaEditoriales"
        );

    if (!tabla) {
        return;
    }

    editorialSeleccionada = null;

    deshabilitarBotonesEditorial();


    if (
        !editoriales ||
        editoriales.length === 0
    ) {

        tabla.innerHTML = `
            <tr>
                <td colspan="4">
                    No se encontraron editoriales.
                </td>
            </tr>
        `;

        return;
    }


    tabla.innerHTML = "";


    editoriales.forEach(
        editorial => {

            const fila =
                document.createElement(
                    "tr"
                );

            const estado =
                editorial.estado
                    ? '<span class="estado-activo">Activo</span>'
                    : '<span class="estado-inactivo">Inactivo</span>';


            fila.innerHTML = `

                <td>
                    <input
                        type="radio"
                        name="editorialSeleccionada"
                        value="${editorial.idEditorial}">
                </td>

                <td>
                    ${editorial.idEditorial ?? ""}
                </td>

                <td>
                    ${editorial.nombre ?? ""}
                </td>

                <td>
                    ${estado}
                </td>
            `;


            tabla.appendChild(
                fila
            );

        }
    );


    configurarSeleccionEditorial();
}


// =====================================================
// BUSCAR EDITORIALES
// =====================================================

function buscarEditoriales(
    texto
) {

    if (!texto) {

        mostrarEditoriales(
            editorialesCargadas
        );

        return;
    }


    const busqueda =
        texto.toLowerCase();


    const resultado =
        editorialesCargadas
            .filter(
                editorial => {

                    const nombre =
                        (
                            editorial.nombre ??
                            ""
                        ).toLowerCase();

                    return nombre.includes(
                        busqueda
                    );

                }
            );


    mostrarEditoriales(
        resultado
    );
}


// =====================================================
// SELECCIONAR EDITORIAL
// =====================================================

function configurarSeleccionEditorial() {

    const radios =
        document.querySelectorAll(
            'input[name="editorialSeleccionada"]'
        );


    radios.forEach(
        radio => {

            radio.addEventListener(
                "change",
                () => {

                    editorialSeleccionada =
                        Number(
                            radio.value
                        );


                    const btnEditar =
                        document.getElementById(
                            "btnEditarEditorial"
                        );

                    const btnEstado =
                        document.getElementById(
                            "btnEstadoEditorial"
                        );


                    if (btnEditar) {

                        btnEditar.disabled =
                            false;

                    }


                    if (btnEstado) {

                        btnEstado.disabled =
                            false;

                    }

                }
            );

        }
    );
}


// =====================================================
// DESHABILITAR BOTONES
// =====================================================

function deshabilitarBotonesEditorial() {

    const btnEditar =
        document.getElementById(
            "btnEditarEditorial"
        );

    const btnEstado =
        document.getElementById(
            "btnEstadoEditorial"
        );


    if (btnEditar) {

        btnEditar.disabled =
            true;

    }


    if (btnEstado) {

        btnEstado.disabled =
            true;

    }
}


// =====================================================
// REGISTRAR EDITORIAL
// =====================================================

async function registrarEditorial(
    event
) {

    event.preventDefault();


    const mensaje =
        document.getElementById(
            "mensajeEditorialFormulario"
        );


    const datos = {

        nombre:
            document.getElementById(
                "nombreEditorial"
            ).value.trim(),

        estado: true
    };


    try {

        const response =
            await fetch(
                "/api/editoriales",
                {

                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/json"

                    },

                    body:
                        JSON.stringify(
                            datos
                        )

                }
            );


        const respuesta =
            await response.json();


        if (!response.ok) {

            throw new Error(
                respuesta.mensaje ||
                "No se pudo registrar la editorial."
            );

        }


        mensaje.className =
            "form-message success";


        mensaje.textContent =
            "Editorial registrada correctamente.";


        setTimeout(
            () => {

                window.location.href =
                    "/editoriales";

            },
            700
        );

    } catch (error) {

        mensaje.className =
            "form-message error";


        mensaje.textContent =
            error.message;

    }
}


// =====================================================
// CARGAR EDITORIAL PARA EDITAR
// =====================================================

async function cargarEditorialEditar() {

    const id =
        localStorage.getItem(
            "editorialEditarId"
        );


    if (!id) {

        window.location.href =
            "/editoriales";

        return;
    }


    try {

        const response =
            await fetch(
                `/api/editoriales/${id}`
            );


        if (!response.ok) {

            throw new Error(
                "No se pudo cargar la editorial."
            );

        }


        const editorial =
            await response.json();


        document.getElementById(
            "idEditorialEditar"
        ).value =
            editorial.idEditorial ??
            "";


        document.getElementById(
            "nombreEditorialEditar"
        ).value =
            editorial.nombre ??
            "";


    } catch (error) {

        const mensaje =
            document.getElementById(
                "mensajeEditarEditorial"
            );


        if (mensaje) {

            mensaje.className =
                "form-message error";


            mensaje.textContent =
                error.message;

        }
    }
}


// =====================================================
// ACTUALIZAR EDITORIAL
// =====================================================

async function actualizarEditorial(
    event
) {

    event.preventDefault();


    const id =
        document.getElementById(
            "idEditorialEditar"
        ).value;


    const mensaje =
        document.getElementById(
            "mensajeEditarEditorial"
        );


    try {

        const actualResponse =
            await fetch(
                `/api/editoriales/${id}`
            );


        if (!actualResponse.ok) {

            throw new Error(
                "No se pudo obtener la editorial."
            );

        }


        const editorialActual =
            await actualResponse.json();


        const datos = {

            nombre:
                document.getElementById(
                    "nombreEditorialEditar"
                ).value.trim(),

            estado:
            editorialActual.estado

        };


        const response =
            await fetch(
                `/api/editoriales/${id}`,
                {

                    method: "PUT",

                    headers: {

                        "Content-Type":
                            "application/json"

                    },

                    body:
                        JSON.stringify(
                            datos
                        )

                }
            );


        const respuesta =
            await response.json();


        if (!response.ok) {

            throw new Error(
                respuesta.mensaje ||
                "No se pudo actualizar la editorial."
            );

        }


        mensaje.className =
            "form-message success";


        mensaje.textContent =
            "Editorial actualizada correctamente.";


        localStorage.removeItem(
            "editorialEditarId"
        );


        setTimeout(
            () => {

                window.location.href =
                    "/editoriales";

            },
            700
        );


    } catch (error) {

        mensaje.className =
            "form-message error";


        mensaje.textContent =
            error.message;

    }
}


// =====================================================
// CAMBIAR ESTADO EDITORIAL
// =====================================================

async function cambiarEstadoEditorial() {

    if (!editorialSeleccionada) {
        return;
    }


    const confirmar =
        window.confirm(
            "¿Desea cambiar el estado de la editorial seleccionada?"
        );


    if (!confirmar) {
        return;
    }


    try {

        const responseActual =
            await fetch(
                `/api/editoriales/${editorialSeleccionada}`
            );


        if (!responseActual.ok) {

            throw new Error(
                "No se pudo obtener la editorial."
            );

        }


        const editorial =
            await responseActual.json();


        const datos = {

            nombre:
            editorial.nombre,

            estado:
                !editorial.estado

        };


        const response =
            await fetch(
                `/api/editoriales/${editorialSeleccionada}`,
                {

                    method: "PUT",

                    headers: {

                        "Content-Type":
                            "application/json"

                    },

                    body:
                        JSON.stringify(
                            datos
                        )

                }
            );


        if (!response.ok) {

            let mensajeError =
                "No se pudo cambiar el estado de la editorial.";


            try {

                const respuesta =
                    await response.json();


                if (
                    respuesta.mensaje
                ) {

                    mensajeError =
                        respuesta.mensaje;

                }

            } catch (error) {

                // Mantener mensaje por defecto

            }


            throw new Error(
                mensajeError
            );

        }


        editorialSeleccionada =
            null;


        await cargarEditoriales();


    } catch (error) {

        const mensaje =
            document.getElementById(
                "mensajeEditoriales"
            );


        if (mensaje) {

            mensaje.textContent =
                error.message;

        }
    }
}
// =====================================================
// MÓDULO CATEGORÍAS
// =====================================================

let categoriaSeleccionada = null;
let categoriasCargadas = [];

document.addEventListener("DOMContentLoaded", () => {

    const tablaCategorias =
        document.getElementById("tablaCategorias");

    const buscarCategoria =
        document.getElementById("buscarCategoria");

    const btnBuscarCategoria =
        document.getElementById("btnBuscarCategoria");

    const btnLimpiarBusquedaCategoria =
        document.getElementById("btnLimpiarBusquedaCategoria");

    // LISTADO
    if (tablaCategorias) {

        cargarCategorias();

        if (btnBuscarCategoria) {
            btnBuscarCategoria.addEventListener(
                "click",
                () => {
                    buscarCategorias(
                        buscarCategoria.value.trim()
                    );
                }
            );
        }

        if (buscarCategoria) {
            buscarCategoria.addEventListener(
                "keydown",
                (event) => {

                    if (event.key === "Enter") {
                        buscarCategorias(
                            buscarCategoria.value.trim()
                        );
                    }

                }
            );
        }

        if (btnLimpiarBusquedaCategoria) {
            btnLimpiarBusquedaCategoria.addEventListener(
                "click",
                () => {

                    buscarCategoria.value = "";

                    mostrarCategorias(
                        categoriasCargadas
                    );

                }
            );
        }
    }

    // AGREGAR
    const btnAgregarCategoria =
        document.getElementById(
            "btnAgregarCategoria"
        );

    if (btnAgregarCategoria) {
        btnAgregarCategoria.addEventListener(
            "click",
            () => {
                window.location.href =
                    "/categorias/nuevo";
            }
        );
    }

    // EDITAR
    const btnEditarCategoria =
        document.getElementById(
            "btnEditarCategoria"
        );

    if (btnEditarCategoria) {
        btnEditarCategoria.addEventListener(
            "click",
            () => {

                if (!categoriaSeleccionada) {
                    return;
                }

                localStorage.setItem(
                    "categoriaEditarId",
                    categoriaSeleccionada
                );

                window.location.href =
                    "/categorias/editar";
            }
        );
    }

    // CAMBIAR ESTADO
    const btnEstadoCategoria =
        document.getElementById(
            "btnEstadoCategoria"
        );

    if (btnEstadoCategoria) {
        btnEstadoCategoria.addEventListener(
            "click",
            cambiarEstadoCategoria
        );
    }

    // NUEVA CATEGORÍA
    const formNuevaCategoria =
        document.getElementById(
            "formNuevaCategoria"
        );

    if (formNuevaCategoria) {
        formNuevaCategoria.addEventListener(
            "submit",
            registrarCategoria
        );
    }

    // EDITAR CATEGORÍA
    const formEditarCategoria =
        document.getElementById(
            "formEditarCategoria"
        );

    if (formEditarCategoria) {

        cargarCategoriaEditar();

        formEditarCategoria.addEventListener(
            "submit",
            actualizarCategoria
        );
    }

});


// =====================================================
// CARGAR CATEGORÍAS
// =====================================================

async function cargarCategorias() {

    const tabla =
        document.getElementById(
            "tablaCategorias"
        );

    const mensaje =
        document.getElementById(
            "mensajeCategorias"
        );

    if (!tabla) {
        return;
    }

    categoriaSeleccionada = null;

    deshabilitarBotonesCategoria();

    tabla.innerHTML = `
        <tr>
            <td colspan="6">
                Cargando categorías...
            </td>
        </tr>
    `;

    try {

        const response =
            await fetch("/api/categorias");

        if (response.status === 401) {

            window.location.href =
                "/login";

            return;
        }

        if (!response.ok) {
            throw new Error(
                "No se pudieron obtener las categorías."
            );
        }

        categoriasCargadas =
            await response.json();

        mostrarCategorias(
            categoriasCargadas
        );

        if (mensaje) {
            mensaje.textContent = "";
        }

    } catch (error) {

        tabla.innerHTML = `
            <tr>
                <td colspan="6">
                    No se pudieron cargar las categorías.
                </td>
            </tr>
        `;

        if (mensaje) {
            mensaje.textContent =
                error.message;
        }

        console.error(error);
    }
}


// =====================================================
// MOSTRAR CATEGORÍAS
// =====================================================

function mostrarCategorias(categorias) {

    const tabla =
        document.getElementById(
            "tablaCategorias"
        );

    if (!tabla) {
        return;
    }

    categoriaSeleccionada = null;

    deshabilitarBotonesCategoria();

    if (!categorias || categorias.length === 0) {

        tabla.innerHTML = `
            <tr>
                <td colspan="6">
                    No se encontraron categorías.
                </td>
            </tr>
        `;

        return;
    }

    tabla.innerHTML = "";

    categorias.forEach(categoria => {

        const fila =
            document.createElement("tr");

        const estado =
            categoria.estado
                ? '<span class="estado-activo">Activo</span>'
                : '<span class="estado-inactivo">Inactivo</span>';

        const imagen =
            categoria.imagen
                ? `
                    <img
                        src="${categoria.imagen}"
                        alt="${categoria.nombre ?? "Categoría"}"
                        class="categoria-imagen"
                        onerror="
                            this.style.display='none';
                            this.nextElementSibling.style.display='inline';
                        ">

                    <span
                        class="categoria-sin-imagen"
                        style="display:none;">
                        Sin imagen
                    </span>
                `
                : `
                    <span class="categoria-sin-imagen">
                        Sin imagen
                    </span>
                `;

        fila.innerHTML = `

            <td>
                <input
                    type="radio"
                    name="categoriaSeleccionada"
                    value="${categoria.idCategoria}">
            </td>

            <td>
                ${categoria.idCategoria ?? ""}
            </td>

            <td class="categoria-imagen-celda">
                ${imagen}
            </td>

            <td>
                ${categoria.nombre ?? ""}
            </td>

            <td>
                ${categoria.descripcion ?? ""}
            </td>

            <td>
                ${estado}
            </td>
        `;

        tabla.appendChild(fila);

    });

    configurarSeleccionCategoria();
}


// =====================================================
// BUSCAR CATEGORÍAS
// =====================================================

function buscarCategorias(texto) {

    if (!texto) {

        mostrarCategorias(
            categoriasCargadas
        );

        return;
    }

    const busqueda =
        texto.toLowerCase();

    const resultado =
        categoriasCargadas.filter(
            categoria => {

                const nombre =
                    (categoria.nombre ?? "")
                        .toLowerCase();

                const descripcion =
                    (categoria.descripcion ?? "")
                        .toLowerCase();

                return (
                    nombre.includes(busqueda) ||
                    descripcion.includes(busqueda)
                );

            }
        );

    mostrarCategorias(resultado);
}


// =====================================================
// SELECCIONAR CATEGORÍA
// =====================================================

function configurarSeleccionCategoria() {

    const radios =
        document.querySelectorAll(
            'input[name="categoriaSeleccionada"]'
        );

    radios.forEach(radio => {

        radio.addEventListener(
            "change",
            () => {

                categoriaSeleccionada =
                    Number(radio.value);

                const btnEditar =
                    document.getElementById(
                        "btnEditarCategoria"
                    );

                const btnEstado =
                    document.getElementById(
                        "btnEstadoCategoria"
                    );

                if (btnEditar) {
                    btnEditar.disabled = false;
                }

                if (btnEstado) {
                    btnEstado.disabled = false;
                }

            }
        );

    });
}


// =====================================================
// DESHABILITAR BOTONES
// =====================================================

function deshabilitarBotonesCategoria() {

    const btnEditar =
        document.getElementById(
            "btnEditarCategoria"
        );

    const btnEstado =
        document.getElementById(
            "btnEstadoCategoria"
        );

    if (btnEditar) {
        btnEditar.disabled = true;
    }

    if (btnEstado) {
        btnEstado.disabled = true;
    }
}


// =====================================================
// REGISTRAR CATEGORÍA
// =====================================================

async function registrarCategoria(event) {

    event.preventDefault();

    const mensaje =
        document.getElementById(
            "mensajeCategoriaFormulario"
        );

    const datos = {

        nombre:
            document.getElementById(
                "nombreCategoria"
            ).value.trim(),

        descripcion:
            document.getElementById(
                "descripcionCategoria"
            ).value.trim(),

        imagen:
            document.getElementById(
                "imagenCategoria"
            ).value.trim(),

        estado: true
    };

    try {

        const response =
            await fetch(
                "/api/categorias",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(datos)
                }
            );

        const respuesta =
            await response.json();

        if (!response.ok) {
            throw new Error(
                respuesta.mensaje ||
                "No se pudo registrar la categoría."
            );
        }

        mensaje.className =
            "form-message success";

        mensaje.textContent =
            "Categoría registrada correctamente.";

        setTimeout(() => {

            window.location.href =
                "/categorias";

        }, 700);

    } catch (error) {

        mensaje.className =
            "form-message error";

        mensaje.textContent =
            error.message;
    }
}


// =====================================================
// CARGAR CATEGORÍA PARA EDITAR
// =====================================================

async function cargarCategoriaEditar() {

    const id =
        localStorage.getItem(
            "categoriaEditarId"
        );

    if (!id) {

        window.location.href =
            "/categorias";

        return;
    }

    try {

        const response =
            await fetch(
                `/api/categorias/${id}`
            );

        if (!response.ok) {

            throw new Error(
                "No se pudo cargar la categoría."
            );
        }

        const categoria =
            await response.json();


        // ID
        document.getElementById(
            "idCategoriaEditar"
        ).value =
            categoria.idCategoria ?? "";


        // NOMBRE
        document.getElementById(
            "nombreCategoriaEditar"
        ).value =
            categoria.nombre ?? "";


        // DESCRIPCIÓN
        document.getElementById(
            "descripcionCategoriaEditar"
        ).value =
            categoria.descripcion ?? "";


        // IMAGEN
        const inputImagen =
            document.getElementById(
                "imagenCategoriaEditar"
            );

        inputImagen.value =
            categoria.imagen ?? "";


        // =========================
        // VISTA PREVIA
        // =========================

        const preview =
            document.getElementById(
                "previewCategoriaEditar"
            );

        const textoPreview =
            document.getElementById(
                "previewCategoriaEditarTexto"
            );


        if (categoria.imagen) {

            preview.src =
                categoria.imagen;

            preview.style.display =
                "block";

            textoPreview.style.display =
                "none";


            preview.onload = () => {

                preview.style.display =
                    "block";

                textoPreview.style.display =
                    "none";

            };


            preview.onerror = () => {

                preview.style.display =
                    "none";

                textoPreview.style.display =
                    "inline";

                textoPreview.textContent =
                    "No se pudo cargar la imagen";

            };

        } else {

            preview.style.display =
                "none";

            textoPreview.style.display =
                "inline";

            textoPreview.textContent =
                "Sin imagen";
        }


        // =========================
        // ACTUALIZAR PREVIEW
        // AL CAMBIAR LA URL
        // =========================

        inputImagen.addEventListener(
            "input",
            () => {

                const url =
                    inputImagen.value.trim();

                if (!url) {

                    preview.style.display =
                        "none";

                    textoPreview.style.display =
                        "inline";

                    textoPreview.textContent =
                        "Sin imagen";

                    return;
                }


                preview.src =
                    url;

                preview.style.display =
                    "block";

                textoPreview.style.display =
                    "none";


                preview.onload = () => {

                    preview.style.display =
                        "block";

                    textoPreview.style.display =
                        "none";

                };


                preview.onerror = () => {

                    preview.style.display =
                        "none";

                    textoPreview.style.display =
                        "inline";

                    textoPreview.textContent =
                        "No se pudo cargar la imagen";

                };

            }
        );

    } catch (error) {

        const mensaje =
            document.getElementById(
                "mensajeEditarCategoria"
            );

        if (mensaje) {

            mensaje.className =
                "form-message error";

            mensaje.textContent =
                error.message;
        }
    }
}


// =====================================================
// ACTUALIZAR CATEGORÍA
// =====================================================

async function actualizarCategoria(event) {

    event.preventDefault();

    const id =
        document.getElementById(
            "idCategoriaEditar"
        ).value;

    const mensaje =
        document.getElementById(
            "mensajeEditarCategoria"
        );

    try {

        const actualResponse =
            await fetch(
                `/api/categorias/${id}`
            );

        if (!actualResponse.ok) {
            throw new Error(
                "No se pudo obtener la categoría."
            );
        }

        const categoriaActual =
            await actualResponse.json();

        const datos = {

            nombre:
                document.getElementById(
                    "nombreCategoriaEditar"
                ).value.trim(),

            descripcion:
                document.getElementById(
                    "descripcionCategoriaEditar"
                ).value.trim(),

            imagen:
                document.getElementById(
                    "imagenCategoriaEditar"
                ).value.trim(),

            estado:
            categoriaActual.estado
        };

        const response =
            await fetch(
                `/api/categorias/${id}`,
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(datos)
                }
            );

        const respuesta =
            await response.json();

        if (!response.ok) {
            throw new Error(
                respuesta.mensaje ||
                "No se pudo actualizar la categoría."
            );
        }

        mensaje.className =
            "form-message success";

        mensaje.textContent =
            "Categoría actualizada correctamente.";

        localStorage.removeItem(
            "categoriaEditarId"
        );

        setTimeout(() => {

            window.location.href =
                "/categorias";

        }, 700);

    } catch (error) {

        mensaje.className =
            "form-message error";

        mensaje.textContent =
            error.message;
    }
}


// =====================================================
// CAMBIAR ESTADO CATEGORÍA
// =====================================================

async function cambiarEstadoCategoria() {

    if (!categoriaSeleccionada) {
        return;
    }

    const confirmar =
        window.confirm(
            "¿Desea cambiar el estado de la categoría seleccionada?"
        );

    if (!confirmar) {
        return;
    }

    try {

        const responseActual =
            await fetch(
                `/api/categorias/${categoriaSeleccionada}`
            );

        if (!responseActual.ok) {
            throw new Error(
                "No se pudo obtener la categoría."
            );
        }

        const categoria =
            await responseActual.json();

        const datos = {

            nombre:
            categoria.nombre,

            descripcion:
            categoria.descripcion,

            imagen:
            categoria.imagen,

            estado:
                !categoria.estado
        };

        const response =
            await fetch(
                `/api/categorias/${categoriaSeleccionada}`,
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(datos)
                }
            );

        if (!response.ok) {

            let mensajeError =
                "No se pudo cambiar el estado de la categoría.";

            try {

                const respuesta =
                    await response.json();

                if (respuesta.mensaje) {
                    mensajeError =
                        respuesta.mensaje;
                }

            } catch (error) {
                // Mantener mensaje por defecto
            }

            throw new Error(
                mensajeError
            );
        }

        categoriaSeleccionada = null;

        await cargarCategorias();

    } catch (error) {

        const mensaje =
            document.getElementById(
                "mensajeCategorias"
            );

        if (mensaje) {
            mensaje.textContent =
                error.message;
        }
    }
}

// =====================================================
// VISTA PREVIA DE IMAGEN - CATEGORÍAS
// =====================================================

document.addEventListener("DOMContentLoaded", () => {

    const inputImagenNueva =
        document.getElementById(
            "imagenCategoria"
        );

    const previewNueva =
        document.getElementById(
            "previewCategoriaNueva"
        );

    const textoPreviewNueva =
        document.getElementById(
            "previewCategoriaNuevaTexto"
        );


    if (
        inputImagenNueva &&
        previewNueva &&
        textoPreviewNueva
    ) {

        inputImagenNueva.addEventListener(
            "input",
            () => {

                actualizarPreviewCategoria(
                    inputImagenNueva.value.trim(),
                    previewNueva,
                    textoPreviewNueva
                );

            }
        );

    }


    const inputImagenEditar =
        document.getElementById(
            "imagenCategoriaEditar"
        );

    const previewEditar =
        document.getElementById(
            "previewCategoriaEditar"
        );

    const textoPreviewEditar =
        document.getElementById(
            "previewCategoriaEditarTexto"
        );


    if (
        inputImagenEditar &&
        previewEditar &&
        textoPreviewEditar
    ) {

        inputImagenEditar.addEventListener(
            "input",
            () => {

                actualizarPreviewCategoria(
                    inputImagenEditar.value.trim(),
                    previewEditar,
                    textoPreviewEditar
                );

            }
        );

    }

});


// =====================================================
// FUNCIÓN PREVIEW CATEGORÍA
// =====================================================

function actualizarPreviewCategoria(
    url,
    imagen,
    texto
) {

    if (!url) {

        imagen.style.display =
            "none";

        imagen.removeAttribute(
            "src"
        );

        texto.style.display =
            "inline";

        texto.textContent =
            "Sin imagen";

        return;
    }

    texto.style.display =
        "none";

    imagen.style.display =
        "block";

    imagen.src =
        url;


    imagen.onload =
        () => {

            imagen.style.display =
                "block";

            texto.style.display =
                "none";

        };


    imagen.onerror =
        () => {

            imagen.style.display =
                "none";

            texto.style.display =
                "inline";

            texto.textContent =
                "No se pudo cargar la imagen";

        };

}
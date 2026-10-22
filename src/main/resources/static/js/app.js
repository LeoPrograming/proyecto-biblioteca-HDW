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
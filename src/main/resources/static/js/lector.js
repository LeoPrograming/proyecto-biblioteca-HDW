let categoriasLector = [];
let librosLector = [];

document.addEventListener(
    "DOMContentLoaded",
    () => {

        const contenedorCategorias =
            document.getElementById(
                "categoriasLector"
            );

        const contenedorLibros =
            document.getElementById(
                "librosCategoriaLector"
            );

        if (contenedorCategorias) {
            cargarCategoriasLector();
        }

        if (contenedorLibros) {
            cargarLibrosCategoriaLector();
        }

    }
);


// =====================================================
// CATEGORÍAS
// =====================================================

async function cargarCategoriasLector() {

    const contenedor =
        document.getElementById(
            "categoriasLector"
        );

    try {

        const response =
            await fetch(
                "/api/categorias"
            );

        if (!response.ok) {

            throw new Error(
                "No se pudieron cargar las categorías."
            );
        }

        categoriasLector =
            await response.json();

        const activas =
            categoriasLector.filter(
                categoria =>
                    categoria.estado === true
            );

        if (activas.length === 0) {

            contenedor.innerHTML = `
                <p>
                    No hay categorías disponibles.
                </p>
            `;

            return;
        }

        contenedor.innerHTML = "";

        activas.forEach(
            categoria => {

                const tarjeta =
                    document.createElement("a");

                tarjeta.href =
                    `/lector/catalogo/${categoria.idCategoria}`;

                tarjeta.className =
                    "module-card";

                tarjeta.style.textDecoration =
                    "none";

                tarjeta.style.minHeight =
                    "210px";

                tarjeta.innerHTML = `

                    <div class="module-icon"
                         style="font-size: 42px;">
                        📚
                    </div>

                    <strong style="
                        font-size: 18px;
                        margin-top: 12px;
                    ">
                        ${categoria.nombre ?? "Categoría"}
                    </strong>

                    <span style="
                        margin-top: 8px;
                        text-align: center;
                    ">
                        ${categoria.descripcion ?? ""}
                    </span>

                    <span style="
                        margin-top: 15px;
                        font-weight: 600;
                    ">
                        Ver libros →
                    </span>
                `;

                contenedor.appendChild(
                    tarjeta
                );
            }
        );

    } catch (error) {

        contenedor.innerHTML = `
            <p>${error.message}</p>
        `;

        console.error(error);
    }
}


// =====================================================
// LIBROS POR CATEGORÍA
// =====================================================

async function cargarLibrosCategoriaLector() {

    const contenedor =
        document.getElementById(
            "librosCategoriaLector"
        );

    try {

        const partes =
            window.location.pathname.split("/");

        const idCategoria =
            Number(
                partes[
                partes.length - 1
                    ]
            );

        const [
            responseCategorias,
            responseLibros
        ] = await Promise.all([
            fetch("/api/categorias"),
            fetch("/api/libros")
        ]);

        if (!responseCategorias.ok ||
            !responseLibros.ok) {

            throw new Error(
                "No se pudo cargar el catálogo."
            );
        }

        categoriasLector =
            await responseCategorias.json();

        librosLector =
            await responseLibros.json();

        const categoria =
            categoriasLector.find(
                item =>
                    Number(item.idCategoria) ===
                    idCategoria
            );

        const titulo =
            document.getElementById(
                "tituloCategoria"
            );

        if (titulo && categoria) {

            titulo.textContent =
                `Libros - ${categoria.nombre}`;
        }

        const libros =
            librosLector.filter(
                libro =>
                    Number(libro.idCategoria) ===
                    idCategoria
            );

        if (libros.length === 0) {

            contenedor.innerHTML = `

        <div style="
            grid-column: 1 / -1;
            width: 100%;
            text-align: center;
            padding: 55px 20px;
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 14px rgba(0,0,0,0.08);
        ">

            <div style="
                font-size: 48px;
                margin-bottom: 15px;
            ">
                📚
            </div>

            <h3 style="
                margin-bottom: 8px;
                color: #17365d;
            ">
                No hay libros disponibles
            </h3>

            <p style="
                margin: 0;
                color: #666;
            ">
                Actualmente no existen libros registrados en esta categoría.
            </p>

        </div>
    `;

            return;
        }

        contenedor.innerHTML = "";

        libros.forEach(
            libro => {

                const disponible =
                    libro.estado === true;

                const autores =
                    libro.autores &&
                    libro.autores.length > 0

                        ? libro.autores
                            .map(
                                autor =>
                                    `${autor.nombres ?? ""} ${autor.apellidos ?? ""}`
                            )
                            .join(", ")

                        : "Sin autor";

                const tarjeta =
                    document.createElement("div");

                tarjeta.className =
                    "module-card";

                tarjeta.style.minHeight =
                    "290px";

                tarjeta.innerHTML = `

                    <div class="module-icon"
                         style="font-size: 46px;">
                        📖
                    </div>

                    <strong style="
                        font-size: 18px;
                        text-align: center;
                        margin-top: 10px;
                    ">
                        ${libro.titulo ?? ""}
                    </strong>

                    <span style="
                        margin-top: 8px;
                        text-align: center;
                    ">
                        ${autores}
                    </span>

                    <span style="
                        margin-top: 6px;
                    ">
                        ISBN:
                        ${libro.isbn ?? "Sin ISBN"}
                    </span>

                    <span style="
                        margin-top: 6px;
                        font-weight: 600;
                    ">
                        ${
                    disponible
                        ? "Disponible"
                        : "No disponible"
                }
                    </span>

                    <button
                        type="button"
                        class="btn ${
                    disponible
                        ? "btn-blue"
                        : "btn-yellow"
                }"
                        style="margin-top: 15px;">

                        ${
                    disponible
                        ? "Reservar"
                        : "Unirme a Cola"
                }

                    </button>
                `;

                const boton =
                    tarjeta.querySelector(
                        "button"
                    );

                boton.addEventListener(
                    "click",
                    () => {

                        if (disponible) {

                            window.location.href =
                                `/lector/reservar/${libro.idLibro}`;

                        } else {

                            window.location.href =
                                `/lector/cola/${libro.idLibro}`;
                        }
                    }
                );

                contenedor.appendChild(
                    tarjeta
                );
            }
        );

    } catch (error) {

        contenedor.innerHTML = `
            <p>${error.message}</p>
        `;

        console.error(error);
    }
}

// =====================================================
// RESERVA DEL LECTOR
// =====================================================

document.addEventListener(
    "DOMContentLoaded",
    () => {

        const formReserva =
            document.getElementById(
                "formReservaLector"
            );

        if (formReserva) {

            cargarLibroReserva();

            formReserva.addEventListener(
                "submit",
                registrarReservaLector
            );

            const dias =
                document.getElementById(
                    "diasReserva"
                );

            if (dias) {

                dias.addEventListener(
                    "input",
                    actualizarTotalReserva
                );
            }
        }

    }
);


let libroReservaActual = null;


async function cargarLibroReserva() {

    const partes =
        window.location.pathname.split("/");

    const idLibro =
        Number(
            partes[
            partes.length - 1
                ]
        );

    try {

        const response =
            await fetch(
                `/api/libros/${idLibro}`
            );

        if (!response.ok) {

            throw new Error(
                "No se pudo cargar el libro."
            );
        }

        libroReservaActual =
            await response.json();

        if (!libroReservaActual.estado) {

            throw new Error(
                "Este libro ya no se encuentra disponible."
            );
        }

        const contenedor =
            document.getElementById(
                "detalleLibroReserva"
            );

        contenedor.innerHTML = `

            <div style="
                text-align: center;
                padding: 20px;
            ">

                <div style="
                    font-size: 52px;
                    margin-bottom: 12px;
                ">
                    📖
                </div>

                <h2>
                    ${libroReservaActual.titulo ?? ""}
                </h2>

                <p>
                    ISBN:
                    ${libroReservaActual.isbn ?? ""}
                </p>

                <p>
                    Precio por día:
                    S/
                    ${Number(
            libroReservaActual.precioTiempo ?? 0
        ).toFixed(2)}
                </p>

            </div>
        `;

        actualizarTotalReserva();

    } catch (error) {

        const mensaje =
            document.getElementById(
                "mensajeReserva"
            );

        mensaje.className =
            "form-message error";

        mensaje.textContent =
            error.message;
    }
}


function actualizarTotalReserva() {

    if (!libroReservaActual) {
        return;
    }

    const dias =
        Number(
            document.getElementById(
                "diasReserva"
            ).value
        );

    const precio =
        Number(
            libroReservaActual.precioTiempo ?? 0
        );

    const total =
        dias * precio;

    document.getElementById(
        "totalReserva"
    ).textContent =
        `Total: S/ ${total.toFixed(2)}`;
}


async function registrarReservaLector(event) {

    event.preventDefault();

    const mensaje =
        document.getElementById(
            "mensajeReserva"
        );

    const sesionGuardada =
        localStorage.getItem(
            "bibliotecaSesion"
        );

    if (!sesionGuardada) {

        mensaje.className =
            "form-message error";

        mensaje.textContent =
            "No se encontró la sesión del lector.";

        return;
    }

    let sesion;

    try {
        sesion =
            JSON.parse(
                sesionGuardada
            );
    } catch (error) {

        mensaje.className =
            "form-message error";

        mensaje.textContent =
            "La sesión no es válida.";

        return;
    }

    const dias =
        Number(
            document.getElementById(
                "diasReserva"
            ).value
        );

    if (!dias || dias <= 0) {

        mensaje.className =
            "form-message error";

        mensaje.textContent =
            "La cantidad de días debe ser mayor a cero.";

        return;
    }

    const datos = {

        idUsuario:
        sesion.idUsuario,

        idComprobante:
            1,

        idsLibros: [
            libroReservaActual.idLibro
        ],

        dias: [
            dias
        ],

        observaciones:
            "Reserva realizada por el lector"
    };

    try {

        const response =
            await fetch(
                "/api/prestamos",
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
                "No se pudo registrar la reserva."
            );
        }
        const idCola =
            localStorage.getItem(
                "colaReservaId"
            );

        if (idCola) {

            try {

                await fetch(
                    `/api/cola-espera/${idCola}/atendido`,
                    {
                        method: "PUT"
                    }
                );

            } catch (errorCola) {

                console.error(
                    "No se pudo actualizar la cola:",
                    errorCola
                );
            }

            localStorage.removeItem(
                "colaReservaId"
            );
        }
        mensaje.className =
            "form-message success";

        mensaje.textContent =
            "Reserva registrada correctamente.";

        setTimeout(
            () => {

                window.location.href =
                    `/lector/reserva/resultado/${respuesta.idPrestamo}`;
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
// MIS PRÉSTAMOS
// =====================================================

document.addEventListener(
    "DOMContentLoaded",
    () => {

        const contenedor =
            document.getElementById(
                "listaPrestamosLector"
            );

        if (contenedor) {
            cargarPrestamosLector();
        }

    }
);


// =====================================================
// CARGAR PRÉSTAMOS DEL LECTOR
// =====================================================

async function cargarPrestamosLector() {

    const contenedor =
        document.getElementById(
            "listaPrestamosLector"
        );

    const mensaje =
        document.getElementById(
            "mensajePrestamosLector"
        );

    const sesionGuardada =
        localStorage.getItem(
            "bibliotecaSesion"
        );

    if (!sesionGuardada) {

        window.location.href =
            "/login";

        return;
    }

    try {

        const sesion =
            JSON.parse(
                sesionGuardada
            );

        const idUsuario =
            sesion.idUsuario;

        if (!idUsuario) {

            throw new Error(
                "No se pudo identificar al lector."
            );
        }

        const response =
            await fetch(
                `/api/prestamos/usuario/${idUsuario}`
            );

        if (!response.ok) {

            throw new Error(
                "No se pudieron cargar tus préstamos."
            );
        }

        const prestamos =
            await response.json();

        mostrarPrestamosLector(
            prestamos
        );

        if (mensaje) {
            mensaje.textContent = "";
        }

    } catch (error) {

        if (contenedor) {

            contenedor.innerHTML = `

                <div style="
                    grid-column: 1 / -1;
                    background: white;
                    border-radius: 12px;
                    padding: 40px;
                    text-align: center;
                    box-shadow:
                        0 4px 14px rgba(0,0,0,0.08);
                ">

                    <div style="
                        font-size: 45px;
                        margin-bottom: 15px;
                    ">
                        ⚠️
                    </div>

                    <strong>
                        No se pudieron cargar tus préstamos.
                    </strong>

                </div>
            `;
        }

        if (mensaje) {

            mensaje.className =
                "mensaje error";

            mensaje.textContent =
                error.message;
        }

        console.error(error);
    }
}


// =====================================================
// MOSTRAR PRÉSTAMOS
// =====================================================

function mostrarPrestamosLector(
    prestamos
) {

    const contenedor =
        document.getElementById(
            "listaPrestamosLector"
        );

    if (!contenedor) {
        return;
    }

    if (!prestamos ||
        prestamos.length === 0) {

        contenedor.innerHTML = `

            <div style="
                grid-column: 1 / -1;
                background: white;
                border-radius: 14px;
                padding: 50px 20px;
                text-align: center;
                box-shadow:
                    0 4px 14px rgba(0,0,0,0.08);
            ">

                <div style="
                    font-size: 50px;
                    margin-bottom: 15px;
                ">
                    📚
                </div>

                <h3 style="
                    margin-bottom: 8px;
                    color: #17365d;
                ">
                    No tienes préstamos registrados
                </h3>

                <p style="
                    color: #666;
                    margin: 0;
                ">
                    Cuando realices una reserva aparecerá aquí.
                </p>

            </div>
        `;

        return;
    }

    contenedor.innerHTML = "";

    prestamos.forEach(
        prestamo => {

            const fecha =
                prestamo.fechaPrestamo
                    ? new Date(
                        prestamo.fechaPrestamo
                    ).toLocaleString(
                        "es-PE"
                    )
                    : "";

            const total =
                Number(
                    prestamo.montoTotal ?? 0
                ).toFixed(2);

            const estado =
                (
                    prestamo.estado ??
                    ""
                ).toLowerCase();

            let fondoEstado =
                "#d1e7dd";

            let colorEstado =
                "#0f5132";

            let textoEstado =
                estado;

            if (estado === "abierto") {

                textoEstado =
                    "Abierto";

            } else if (
                estado === "cerrado"
            ) {

                fondoEstado =
                    "#e2e3e5";

                colorEstado =
                    "#41464b";

                textoEstado =
                    "Cerrado";

            } else if (
                estado === "en_mora"
            ) {

                fondoEstado =
                    "#f8d7da";

                colorEstado =
                    "#842029";

                textoEstado =
                    "En mora";

            } else if (
                estado === "cancelado"
            ) {

                fondoEstado =
                    "#f8d7da";

                colorEstado =
                    "#842029";

                textoEstado =
                    "Cancelado";
            }

            const tarjeta =
                document.createElement(
                    "div"
                );

            tarjeta.style.cssText = `

                background: white;
                border-radius: 14px;
                padding: 24px;
                box-shadow:
                    0 4px 14px rgba(0,0,0,0.08);

                display: flex;
                flex-direction: column;
                gap: 12px;

                min-height: 250px;
            `;

            tarjeta.innerHTML = `

                <div style="
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    gap: 10px;
                ">

                    <strong style="
                        font-size: 19px;
                        color: #17365d;
                    ">
                        Préstamo #${prestamo.idPrestamo ?? ""}
                    </strong>

                    <span style="
                        background: ${fondoEstado};
                        color: ${colorEstado};
                        padding: 6px 12px;
                        border-radius: 20px;
                        font-weight: 600;
                        font-size: 13px;
                    ">
                        ${textoEstado}
                    </span>

                </div>


                <div style="
                    border-top: 1px solid #eee;
                    padding-top: 12px;
                ">
                    <strong>Fecha:</strong>
                    <br>
                    ${fecha}
                </div>


                <div>
                    <strong>Comprobante:</strong>
                    <br>
                    ${prestamo.serie ?? ""}
                    -
                    ${prestamo.numero ?? ""}
                </div>


                <div>
                    <strong>Total:</strong>
                    <br>
                    S/ ${total}
                </div>


                <button
                    type="button"
                    class="btn btn-blue"
                    style="
                        width: 100%;
                        margin-top: auto;
                    "
                    onclick="
                        verDetallePrestamoLector(
                            ${prestamo.idPrestamo}
                        )
                    ">

                    Ver detalle

                </button>
            `;

            contenedor.appendChild(
                tarjeta
            );
        }
    );
}


// =====================================================
// VER DETALLE
// =====================================================

function verDetallePrestamoLector(
    idPrestamo
) {

    localStorage.setItem(
        "prestamoLectorDetalleId",
        idPrestamo
    );

    window.location.href =
        `/lector/prestamos/${idPrestamo}`;
}

// =====================================================
// DETALLE DEL PRÉSTAMO DEL LECTOR
// =====================================================

document.addEventListener(
    "DOMContentLoaded",
    () => {

        const contenedor =
            document.getElementById(
                "detallePrestamoLector"
            );

        if (contenedor) {
            cargarDetallePrestamoLector();
        }

    }
);


async function cargarDetallePrestamoLector() {

    const contenedor =
        document.getElementById(
            "detallePrestamoLector"
        );

    const partes =
        window.location.pathname
            .split("/");

    const idPrestamo =
        Number(
            partes[
            partes.length - 1
                ]
        );

    try {

        const response =
            await fetch(
                `/api/prestamos/lector/${idPrestamo}`
            );

        if (response.status === 401) {

            window.location.href =
                "/login";

            return;
        }

        if (response.status === 403) {

            throw new Error(
                "No tienes permiso para consultar este préstamo."
            );
        }

        if (!response.ok) {

            throw new Error(
                "No se pudo cargar el préstamo."
            );
        }

        const prestamo =
            await response.json();

        mostrarDetallePrestamoLector(
            prestamo
        );

    } catch (error) {

        contenedor.innerHTML = `

            <div style="
                background: white;
                padding: 40px;
                border-radius: 14px;
                text-align: center;
                box-shadow:
                    0 4px 14px rgba(0,0,0,0.08);
            ">

                <div style="
                    font-size: 45px;
                    margin-bottom: 15px;
                ">
                    ⚠️
                </div>

                <strong>
                    ${error.message}
                </strong>

            </div>
        `;

        console.error(error);
    }
}


function mostrarDetallePrestamoLector(
    prestamo
) {

    const contenedor =
        document.getElementById(
            "detallePrestamoLector"
        );

    const fecha =
        prestamo.fechaPrestamo
            ? new Date(
                prestamo.fechaPrestamo
            ).toLocaleString("es-PE")
            : "";

    const total =
        Number(
            prestamo.montoTotal ?? 0
        ).toFixed(2);

    const detalles =
        prestamo.detalles ?? [];

    const librosHtml =
        detalles.length > 0

            ? detalles
                .map(
                    detalle => `

                    <div style="
                        background: #f8f9fa;
                        padding: 18px;
                        border-radius: 10px;
                        margin-top: 12px;
                    ">

                        <strong style="
                            font-size: 17px;
                            color: #17365d;
                        ">
                            📖 ${detalle.tituloLibro ?? ""}
                        </strong>

                        <div style="
                            margin-top: 10px;
                            line-height: 1.7;
                        ">

                            <div>
                                <strong>Días:</strong>
                                ${detalle.tiempo ?? ""}
                            </div>

                            <div>
                                <strong>Fecha compromiso:</strong>
                                ${detalle.fechaCompromiso ?? ""}
                            </div>

                            <div>
                                <strong>Estado:</strong>
                                ${detalle.estado ?? ""}
                            </div>

                            <div>
                                <strong>Subtotal:</strong>
                                S/
                                ${Number(
                        detalle.subtotal ?? 0
                    ).toFixed(2)}
                            </div>

                        </div>

                    </div>
                `
                )
                .join("")

            : `
                <p>
                    No existen libros asociados.
                </p>
            `;


    contenedor.innerHTML = `

        <div style="
            background: white;
            border-radius: 14px;
            padding: 30px;
            box-shadow:
                0 4px 14px rgba(0,0,0,0.08);
        ">

            <div style="
                display: flex;
                justify-content: space-between;
                align-items: center;
                gap: 15px;
                border-bottom: 1px solid #eee;
                padding-bottom: 18px;
                margin-bottom: 20px;
            ">

                <h2 style="
                    margin: 0;
                    color: #17365d;
                ">
                    Préstamo #${prestamo.idPrestamo}
                </h2>

                <strong>
                    ${prestamo.estado ?? ""}
                </strong>

            </div>

            <div style="
                display: grid;
                grid-template-columns:
                    repeat(auto-fit, minmax(200px, 1fr));
                gap: 18px;
                margin-bottom: 25px;
            ">

                <div>
                    <strong>Fecha</strong>
                    <br>
                    ${fecha}
                </div>

                <div>
                    <strong>Comprobante</strong>
                    <br>
                    ${prestamo.serie ?? ""}
                    -
                    ${prestamo.numero ?? ""}
                </div>

                <div>
                    <strong>Total</strong>
                    <br>
                    S/ ${total}
                </div>

            </div>

            <h3 style="
                color: #17365d;
                margin-bottom: 10px;
            ">
                Libros
            </h3>

            ${librosHtml}

        </div>
    `;
}

// =====================================================
// RESULTADO DE RESERVA
// =====================================================

document.addEventListener(
    "DOMContentLoaded",
    () => {

        const contenedor =
            document.getElementById(
                "resultadoReservaLector"
            );

        if (contenedor) {
            cargarResultadoReservaLector();
        }

    }
);


async function cargarResultadoReservaLector() {

    const contenedor =
        document.getElementById(
            "resultadoReservaLector"
        );

    const partes =
        window.location.pathname.split("/");

    const idPrestamo =
        Number(
            partes[
            partes.length - 1
                ]
        );

    try {

        const response =
            await fetch(
                `/api/prestamos/lector/${idPrestamo}`
            );

        if (response.status === 401) {

            window.location.href =
                "/login";

            return;
        }

        if (response.status === 403) {

            throw new Error(
                "No tienes permiso para visualizar esta reserva."
            );
        }

        if (!response.ok) {

            throw new Error(
                "No se pudo cargar el resultado de la reserva."
            );
        }

        const prestamo =
            await response.json();

        mostrarResultadoReservaLector(
            prestamo
        );

    } catch (error) {

        contenedor.innerHTML = `

            <div style="
                text-align: center;
                padding: 35px;
            ">

                <div style="
                    font-size: 50px;
                    margin-bottom: 15px;
                ">
                    ⚠️
                </div>

                <strong>
                    ${error.message}
                </strong>

            </div>
        `;

        console.error(error);
    }
}


function mostrarResultadoReservaLector(
    prestamo
) {

    const contenedor =
        document.getElementById(
            "resultadoReservaLector"
        );

    const fecha =
        prestamo.fechaPrestamo
            ? new Date(
                prestamo.fechaPrestamo
            ).toLocaleString(
                "es-PE"
            )
            : "";

    const total =
        Number(
            prestamo.montoTotal ?? 0
        ).toFixed(2);

    const detalles =
        prestamo.detalles ?? [];

    const librosHtml =
        detalles.map(
            detalle => `

                <div style="
                    background: #f8f9fa;
                    border-radius: 10px;
                    padding: 18px;
                    margin-top: 12px;
                    text-align: left;
                ">

                    <strong style="
                        font-size: 17px;
                        color: #17365d;
                    ">
                        📖 ${detalle.tituloLibro ?? ""}
                    </strong>

                    <div style="
                        margin-top: 10px;
                        line-height: 1.7;
                    ">

                        <div>
                            <strong>Días:</strong>
                            ${detalle.tiempo ?? ""}
                        </div>

                        <div>
                            <strong>Fecha compromiso:</strong>
                            ${detalle.fechaCompromiso ?? ""}
                        </div>

                        <div>
                            <strong>Subtotal:</strong>
                            S/
                            ${Number(
                detalle.subtotal ?? 0
            ).toFixed(2)}
                        </div>

                    </div>

                </div>
            `
        ).join("");


    contenedor.innerHTML = `

        <div style="
            text-align: center;
        ">

            <div style="
                font-size: 62px;
                margin-bottom: 12px;
            ">
                ✅
            </div>

            <h1 style="
                color: #198754;
                margin-bottom: 8px;
            ">
                Reserva realizada correctamente
            </h1>

            <p style="
                color: #666;
                margin-bottom: 30px;
            ">
                Tu reserva fue registrada exitosamente.
            </p>

        </div>


        <div style="
            border-top: 1px solid #eee;
            border-bottom: 1px solid #eee;
            padding: 22px 0;
            margin-bottom: 25px;

            display: grid;
            grid-template-columns:
                repeat(auto-fit, minmax(180px, 1fr));

            gap: 20px;
        ">

            <div>
                <strong>Préstamo</strong>
                <br>
                #${prestamo.idPrestamo}
            </div>

            <div>
                <strong>Fecha</strong>
                <br>
                ${fecha}
            </div>

            <div>
                <strong>Comprobante</strong>
                <br>
                ${prestamo.serie ?? ""}
                -
                ${prestamo.numero ?? ""}
            </div>

            <div>
                <strong>Total</strong>
                <br>
                S/ ${total}
            </div>

        </div>


        <h3 style="
            color: #17365d;
            margin-bottom: 10px;
        ">
            Libro reservado
        </h3>

        ${librosHtml}


        <div style="
            margin-top: 30px;
            display: flex;
            justify-content: center;
            gap: 12px;
            flex-wrap: wrap;
        ">

            <button
                type="button"
                id="btnPdfReserva"
                class="btn btn-blue">

                Descargar comprobante PDF

            </button>

            <a href="/lector/prestamos"
               class="btn btn-gray">

                Mis Préstamos

            </a>

            <a href="/lector/catalogo"
               class="btn btn-gray">

                Volver al Catálogo

            </a>

        </div>
    `;


    // =====================================================
    // BOTÓN PDF
    // =====================================================

    const btnPdf =
        document.getElementById(
            "btnPdfReserva"
        );

    if (btnPdf) {

        btnPdf.addEventListener(
            "click",
            () => {

                window.location.href =
                    `/api/prestamos/${prestamo.idPrestamo}/pdf`;

            }
        );
    }
}


// =====================================================
// COLA DE ESPERA - LECTOR
// =====================================================

document.addEventListener(
    "DOMContentLoaded",
    () => {

        const btnUnirse =
            document.getElementById(
                "btnUnirseCola"
            );

        if (btnUnirse) {

            cargarLibroParaCola();

            btnUnirse.addEventListener(
                "click",
                registrarColaLector
            );
        }


        const lista =
            document.getElementById(
                "listaColaLector"
            );

        if (lista) {
            cargarMiColaLector();
        }

    }
);


// =====================================================
// CARGAR LIBRO PARA UNIRSE
// =====================================================

async function cargarLibroParaCola() {

    const contenedor =
        document.getElementById(
            "detalleLibroCola"
        );

    const partes =
        window.location.pathname.split("/");

    const idLibro =
        Number(
            partes[
            partes.length - 1
                ]
        );

    try {

        const response =
            await fetch(
                `/api/libros/${idLibro}`
            );

        if (!response.ok) {

            throw new Error(
                "No se pudo cargar el libro."
            );
        }

        const libro =
            await response.json();

        if (libro.estado === true) {

            contenedor.innerHTML = `

                <div style="
                    font-size:48px;
                    margin-bottom:15px;
                ">
                    ✅
                </div>

                <h3>
                    Este libro ya está disponible
                </h3>

                <p style="
                    color:#666;
                    margin-top:10px;
                ">
                    Ya no necesitas ingresar a la cola.
                </p>

                <a
                    href="/lector/reservar/${libro.idLibro}"
                    class="btn btn-blue"
                    style="
                        display:inline-block;
                        margin-top:20px;
                    ">
                    Reservar ahora
                </a>
            `;

            const btn =
                document.getElementById(
                    "btnUnirseCola"
                );

            if (btn) {
                btn.style.display = "none";
            }

            return;
        }

        contenedor.innerHTML = `

            <div style="
                font-size:55px;
                margin-bottom:15px;
            ">
                📖
            </div>

            <h2 style="
                color:#17365d;
                margin-bottom:10px;
            ">
                ${libro.titulo ?? ""}
            </h2>

            <p>
                <strong>ISBN:</strong>
                ${libro.isbn ?? ""}
            </p>

            <p style="
                margin-top:12px;
                color:#dc3545;
                font-weight:600;
            ">
                Actualmente no disponible
            </p>
        `;

    } catch (error) {

        contenedor.innerHTML =
            `<p>${error.message}</p>`;

        console.error(error);
    }
}


// =====================================================
// REGISTRAR LECTOR EN COLA
// =====================================================

async function registrarColaLector() {

    const mensaje =
        document.getElementById(
            "mensajeUnirseCola"
        );

    const sesionGuardada =
        localStorage.getItem(
            "bibliotecaSesion"
        );

    if (!sesionGuardada) {

        window.location.href =
            "/login";

        return;
    }

    const sesion =
        JSON.parse(
            sesionGuardada
        );

    const idUsuario =
        sesion.idUsuario;

    const partes =
        window.location.pathname.split("/");

    const idLibro =
        Number(
            partes[
            partes.length - 1
                ]
        );

    try {

        const response =
            await fetch(
                `/api/cola-espera/usuario/${idUsuario}/libro/${idLibro}`,
                {
                    method: "POST"
                }
            );

        const respuesta =
            await response.json();

        if (!response.ok) {

            throw new Error(
                respuesta.mensaje ||
                "No se pudo registrar en la cola."
            );
        }

        mensaje.className =
            "form-message success";

        mensaje.textContent =
            "Te uniste correctamente a la cola de espera.";

        setTimeout(
            () => {

                window.location.href =
                    "/lector/cola";

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
// MI COLA DE ESPERA
// =====================================================

async function cargarMiColaLector() {

    const contenedor =
        document.getElementById(
            "listaColaLector"
        );

    const sesionGuardada =
        localStorage.getItem(
            "bibliotecaSesion"
        );

    if (!sesionGuardada) {

        window.location.href =
            "/login";

        return;
    }

    try {

        const sesion =
            JSON.parse(
                sesionGuardada
            );

        const idUsuario =
            sesion.idUsuario;

        const response =
            await fetch(
                `/api/cola-espera/usuario/${idUsuario}`
            );

        if (response.status === 401) {

            window.location.href =
                "/login";

            return;
        }

        if (!response.ok) {

            throw new Error(
                "No se pudo cargar tu cola de espera."
            );
        }

        const registros =
            await response.json();

        await mostrarMiColaLector(
            registros
        );

    } catch (error) {

        contenedor.innerHTML = `

            <div style="
                grid-column:1/-1;
                background:white;
                padding:40px;
                border-radius:14px;
                text-align:center;
            ">

                ⚠️
                ${error.message}

            </div>
        `;

        console.error(error);
    }
}


// =====================================================
// MOSTRAR MI COLA
// =====================================================

async function mostrarMiColaLector(
    registros
) {

    const contenedor =
        document.getElementById(
            "listaColaLector"
        );

    if (!registros ||
        registros.length === 0) {

        contenedor.innerHTML = `

            <div style="
                grid-column:1/-1;
                background:white;
                border-radius:14px;
                padding:50px 20px;
                text-align:center;
                box-shadow:
                    0 4px 14px rgba(0,0,0,0.08);
            ">

                <div style="
                    font-size:50px;
                    margin-bottom:15px;
                ">
                    📚
                </div>

                <h3 style="
                    color:#17365d;
                    margin-bottom:8px;
                ">
                    No estás esperando ningún libro
                </h3>

                <p style="color:#666;">
                    Cuando te unas a una cola aparecerá aquí.
                </p>

            </div>
        `;

        return;
    }


    const registrosConLibro =
        await Promise.all(

            registros.map(
                async registro => {

                    try {

                        const response =
                            await fetch(
                                `/api/libros/${registro.idLibro}`
                            );

                        if (!response.ok) {
                            return {
                                ...registro,
                                libroDetalle: null
                            };
                        }

                        const libro =
                            await response.json();

                        return {
                            ...registro,
                            libroDetalle: libro
                        };

                    } catch {

                        return {
                            ...registro,
                            libroDetalle: null
                        };
                    }
                }
            )
        );


    contenedor.innerHTML = "";


    registrosConLibro.forEach(
        registro => {

            const libro =
                registro.libroDetalle;

            const estado =
                (
                    registro.estado ??
                    ""
                ).toLowerCase();

            const fecha =
                registro.fechaRegistro
                    ? new Date(
                        registro.fechaRegistro
                    ).toLocaleString(
                        "es-PE"
                    )
                    : "-";


            let textoEstado =
                "En espera";

            let fondo =
                "#fff3cd";

            let color =
                "#664d03";


            if (estado === "notificado") {

                textoEstado =
                    "Disponible para reservar";

                fondo =
                    "#d1e7dd";

                color =
                    "#0f5132";

            } else if (
                estado === "atendido"
            ) {

                textoEstado =
                    "Atendido";

                fondo =
                    "#e2e3e5";

                color =
                    "#41464b";
            }


            const tarjeta =
                document.createElement(
                    "div"
                );

            tarjeta.style.cssText = `

                background:white;
                border-radius:14px;
                padding:24px;
                box-shadow:
                    0 4px 14px rgba(0,0,0,0.08);

                display:flex;
                flex-direction:column;
                gap:12px;
            `;


            tarjeta.innerHTML = `

                <div style="
                    display:flex;
                    justify-content:space-between;
                    align-items:flex-start;
                    gap:10px;
                ">

                    <strong style="
                        font-size:19px;
                        color:#17365d;
                    ">
                        📖 ${libro?.titulo ?? "Libro"}
                    </strong>

                    <span style="
                        background:${fondo};
                        color:${color};
                        padding:6px 11px;
                        border-radius:20px;
                        font-size:12px;
                        font-weight:700;
                    ">
                        ${textoEstado}
                    </span>

                </div>


                <div style="
                    border-top:1px solid #eee;
                    padding-top:12px;
                ">

                    <strong>ISBN:</strong>
                    <br>
                    ${libro?.isbn ?? ""}

                </div>


                <div>

                    <strong>Fecha de registro:</strong>
                    <br>
                    ${fecha}

                </div>


                ${
                estado === "notificado"

                    ? `

                            <button
                                type="button"
                                class="btn btn-blue"
                                style="
                                    width:100%;
                                    margin-top:10px;
                                "
                                onclick="
                                    reservarDesdeCola(
                                        ${registro.idCola},
                                        ${registro.idLibro}
                                    )
                                ">

                                Reservar ahora

                            </button>
                        `

                    : ""
            }
            `;

            contenedor.appendChild(
                tarjeta
            );
        }
    );
}


// =====================================================
// RESERVAR DESDE COLA
// =====================================================

function reservarDesdeCola(
    idCola,
    idLibro
) {

    localStorage.setItem(
        "colaReservaId",
        idCola
    );

    window.location.href =
        `/lector/reservar/${idLibro}`;
}
package com.biblioteca.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import com.biblioteca.app.dto.NuevoPrestamoDTO;
import com.biblioteca.app.model.Comprobante;
import com.biblioteca.app.model.DetallePrestamo;
import com.biblioteca.app.model.Prestamo;
import com.biblioteca.app.service.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.biblioteca.app.service.PdfPrestamoService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;
    @Autowired
    private PdfPrestamoService pdfPrestamoService;
    /*
     * Lista todos los préstamos.
     * Si se recibe ?buscar=..., realiza la búsqueda.
     */
    @GetMapping
    public ResponseEntity<List<Prestamo>> listar(
            @RequestParam(required = false) String buscar
    ) {

        if (buscar == null || buscar.isBlank()) {
            return ResponseEntity.ok(
                    prestamoService.listarPrestamos()
            );
        }

        return ResponseEntity.ok(
                prestamoService.buscarPrestamos(buscar)
        );
    }

    /*
     * Registra un nuevo préstamo.
     */
    @PostMapping
    public ResponseEntity<Prestamo> registrar(
            @RequestBody NuevoPrestamoDTO datos
    ) {

        Prestamo prestamo =
                prestamoService.registrarPrestamo(datos);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(prestamo);
    }

    /*
     * Lista únicamente los préstamos
     * correspondientes a un usuario.
     *
     * Si el usuario autenticado es Lector,
     * solamente puede consultar sus propios
     * préstamos.
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Prestamo>> listarPorUsuario(
            @PathVariable Integer idUsuario,
            jakarta.servlet.http.HttpServletRequest request
    ) {

        jakarta.servlet.http.HttpSession session =
                request.getSession(false);

        /*
         * Se valida que exista una sesión activa.
         */
        if (session == null
                || session.getAttribute("usuarioId") == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        Object rol =
                session.getAttribute("tipoUsuario");

        Integer usuarioSesion =
                (Integer) session.getAttribute("usuarioId");

        /*
         * El Lector solamente puede consultar
         * sus propios préstamos.
         */
        if (rol != null
                && "Lector".equalsIgnoreCase(
                rol.toString()
        )
                && !usuarioSesion.equals(idUsuario)) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        return ResponseEntity.ok(
                prestamoService.buscarPorUsuario(
                        idUsuario
                )
        );
    }

    /*
     * Devuelve un préstamo específico para el Lector.
     *
     * El Lector solamente puede consultar
     * préstamos pertenecientes a su propia cuenta.
     */
    @GetMapping("/lector/{idPrestamo}")
    public ResponseEntity<Map<String, Object>> obtenerPrestamoLector(
            @PathVariable Integer idPrestamo,
            jakarta.servlet.http.HttpServletRequest request
    ) {

        jakarta.servlet.http.HttpSession session =
                request.getSession(false);

        if (session == null
                || session.getAttribute("usuarioId") == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        Object rol =
                session.getAttribute("tipoUsuario");

        Integer idUsuarioSesion =
                (Integer) session.getAttribute("usuarioId");

        if (rol == null
                || !"Lector".equalsIgnoreCase(
                rol.toString()
        )) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        Prestamo prestamo =
                prestamoService.buscarPorId(
                        idPrestamo
                );

        if (prestamo.getUsuario() == null
                || !idUsuarioSesion.equals(
                prestamo.getUsuario()
                        .getIdUsuario()
        )) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        respuesta.put(
                "idPrestamo",
                prestamo.getIdPrestamo()
        );

        respuesta.put(
                "fechaPrestamo",
                prestamo.getFechaPrestamo()
        );

        respuesta.put(
                "estado",
                prestamo.getEstado()
        );

        respuesta.put(
                "serie",
                prestamo.getSerie()
        );

        respuesta.put(
                "numero",
                prestamo.getNumero()
        );

        respuesta.put(
                "montoTotal",
                prestamo.getMontoTotal()
        );

        respuesta.put(
                "observaciones",
                prestamo.getObservaciones()
        );

        List<Map<String, Object>> detalles =
                prestamoService
                        .listarDetalles(idPrestamo)
                        .stream()
                        .map(this::convertirDetalle)
                        .toList();

        respuesta.put(
                "detalles",
                detalles
        );

        return ResponseEntity.ok(
                respuesta
        );
    }

    /*
     * Busca un préstamo por su ID.
     */

    @GetMapping("/{id}")
    public ResponseEntity<Prestamo> buscarPorId(
            @PathVariable Integer id,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("usuarioId") == null
                || session.getAttribute("tipoUsuario") == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        Prestamo prestamo = prestamoService.buscarPorId(id);

        if (!puedeConsultarPrestamo(session, prestamo)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        return ResponseEntity.ok(prestamo);
    }


    /*
     * Devuelve los detalles de un préstamo.
     *
     * En lugar de enviar toda la estructura JPA,
     * enviamos solamente los datos que necesita
     * la pantalla.
     */

    @GetMapping("/{id}/detalles")
    public ResponseEntity<List<Map<String, Object>>> listarDetalles(
            @PathVariable Integer id,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("usuarioId") == null
                || session.getAttribute("tipoUsuario") == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        // Validamos el propietario antes de consultar
        // y devolver los detalles del préstamo.
        Prestamo prestamo = prestamoService.buscarPorId(id);

        if (!puedeConsultarPrestamo(session, prestamo)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        List<Map<String, Object>> respuesta =
                prestamoService.listarDetalles(id)
                        .stream()
                        .map(this::convertirDetalle)
                        .toList();

        return ResponseEntity.ok(respuesta);
    }


    /*
     * Lista los tipos de comprobante disponibles.
     */
    @GetMapping("/comprobantes")
    public ResponseEntity<List<Comprobante>> listarComprobantes() {

        return ResponseEntity.ok(
                prestamoService.listarComprobantes()
        );
    }

    /*
     * Genera serie y número para el comprobante
     * seleccionado.
     */
    @GetMapping("/comprobante")
    public ResponseEntity<Map<String, Object>> datosComprobante(
            @RequestParam Integer idComprobante
    ) {

        return ResponseEntity.ok(
                prestamoService.obtenerDatosComprobante(
                        idComprobante
                )
        );
    }

    /*
     * Registra la devolución de un libro.
     */
    @PutMapping("/devolucion/{idDetalle}")
    public ResponseEntity<DetallePrestamo> registrarDevolucion(
            @PathVariable Integer idDetalle
    ) {

        DetallePrestamo detalleDevuelto =
                prestamoService
                        .registrarDevolucion(idDetalle);

        return ResponseEntity.ok(
                detalleDevuelto
        );
    }

    /*
     * Convierte un detalle JPA en una respuesta
     * sencilla para el frontend.
     */
    private Map<String, Object> convertirDetalle(
            DetallePrestamo detalle
    ) {

        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        respuesta.put(
                "idDetallePrestamo",
                detalle.getIdDetallePrestamo()
        );

        respuesta.put(
                "idLibro",
                detalle.getLibro() != null
                        ? detalle.getLibro().getIdLibro()
                        : null
        );

        respuesta.put(
                "tituloLibro",
                detalle.getLibro() != null
                        ? detalle.getLibro().getTitulo()
                        : ""
        );

        respuesta.put(
                "precioTiempo",
                detalle.getPrecioTiempo()
        );

        respuesta.put(
                "tiempo",
                detalle.getTiempo()
        );

        respuesta.put(
                "fechaCompromiso",
                detalle.getFechaCompromiso()
        );

        respuesta.put(
                "fechaDevolucion",
                detalle.getFechaDevolucion()
        );

        respuesta.put(
                "subtotal",
                detalle.getSubtotal()
        );

        respuesta.put(
                "estado",
                detalle.getEstado()
        );

        return respuesta;
    }

    /*
     * Los errores de validación del servicio
     * regresan al frontend como JSON.
     */
    /*
     * Genera el comprobante PDF de un préstamo.
     */
    @GetMapping("/{idPrestamo}/pdf")
    public ResponseEntity<byte[]> descargarPdf(
            @PathVariable Integer idPrestamo,
            jakarta.servlet.http.HttpServletRequest request
    ) {

        jakarta.servlet.http.HttpSession session =
                request.getSession(false);

        if (session == null
                || session.getAttribute("usuarioId") == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        Prestamo prestamo =
                prestamoService.buscarPorId(
                        idPrestamo
                );

        Object rol =
                session.getAttribute("tipoUsuario");

        Integer idUsuarioSesion =
                (Integer) session.getAttribute(
                        "usuarioId"
                );

        /*
         * Si es Lector, solamente puede descargar
         * comprobantes de sus propios préstamos.
         */
        if (rol != null
                && "Lector".equalsIgnoreCase(
                rol.toString()
        )) {

            if (prestamo.getUsuario() == null
                    || !idUsuarioSesion.equals(
                    prestamo.getUsuario()
                            .getIdUsuario()
            )) {

                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .build();
            }
        }

        byte[] pdf =
                pdfPrestamoService
                        .generarComprobante(
                                idPrestamo
                        );

        return ResponseEntity.ok()
                .header(
                        "Content-Type",
                        "application/pdf"
                )
                .header(
                        "Content-Disposition",
                        "attachment; filename=comprobante-reserva-"
                                + idPrestamo
                                + ".pdf"
                )
                .body(pdf);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarError(
            IllegalArgumentException exception
    ) {

        Map<String, String> respuesta =
                new LinkedHashMap<>();

        respuesta.put(
                "mensaje",
                exception.getMessage()
        );

        return ResponseEntity
                .badRequest()
                .body(respuesta);
    }


    private boolean puedeConsultarPrestamo(
            HttpSession session,
            Prestamo prestamo
    ) {
        Object idSesion = session.getAttribute("usuarioId");
        Object tipoUsuario = session.getAttribute("tipoUsuario");

        if (!(idSesion instanceof Number) || tipoUsuario == null) {
            return false;
        }

        String rol = tipoUsuario.toString();

        // El personal autorizado puede consultar
        // los préstamos necesarios para su gestión.
        if ("Administrador".equalsIgnoreCase(rol)
                || "Bibliotecario".equalsIgnoreCase(rol)) {
            return true;
        }

        // Los demás roles no autorizados se rechazan.
        if (!"Lector".equalsIgnoreCase(rol)) {
            return false;
        }

        // El Lector solo puede consultar préstamos propios.
        return prestamo.getUsuario() != null
                && java.util.Objects.equals(
                ((Number) idSesion).intValue(),
                prestamo.getUsuario().getIdUsuario()
        );
    }

}
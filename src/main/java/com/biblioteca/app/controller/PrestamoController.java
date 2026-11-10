package com.biblioteca.app.controller;

import com.biblioteca.app.dto.NuevoPrestamoDTO;
import com.biblioteca.app.model.Comprobante;
import com.biblioteca.app.model.DetallePrestamo;
import com.biblioteca.app.model.Prestamo;
import com.biblioteca.app.service.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

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
     * Busca un préstamo por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Prestamo> buscarPorId(
            @PathVariable Integer id
    ) {

        return ResponseEntity.ok(
                prestamoService.buscarPorId(id)
        );
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
            @PathVariable Integer id
    ) {

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

        return ResponseEntity.ok(detalleDevuelto);
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
}

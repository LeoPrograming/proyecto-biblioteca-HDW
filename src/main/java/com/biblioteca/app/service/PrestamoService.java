package com.biblioteca.app.service;

import com.biblioteca.app.dto.NuevoPrestamoDTO;
import com.biblioteca.app.entity.Usuario;
import com.biblioteca.app.model.Comprobante;
import com.biblioteca.app.model.DetallePrestamo;
import com.biblioteca.app.model.Libro;
import com.biblioteca.app.model.Prestamo;
import com.biblioteca.app.repository.ComprobanteRepository;
import com.biblioteca.app.repository.DetallePrestamoRepository;
import com.biblioteca.app.repository.LibroRepository;
import com.biblioteca.app.repository.PrestamoRepository;
import com.biblioteca.app.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PrestamoService {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private ColaEsperaService colaEsperaService;

    @Autowired
    private DetallePrestamoRepository detallePrestamoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private ComprobanteRepository comprobanteRepository;


    public List<Prestamo> listarPrestamos() {

        return prestamoRepository
                .findAllByOrderByIdPrestamoDesc();
    }


    public List<Prestamo> buscarPrestamos(
            String buscar
    ) {

        if (buscar == null ||
                buscar.isBlank()) {

            return listarPrestamos();
        }

        return prestamoRepository
                .buscarPrestamos(
                        buscar.trim()
                );
    }


    public Prestamo buscarPorId(
            Integer id
    ) {

        return prestamoRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El préstamo con ID "
                                        + id
                                        + " no existe."
                        )
                );
    }


    public List<Prestamo> buscarPorUsuario(
            Integer idUsuario
    ) {

        return prestamoRepository
                .findByUsuario_IdUsuario(
                        idUsuario
                );
    }


    public List<DetallePrestamo> listarDetalles(
            Integer idPrestamo
    ) {

        buscarPorId(idPrestamo);

        return detallePrestamoRepository
                .findByPrestamo_IdPrestamo(
                        idPrestamo
                );
    }


    public List<Comprobante> listarComprobantes() {

        return comprobanteRepository
                .findAll();
    }


    public Map<String, Object> obtenerDatosComprobante(
            Integer idComprobante
    ) {

        comprobanteRepository
                .findById(idComprobante)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Comprobante no encontrado."
                        )
                );

        String serie =
                idComprobante == 1
                        ? "R-2026"
                        : "F-2026";

        long siguiente =
                prestamoRepository
                        .countByComprobante_IdComprobante(
                                idComprobante
                        ) + 1;

        Map<String, Object> datos =
                new LinkedHashMap<>();

        datos.put(
                "serie",
                serie
        );

        datos.put(
                "numero",
                String.format(
                        "%06d",
                        siguiente
                )
        );

        return datos;
    }


    @Transactional
    public Prestamo registrarPrestamo(
            NuevoPrestamoDTO datos
    ) {

        validarNuevoPrestamo(
                datos
        );

        Usuario usuario =
                usuarioRepository
                        .findById(
                                datos.getIdUsuario()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Usuario no encontrado."
                                )
                        );

        Comprobante comprobante =
                comprobanteRepository
                        .findById(
                                datos.getIdComprobante()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Comprobante no encontrado."
                                )
                        );

        long librosPrestadosActuales =
                detallePrestamoRepository
                        .countByPrestamo_Usuario_IdUsuarioAndEstado(
                                datos.getIdUsuario(),
                                "prestado"
                        );

        if (librosPrestadosActuales
                + datos.getIdsLibros().size() > 2) {

            throw new IllegalArgumentException(
                    "El usuario no puede superar el máximo de 2 libros prestados."
            );
        }

        Set<Integer> idsUnicos =
                new HashSet<>();

        BigDecimal total =
                BigDecimal.ZERO;

        /*
         * Primero validamos todos los libros
         * antes de guardar el préstamo.
         */
        for (int i = 0;
             i < datos.getIdsLibros().size();
             i++) {

            Integer idLibro =
                    datos.getIdsLibros()
                            .get(i);

            Integer tiempo =
                    datos.getDias()
                            .get(i);

            if (!idsUnicos.add(idLibro)) {

                throw new IllegalArgumentException(
                        "No se puede agregar el mismo libro más de una vez."
                );
            }

            if (tiempo == null ||
                    tiempo <= 0) {

                throw new IllegalArgumentException(
                        "El tiempo de préstamo debe ser mayor a cero."
                );
            }

            Libro libro =
                    libroRepository
                            .findById(idLibro)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Libro no encontrado."
                                    )
                            );

            /*
             * Solo se pueden prestar
             * libros disponibles.
             */
            if (!Boolean.TRUE.equals(
                    libro.getEstado()
            )) {

                throw new IllegalArgumentException(
                        "El libro "
                                + libro.getTitulo()
                                + " no está disponible."
                );
            }

            BigDecimal precio =
                    libro.getPrecioTiempo() == null
                            ? BigDecimal.ZERO
                            : libro.getPrecioTiempo();

            total =
                    total.add(
                            precio.multiply(
                                    BigDecimal.valueOf(
                                            tiempo
                                    )
                            )
                    );
        }

        Map<String, Object> comprobanteGenerado =
                obtenerDatosComprobante(
                        datos.getIdComprobante()
                );

        /*
         * Creamos el préstamo principal.
         */
        Prestamo prestamo =
                new Prestamo();

        prestamo.setUsuario(
                usuario
        );

        prestamo.setComprobante(
                comprobante
        );

        prestamo.setFechaPrestamo(
                LocalDateTime.now()
        );

        prestamo.setEstado(
                "abierto"
        );

        prestamo.setSerie(
                datos.getSerie() == null
                        || datos.getSerie().isBlank()
                        ? comprobanteGenerado
                          .get("serie")
                          .toString()
                        : datos.getSerie().trim()
        );

        prestamo.setNumero(
                datos.getNumero() == null
                        || datos.getNumero().isBlank()
                        ? comprobanteGenerado
                          .get("numero")
                          .toString()
                        : datos.getNumero().trim()
        );

        prestamo.setImpuesto(
                BigDecimal.ZERO
        );

        prestamo.setMontoTotal(
                total
        );

        prestamo.setObservaciones(
                datos.getObservaciones()
        );

        Prestamo prestamoGuardado =
                prestamoRepository
                        .save(prestamo);

        /*
         * Creamos un detalle
         * por cada libro.
         */
        for (int i = 0;
             i < datos.getIdsLibros().size();
             i++) {

            Libro libro =
                    libroRepository
                            .findById(
                                    datos.getIdsLibros()
                                            .get(i)
                            )
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Libro no encontrado."
                                    )
                            );

            Integer tiempo =
                    datos.getDias()
                            .get(i);

            BigDecimal precio =
                    libro.getPrecioTiempo() == null
                            ? BigDecimal.ZERO
                            : libro.getPrecioTiempo();

            BigDecimal subtotal =
                    precio.multiply(
                            BigDecimal.valueOf(
                                    tiempo
                            )
                    );

            DetallePrestamo detalle =
                    new DetallePrestamo();

            detalle.setPrestamo(
                    prestamoGuardado
            );

            detalle.setLibro(
                    libro
            );

            detalle.setPrecioTiempo(
                    precio
            );

            detalle.setTiempo(
                    tiempo
            );

            detalle.setFechaCompromiso(
                    LocalDate.now()
                            .plusDays(
                                    tiempo
                            )
            );

            detalle.setFechaDevolucion(
                    null
            );

            detalle.setEstado(
                    "prestado"
            );

            detalle.setSubtotal(
                    subtotal
            );

            detallePrestamoRepository
                    .save(detalle);

            /*
             * Al prestarse, el libro
             * deja de estar disponible.
             */
            libro.setEstado(
                    false
            );

            libroRepository
                    .save(libro);
        }

        return prestamoGuardado;
    }


    private void validarNuevoPrestamo(
            NuevoPrestamoDTO datos
    ) {

        if (datos == null) {

            throw new IllegalArgumentException(
                    "Los datos del préstamo son obligatorios."
            );
        }

        if (datos.getIdUsuario() == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un usuario."
            );
        }

        if (datos.getIdComprobante() == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un comprobante."
            );
        }

        if (datos.getIdsLibros() == null
                || datos.getIdsLibros().isEmpty()) {

            throw new IllegalArgumentException(
                    "Debe seleccionar al menos un libro."
            );
        }

        if (datos.getIdsLibros().size() > 2) {

            throw new IllegalArgumentException(
                    "Solo se permiten máximo 2 libros por préstamo."
            );
        }

        if (datos.getDias() == null
                || datos.getDias().size()
                != datos.getIdsLibros().size()) {

            throw new IllegalArgumentException(
                    "Debe indicar el tiempo de préstamo para cada libro."
            );
        }
    }


    @Transactional
    public DetallePrestamo registrarDevolucion(
            Integer idDetalle
    ) {

        DetallePrestamo detalle =
                detallePrestamoRepository
                        .findById(idDetalle)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "El detalle de préstamo no existe."
                                )
                        );

        /*
         * Evitamos devolver dos veces
         * el mismo libro.
         */
        if ("devuelto".equalsIgnoreCase(
                detalle.getEstado()
        )) {

            throw new IllegalArgumentException(
                    "Devolución duplicada: Este libro ya fue devuelto."
            );
        }

        detalle.setEstado(
                "devuelto"
        );

        detalle.setFechaDevolucion(
                LocalDate.now()
        );

        detallePrestamoRepository
                .save(detalle);

        /*
         * El libro vuelve
         * a estar disponible.
         */
        Libro libro =
                detalle.getLibro();

        libro.setEstado(
                true
        );

        libroRepository
                .save(libro);

        /*
         * COLA DE ESPERA:
         * si existe un lector esperando
         * este libro, se notifica únicamente
         * al primero que ingresó a la cola.
         */
        colaEsperaService
                .notificarPrimerLectorEnEspera(
                        libro.getIdLibro()
                );

        /*
         * Revisamos si todavía
         * quedan libros sin devolver.
         */
        Prestamo prestamo =
                detalle.getPrestamo();

        List<DetallePrestamo> pendientes =
                detallePrestamoRepository
                        .findByPrestamo_IdPrestamoAndEstado(
                                prestamo.getIdPrestamo(),
                                "prestado"
                        );

        /*
         * Si todos fueron devueltos,
         * cerramos el préstamo.
         */
        if (pendientes.isEmpty()) {

            prestamo.setEstado(
                    "cerrado"
            );

            prestamoRepository
                    .save(prestamo);
        }

        return detalle;
    }
}
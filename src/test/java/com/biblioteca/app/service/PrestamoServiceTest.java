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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrestamoServiceTest {

    @Mock
    private PrestamoRepository prestamoRepository;

    @Mock
    private DetallePrestamoRepository detallePrestamoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LibroRepository libroRepository;

    @Mock
    private ComprobanteRepository comprobanteRepository;

    @InjectMocks
    private PrestamoService prestamoService;

    private Usuario usuario;
    private Libro libro;
    private Comprobante comprobante;
    private Prestamo prestamo;
    private DetallePrestamo detalle;

    @BeforeEach
    void setUp() {

        usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNombres("Jhordy");
        usuario.setApellidos("Gala");

        comprobante = new Comprobante();
        comprobante.setIdComprobante(1);
        comprobante.setTipo("Recibo");

        libro = new Libro();
        libro.setIdLibro(10);
        libro.setTitulo("Libro de prueba");
        libro.setEstado(true);
        libro.setPrecioTiempo(
                new BigDecimal("2.50")
        );

        prestamo = new Prestamo();
        prestamo.setIdPrestamo(1);
        prestamo.setUsuario(usuario);
        prestamo.setComprobante(comprobante);
        prestamo.setEstado("abierto");

        detalle = new DetallePrestamo();
        detalle.setIdDetallePrestamo(1);
        detalle.setPrestamo(prestamo);
        detalle.setLibro(libro);
        detalle.setEstado("prestado");
    }

    @Test
    void debeRegistrarPrestamoCorrectamente() {

        NuevoPrestamoDTO dto =
                new NuevoPrestamoDTO();

        dto.setIdUsuario(1);
        dto.setIdComprobante(1);
        dto.setIdsLibros(List.of(10));
        dto.setDias(List.of(3));
        dto.setObservaciones("Préstamo de prueba");

        when(usuarioRepository.findById(1))
                .thenReturn(Optional.of(usuario));

        when(comprobanteRepository.findById(1))
                .thenReturn(Optional.of(comprobante));

        when(libroRepository.findById(10))
                .thenReturn(Optional.of(libro));

        when(
                prestamoRepository
                        .countByComprobante_IdComprobante(1)
        ).thenReturn(0L);

        when(prestamoRepository.save(any(Prestamo.class)))
                .thenAnswer(invocation -> {

                    Prestamo guardado =
                            invocation.getArgument(0);

                    guardado.setIdPrestamo(1);

                    return guardado;
                });

        Prestamo resultado =
                prestamoService
                        .registrarPrestamo(dto);

        assertEquals(
                "abierto",
                resultado.getEstado()
        );

        assertEquals(
                new BigDecimal("7.50"),
                resultado.getMontoTotal()
        );

        assertEquals(
                "R-2026",
                resultado.getSerie()
        );

        assertEquals(
                "000001",
                resultado.getNumero()
        );

        assertFalse(libro.getEstado());

        ArgumentCaptor<DetallePrestamo> captor =
                ArgumentCaptor.forClass(
                        DetallePrestamo.class
                );

        verify(detallePrestamoRepository)
                .save(captor.capture());

        DetallePrestamo detalleGuardado =
                captor.getValue();

        assertEquals(
                "prestado",
                detalleGuardado.getEstado()
        );

        assertEquals(
                new BigDecimal("2.50"),
                detalleGuardado.getPrecioTiempo()
        );

        assertEquals(
                3,
                detalleGuardado.getTiempo()
        );

        assertEquals(
                new BigDecimal("7.50"),
                detalleGuardado.getSubtotal()
        );

        assertEquals(
                LocalDate.now().plusDays(3),
                detalleGuardado.getFechaCompromiso()
        );

        verify(libroRepository)
                .save(libro);
    }

    @Test
    void debeRechazarLibroNoDisponible() {

        libro.setEstado(false);

        NuevoPrestamoDTO dto =
                crearDtoValido();

        when(usuarioRepository.findById(1))
                .thenReturn(Optional.of(usuario));

        when(comprobanteRepository.findById(1))
                .thenReturn(Optional.of(comprobante));

        when(libroRepository.findById(10))
                .thenReturn(Optional.of(libro));

        IllegalArgumentException error =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                prestamoService
                                        .registrarPrestamo(dto)
                );

        assertTrue(
                error.getMessage()
                        .contains("no está disponible")
        );

        verify(
                prestamoRepository,
                never()
        ).save(any());
    }

    @Test
    void debeRechazarLibroDuplicadoEnMismoPrestamo() {

        NuevoPrestamoDTO dto =
                new NuevoPrestamoDTO();

        dto.setIdUsuario(1);
        dto.setIdComprobante(1);
        dto.setIdsLibros(
                List.of(10, 10)
        );
        dto.setDias(
                List.of(2, 3)
        );

        when(usuarioRepository.findById(1))
                .thenReturn(Optional.of(usuario));

        when(comprobanteRepository.findById(1))
                .thenReturn(Optional.of(comprobante));

        when(libroRepository.findById(10))
                .thenReturn(Optional.of(libro));

        IllegalArgumentException error =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                prestamoService
                                        .registrarPrestamo(dto)
                );

        assertTrue(
                error.getMessage()
                        .contains("mismo libro")
        );

        verify(
                prestamoRepository,
                never()
        ).save(any());
    }

    @Test
    void debeRechazarUsuarioInexistente() {

        NuevoPrestamoDTO dto =
                crearDtoValido();

        when(usuarioRepository.findById(1))
                .thenReturn(Optional.empty());

        IllegalArgumentException error =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                prestamoService
                                        .registrarPrestamo(dto)
                );

        assertEquals(
                "Usuario no encontrado.",
                error.getMessage()
        );
    }

    @Test
    void debeGenerarSerieYNumeroDeComprobante() {

        when(comprobanteRepository.findById(1))
                .thenReturn(Optional.of(comprobante));

        when(
                prestamoRepository
                        .countByComprobante_IdComprobante(1)
        ).thenReturn(4L);

        var datos =
                prestamoService
                        .obtenerDatosComprobante(1);

        assertEquals(
                "R-2026",
                datos.get("serie")
        );

        assertEquals(
                "000005",
                datos.get("numero")
        );
    }

    @Test
    void registrarDevolucionDebeLiberarLibroYCerrarPrestamo() {

        libro.setEstado(false);

        when(detallePrestamoRepository.findById(1))
                .thenReturn(
                        Optional.of(detalle)
                );

        when(
                detallePrestamoRepository
                        .findByPrestamo_IdPrestamoAndEstado(
                                1,
                                "prestado"
                        )
        ).thenReturn(List.of());

        DetallePrestamo resultado =
                prestamoService
                        .registrarDevolucion(1);

        assertEquals(
                "devuelto",
                resultado.getEstado()
        );

        assertEquals(
                LocalDate.now(),
                resultado.getFechaDevolucion()
        );

        assertTrue(
                libro.getEstado()
        );

        assertEquals(
                "cerrado",
                prestamo.getEstado()
        );

        verify(detallePrestamoRepository)
                .save(detalle);

        verify(libroRepository)
                .save(libro);

        verify(prestamoRepository)
                .save(prestamo);
    }

    @Test
    void registrarDevolucionNoDebeCerrarSiQuedanLibrosPrestados() {

        DetallePrestamo pendiente =
                new DetallePrestamo();

        pendiente.setIdDetallePrestamo(2);
        pendiente.setPrestamo(prestamo);
        pendiente.setEstado("prestado");

        when(detallePrestamoRepository.findById(1))
                .thenReturn(
                        Optional.of(detalle)
                );

        when(
                detallePrestamoRepository
                        .findByPrestamo_IdPrestamoAndEstado(
                                1,
                                "prestado"
                        )
        ).thenReturn(
                List.of(pendiente)
        );

        prestamoService
                .registrarDevolucion(1);

        assertEquals(
                "abierto",
                prestamo.getEstado()
        );

        verify(
                prestamoRepository,
                never()
        ).save(prestamo);
    }

    @Test
    void registrarDevolucionDuplicadaDebeLanzarExcepcion() {

        detalle.setEstado("devuelto");

        when(detallePrestamoRepository.findById(1))
                .thenReturn(
                        Optional.of(detalle)
                );

        IllegalArgumentException error =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                prestamoService
                                        .registrarDevolucion(1)
                );

        assertTrue(
                error.getMessage()
                        .contains(
                                "Devolución duplicada"
                        )
        );

        verify(
                libroRepository,
                never()
        ).save(any());
    }

        @Test
    void debeRechazarMasDeDosLibrosEnUnPrestamo() {

        NuevoPrestamoDTO dto =
                new NuevoPrestamoDTO();

        dto.setIdUsuario(1);
        dto.setIdComprobante(1);
        dto.setIdsLibros(
                List.of(10, 11, 12)
        );
        dto.setDias(
                List.of(2, 2, 2)
        );

        IllegalArgumentException error =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                prestamoService
                                        .registrarPrestamo(dto)
                );

        assertEquals(
                "Solo se permiten máximo 2 libros por préstamo.",
                error.getMessage()
        );

        verify(
                prestamoRepository,
                never()
        ).save(any());
    }

    @Test
    void debeRechazarPrestamoSiUsuarioSuperaDosLibrosPrestados() {

        NuevoPrestamoDTO dto =
                crearDtoValido();

        when(usuarioRepository.findById(1))
                .thenReturn(
                        Optional.of(usuario)
                );

        when(comprobanteRepository.findById(1))
                .thenReturn(
                        Optional.of(comprobante)
                );

        when(
                detallePrestamoRepository
                        .countByPrestamo_Usuario_IdUsuarioAndEstado(
                                1,
                                "prestado"
                        )
        ).thenReturn(2L);

        IllegalArgumentException error =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                prestamoService
                                        .registrarPrestamo(dto)
                );

        assertEquals(
                "El usuario no puede superar el máximo de 2 libros prestados.",
                error.getMessage()
        );

        verify(
                prestamoRepository,
                never()
        ).save(any());
    }

    private NuevoPrestamoDTO crearDtoValido() {

        NuevoPrestamoDTO dto =
                new NuevoPrestamoDTO();

        dto.setIdUsuario(1);
        dto.setIdComprobante(1);
        dto.setIdsLibros(
                List.of(10)
        );
        dto.setDias(
                List.of(2)
        );

        return dto;
    }
}

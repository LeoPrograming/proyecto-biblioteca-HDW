package com.biblioteca.app.service;

import com.biblioteca.app.entity.Usuario;
import com.biblioteca.app.model.DetallePrestamo;
import com.biblioteca.app.model.Libro;
import com.biblioteca.app.model.Prestamo;
import com.biblioteca.app.repository.DetallePrestamoRepository;
import com.biblioteca.app.repository.LibroRepository;
import com.biblioteca.app.repository.PrestamoRepository;
import com.biblioteca.app.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PrestamoServiceTest {

    @Mock
    private PrestamoRepository prestamoRepository;

    @Mock
    private DetallePrestamoRepository detallePrestamoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private PrestamoService prestamoService;

    private Usuario usuarioMock;
    private Libro libroMock;
    private Prestamo prestamoMock;
    private DetallePrestamo detalleMock;

    @BeforeEach
    void setUp() {

        usuarioMock = new Usuario();
        usuarioMock.setIdUsuario(1);

        libroMock = new Libro();
        libroMock.setIdLibro(1);
        libroMock.setEstado(true);

        prestamoMock = new Prestamo();
        prestamoMock.setIdPrestamo(1);
        prestamoMock.setUsuario(usuarioMock);
        prestamoMock.setEstado("abierto");

        detalleMock = new DetallePrestamo();
        detalleMock.setIdDetallePrestamo(1);
        detalleMock.setPrestamo(prestamoMock);
        detalleMock.setLibro(libroMock);
        detalleMock.setEstado("prestado");
    }

    @Test
    void registrarDevolucion_ExitosaYCierraPrestamo() {

        when(detallePrestamoRepository.findById(1))
                .thenReturn(Optional.of(detalleMock));

        when(detallePrestamoRepository
                .findByPrestamo_IdPrestamo(1))
                .thenReturn(List.of(detalleMock));

        DetallePrestamo resultado =
                prestamoService.registrarDevolucion(1);

        assertEquals(
                "devuelto",
                resultado.getEstado()
        );

        assertEquals(
                LocalDate.now(),
                resultado.getFechaDevolucion()
        );

        assertTrue(
                libroMock.getEstado()
        );

        assertEquals(
                "cerrado",
                prestamoMock.getEstado()
        );

        verify(detallePrestamoRepository)
                .save(detalleMock);

        verify(libroRepository)
                .save(libroMock);

        verify(prestamoRepository)
                .save(prestamoMock);
    }

    @Test
    void registrarDevolucion_Duplicada_LanzaExcepcion() {

        detalleMock.setEstado("devuelto");

        when(detallePrestamoRepository.findById(1))
                .thenReturn(Optional.of(detalleMock));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> prestamoService
                                .registrarDevolucion(1)
                );

        assertTrue(
                exception
                        .getMessage()
                        .contains("Devolución duplicada")
        );

        verify(
                libroRepository,
                never()
        ).save(any());
    }
}
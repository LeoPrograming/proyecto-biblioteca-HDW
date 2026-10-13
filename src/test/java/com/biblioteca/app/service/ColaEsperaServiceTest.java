package com.biblioteca.app.service;

import com.biblioteca.app.entity.Usuario;
import com.biblioteca.app.model.ColaEspera;
import com.biblioteca.app.model.Libro;
import com.biblioteca.app.repository.ColaEsperaRepository;
import com.biblioteca.app.repository.LibroRepository;
import com.biblioteca.app.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ColaEsperaServiceTest {

    @Mock
    private ColaEsperaRepository colaEsperaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private ColaEsperaService colaEsperaService;

    private Usuario usuario;
    private Libro libro;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1);

        libro = new Libro();
        libro.setIdLibro(10);
        libro.setEstado(false);
    }

    @Test
    void debeRegistrarUsuarioEnCola() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(libroRepository.findById(10)).thenReturn(Optional.of(libro));
        when(colaEsperaRepository
                .existsByUsuario_IdUsuarioAndLibro_IdLibroAndEstado(1, 10, "en_espera"))
                .thenReturn(false);
        when(colaEsperaRepository.save(any(ColaEspera.class)))
                .thenAnswer(invocacion -> {
                    ColaEspera cola = invocacion.getArgument(0);
                    cola.setIdCola(100);
                    return cola;
                });

        ColaEspera resultado = colaEsperaService.agregarUsuarioACola(1, 10);

        assertEquals(100, resultado.getIdCola());
        assertSame(usuario, resultado.getUsuario());
        assertSame(libro, resultado.getLibro());
        assertEquals("en_espera", resultado.getEstado());
        assertNotNull(resultado.getFechaRegistro());
        verify(colaEsperaRepository).save(any(ColaEspera.class));
    }

    @Test
    void debeRechazarRegistroDuplicadoActivo() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(libroRepository.findById(10)).thenReturn(Optional.of(libro));
        when(colaEsperaRepository
                .existsByUsuario_IdUsuarioAndLibro_IdLibroAndEstado(1, 10, "en_espera"))
                .thenReturn(true);

        assertThrows(
                IllegalStateException.class,
                () -> colaEsperaService.agregarUsuarioACola(1, 10)
        );

        verify(colaEsperaRepository, never()).save(any());
    }

    @Test
    void debeRechazarUsuarioInexistente() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> colaEsperaService.agregarUsuarioACola(1, 10)
        );

        assertEquals("Usuario no encontrado.", excepcion.getMessage());
        verify(libroRepository, never()).findById(any());
        verify(colaEsperaRepository, never()).save(any());
    }

    @Test
    void debeRechazarLibroInexistente() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(libroRepository.findById(10)).thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> colaEsperaService.agregarUsuarioACola(1, 10)
        );

        assertEquals("Libro no encontrado.", excepcion.getMessage());
        verify(colaEsperaRepository, never()).save(any());
    }

    @Test
    void debeRechazarLibroDisponible() {
        libro.setEstado(true);
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(libroRepository.findById(10)).thenReturn(Optional.of(libro));

        assertThrows(
                IllegalStateException.class,
                () -> colaEsperaService.agregarUsuarioACola(1, 10)
        );

        verify(colaEsperaRepository, never()).save(any());
    }

    @Test
    void debeMarcarRegistroComoAtendido() {
        ColaEspera cola = new ColaEspera();
        cola.setIdCola(20);
        cola.setEstado("en_espera");

        when(colaEsperaRepository.findById(20)).thenReturn(Optional.of(cola));
        when(colaEsperaRepository.save(cola)).thenReturn(cola);

        ColaEspera resultado = colaEsperaService.marcarComoAtendido(20);

        assertEquals("atendido", resultado.getEstado());
        verify(colaEsperaRepository).save(cola);
    }

    @Test
    void debeConsultarColaPorLibro() {
        ColaEspera cola = new ColaEspera();
        when(colaEsperaRepository.findByLibro_IdLibro(10)).thenReturn(List.of(cola));

        List<ColaEspera> resultado = colaEsperaService.consultarColaPorLibro(10);

        assertEquals(1, resultado.size());
        verify(colaEsperaRepository).findByLibro_IdLibro(10);
    }

    @Test
    void debeConsultarRegistrosPorUsuario() {
        ColaEspera cola = new ColaEspera();
        when(colaEsperaRepository.findByUsuario_IdUsuario(1)).thenReturn(List.of(cola));

        List<ColaEspera> resultado = colaEsperaService.consultarRegistrosPorUsuario(1);

        assertEquals(1, resultado.size());
        verify(colaEsperaRepository).findByUsuario_IdUsuario(1);
    }
}

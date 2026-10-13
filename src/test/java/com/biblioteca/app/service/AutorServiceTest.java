
package com.biblioteca.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.biblioteca.app.model.Autor;
import com.biblioteca.app.repository.AutorRepository;

@ExtendWith(MockitoExtension.class)
class AutorServiceTest {

    @Mock
    private AutorRepository autorRepository;

    @InjectMocks
    private AutorService autorService;

    // 1. Listar autores
    @Test
    void debeListarAutores() {
        Autor autor1 = new Autor();
        autor1.setIdAutor(1);
        autor1.setNombres("Gabriel");

        Autor autor2 = new Autor();
        autor2.setIdAutor(2);
        autor2.setNombres("Mario");

        when(autorRepository.findAll())
                .thenReturn(List.of(autor1, autor2));

        List<Autor> resultado = autorService.listarAutores();

        assertEquals(2, resultado.size());
        assertEquals("Gabriel", resultado.get(0).getNombres());
        assertEquals("Mario", resultado.get(1).getNombres());

        verify(autorRepository).findAll();
    }

    // 2. Buscar autor existente
    @Test
    void debeBuscarAutorPorId() {
        Autor autor = new Autor();
        autor.setIdAutor(1);
        autor.setNombres("Gabriel");
        autor.setApellidos("Garcia Marquez");

        when(autorRepository.findById(1))
                .thenReturn(Optional.of(autor));

        Optional<Autor> resultado =
                autorService.obtenerAutorPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals("Gabriel", resultado.get().getNombres());
        assertEquals("Garcia Marquez",
                resultado.get().getApellidos());

        verify(autorRepository).findById(1);
    }

    // 3. Buscar autor inexistente
    @Test
    void debeRetornarVacioSiAutorNoExiste() {
        when(autorRepository.findById(999))
                .thenReturn(Optional.empty());

        Optional<Autor> resultado =
                autorService.obtenerAutorPorId(999);

        assertTrue(resultado.isEmpty());

        verify(autorRepository).findById(999);
    }

    // 4. Registrar autor
    @Test
    void debeRegistrarAutor() {
        Autor datos = new Autor();
        datos.setNombres("Mario");
        datos.setApellidos("Vargas Llosa");

        when(autorRepository.save(any(Autor.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Autor resultado = autorService.registrarAutor(datos);

        assertEquals("Mario", resultado.getNombres());
        assertEquals("Vargas Llosa", resultado.getApellidos());
        assertTrue(resultado.getEstado());

        verify(autorRepository).save(any(Autor.class));
    }

    // 5. Actualizar autor existente
    @Test
    void debeActualizarAutor() {
        Autor existente = new Autor();
        existente.setIdAutor(1);
        existente.setNombres("Nombre anterior");
        existente.setEstado(true);

        Autor datos = new Autor();
        datos.setNombres("Nombre actualizado");
        datos.setApellidos("Apellido actualizado");

        when(autorRepository.findById(1))
                .thenReturn(Optional.of(existente));

        when(autorRepository.save(any(Autor.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Autor resultado =
                autorService.actualizarAutor(1, datos);

        assertEquals("Nombre actualizado",
                resultado.getNombres());

        assertEquals("Apellido actualizado",
                resultado.getApellidos());

        assertTrue(resultado.getEstado());

        verify(autorRepository).save(existente);
    }

    // 6. Actualizar autor inexistente
    @Test
    void debeRechazarActualizacionDeAutorInexistente() {
        Autor datos = new Autor();
        datos.setNombres("Autor de prueba");

        when(autorRepository.findById(999))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> autorService.actualizarAutor(999, datos));

        verify(autorRepository, never())
                .save(any(Autor.class));
    }

    // 7. Rechazar nombre vacio
    @Test
    void debeRechazarAutorSinNombre() {
        Autor datos = new Autor();
        datos.setNombres("   ");

        assertThrows(IllegalArgumentException.class,
                () -> autorService.registrarAutor(datos));

        verify(autorRepository, never())
                .save(any(Autor.class));
    }
}

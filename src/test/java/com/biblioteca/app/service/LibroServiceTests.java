package com.biblioteca.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.biblioteca.app.model.Autor;
import com.biblioteca.app.model.Categoria;
import com.biblioteca.app.model.Editorial;
import com.biblioteca.app.model.Libro;
import com.biblioteca.app.repository.LibroRepository;

@ExtendWith(MockitoExtension.class)
class LibroServiceTests {

    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private LibroService libroService;

    @Test
    void debeListarLosLibrosExistentes() {
        Libro libro1 = new Libro();
        libro1.setIdLibro(1);
        libro1.setTitulo("Libro uno");

        Libro libro2 = new Libro();
        libro2.setIdLibro(2);
        libro2.setTitulo("Libro dos");

        when(libroRepository.findAll()).thenReturn(List.of(libro1, libro2));

        List<Libro> resultado = libroService.listarLibros();

        assertEquals(2, resultado.size());
        assertSame(libro1, resultado.get(0));
        assertSame(libro2, resultado.get(1));

        verify(libroRepository).findAll();
    }

    @Test
    void debeRecuperarUnLibroExistentePorId() {
        Libro libro = new Libro();
        libro.setIdLibro(10);
        libro.setTitulo("Libro encontrado");

        when(libroRepository.findById(10)).thenReturn(Optional.of(libro));

        Optional<Libro> resultado = libroService.obtenerLibroPorId(10);

        assertTrue(resultado.isPresent());
        assertSame(libro, resultado.get());

        verify(libroRepository).findById(10);
    }

    @Test
    void debeRetornarOptionalVacioCuandoElLibroNoExiste() {
        when(libroRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Libro> resultado = libroService.obtenerLibroPorId(999);

        assertTrue(resultado.isEmpty());

        verify(libroRepository).findById(999);
    }

    @Test
    void debeMantenerAccesoALasRelacionesDelCatalogo() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Tecnologia");

        Editorial editorial = new Editorial();
        editorial.setNombre("Editorial STM");

        Autor autor = new Autor();
        autor.setNombres("Juan");
        autor.setApellidos("Perez");

        Libro libro = new Libro();
        libro.setIdLibro(20);
        libro.setTitulo("Libro con relaciones");
        libro.setCategoria(categoria);
        libro.setEditorial(editorial);
        libro.setAutores(List.of(autor));

        when(libroRepository.findById(20)).thenReturn(Optional.of(libro));

        Libro resultado = libroService.obtenerLibroPorId(20).orElseThrow();

        assertSame(categoria, resultado.getCategoria());
        assertSame(editorial, resultado.getEditorial());
        assertEquals(1, resultado.getAutores().size());
        assertSame(autor, resultado.getAutores().get(0));

        verify(libroRepository).findById(20);
    }
}

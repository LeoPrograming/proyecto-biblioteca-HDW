package com.biblioteca.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
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

import com.biblioteca.app.repository.AutorRepository;
import com.biblioteca.app.repository.CategoriaRepository;
import com.biblioteca.app.repository.EditorialRepository;
import com.biblioteca.app.repository.LibroRepository;

@ExtendWith(MockitoExtension.class)
class LibroServiceTests {

    @Mock
    private LibroRepository libroRepository;

    @Mock
    private AutorRepository autorRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private EditorialRepository editorialRepository;

    @InjectMocks
    private LibroService libroService;

    // PRUEBAS DE LA ISSUE 10

    @Test
    void debeListarLosLibrosExistentes() {

        Libro libro1 = new Libro();
        libro1.setIdLibro(1);
        libro1.setTitulo("Libro uno");

        Libro libro2 = new Libro();
        libro2.setIdLibro(2);
        libro2.setTitulo("Libro dos");

        when(libroRepository.findAll())
                .thenReturn(List.of(libro1, libro2));

        List<Libro> resultado =
                libroService.listarLibros();

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

        when(libroRepository.findById(10))
                .thenReturn(Optional.of(libro));

        Optional<Libro> resultado =
                libroService.obtenerLibroPorId(10);

        assertTrue(resultado.isPresent());
        assertSame(libro, resultado.get());

        verify(libroRepository).findById(10);
    }

    @Test
    void debeRetornarOptionalVacioCuandoElLibroNoExiste() {

        when(libroRepository.findById(999))
                .thenReturn(Optional.empty());

        Optional<Libro> resultado =
                libroService.obtenerLibroPorId(999);

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

        when(libroRepository.findById(20))
                .thenReturn(Optional.of(libro));

        Libro resultado =
                libroService.obtenerLibroPorId(20)
                        .orElseThrow();

        assertSame(categoria, resultado.getCategoria());
        assertSame(editorial, resultado.getEditorial());
        assertEquals(1, resultado.getAutores().size());
        assertSame(autor, resultado.getAutores().get(0));

        verify(libroRepository).findById(20);
    }

    // PRUEBAS ISSUE 20 / ISSUE 27

    @Test
    void debeRegistrarLibroCorrectamente() {

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1);

        Editorial editorial = new Editorial();
        editorial.setIdEditorial(2);

        Autor autor = new Autor();
        autor.setIdAutor(3);

        Libro datos = new Libro();
        datos.setTitulo("Programacion en Java");
        datos.setIsbn("9781234567890");
        datos.setFechaPublic(LocalDate.of(2026, 10, 27));
        datos.setPrecioTiempo(new BigDecimal("85.50"));
        datos.setImagen("java.jpg");
        datos.setCategoria(categoria);
        datos.setEditorial(editorial);
        datos.setAutores(List.of(autor));

        when(categoriaRepository.findById(1))
                .thenReturn(Optional.of(categoria));

        when(editorialRepository.findById(2))
                .thenReturn(Optional.of(editorial));

        when(autorRepository.findById(3))
                .thenReturn(Optional.of(autor));

        when(libroRepository.save(any(Libro.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Libro resultado =
                libroService.registrarLibro(datos);

        assertEquals(
                "Programacion en Java",
                resultado.getTitulo()
        );

        assertEquals(
                "9781234567890",
                resultado.getIsbn()
        );

        assertEquals(
                LocalDate.of(2026, 10, 27),
                resultado.getFechaPublic()
        );

        assertEquals(
                new BigDecimal("85.50"),
                resultado.getPrecioTiempo()
        );

        assertEquals(
                "java.jpg",
                resultado.getImagen()
        );

        assertTrue(resultado.getEstado());

        assertSame(
                categoria,
                resultado.getCategoria()
        );

        assertSame(
                editorial,
                resultado.getEditorial()
        );

        assertEquals(
                1,
                resultado.getAutores().size()
        );

        verify(libroRepository)
                .save(any(Libro.class));
    }

    @Test
    void debeActualizarLibroCorrectamente() {

        Libro existente = new Libro();
        existente.setIdLibro(1);
        existente.setTitulo("Titulo anterior");
        existente.setIsbn("1111111111111");

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(2);

        Libro datos = new Libro();
        datos.setTitulo("Titulo actualizado");
        datos.setIsbn("9781234567891");
        datos.setFechaPublic(LocalDate.of(2025, 5, 15));
        datos.setPrecioTiempo(new BigDecimal("100.00"));
        datos.setImagen("actualizado.jpg");
        datos.setCategoria(categoria);

        when(libroRepository.findById(1))
                .thenReturn(Optional.of(existente));

        when(categoriaRepository.findById(2))
                .thenReturn(Optional.of(categoria));

        when(libroRepository.save(any(Libro.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Libro resultado =
                libroService.actualizarLibro(
                        1,
                        datos
                );

        assertEquals(
                "Titulo actualizado",
                resultado.getTitulo()
        );

        assertEquals(
                "9781234567891",
                resultado.getIsbn()
        );

        assertEquals(
                LocalDate.of(2025, 5, 15),
                resultado.getFechaPublic()
        );

        assertEquals(
                new BigDecimal("100.00"),
                resultado.getPrecioTiempo()
        );

        assertEquals(
                "actualizado.jpg",
                resultado.getImagen()
        );

        assertSame(
                categoria,
                resultado.getCategoria()
        );

        verify(libroRepository)
                .save(existente);
    }

    @Test
    void debeCambiarEstadoDelLibro() {

        Libro libro = new Libro();
        libro.setIdLibro(1);
        libro.setEstado(true);

        when(libroRepository.findById(1))
                .thenReturn(Optional.of(libro));

        when(libroRepository.save(any(Libro.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Libro resultado =
                libroService.cambiarEstado(
                        1,
                        false
                );

        assertFalse(resultado.getEstado());

        verify(libroRepository)
                .save(libro);
    }

    @Test
    void debeRechazarCategoriaInexistente() {

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(999);

        Libro datos = new Libro();
        datos.setTitulo("Libro de prueba");
        datos.setIsbn("ISBN-CAT-999");
        datos.setCategoria(categoria);

        when(categoriaRepository.findById(999))
                .thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> libroService.registrarLibro(datos)
        );

        verify(libroRepository, never())
                .save(any(Libro.class));
    }

    @Test
    void debeRechazarEditorialInexistente() {

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1);

        Editorial editorial = new Editorial();
        editorial.setIdEditorial(999);

        Libro datos = new Libro();
        datos.setTitulo("Libro de prueba");
        datos.setIsbn("ISBN-EDI-999");
        datos.setCategoria(categoria);
        datos.setEditorial(editorial);

        when(categoriaRepository.findById(1))
                .thenReturn(Optional.of(categoria));

        when(editorialRepository.findById(999))
                .thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> libroService.registrarLibro(datos)
        );

        verify(libroRepository, never())
                .save(any(Libro.class));
    }

    @Test
    void debeRechazarAutorInexistente() {

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1);

        Autor autor = new Autor();
        autor.setIdAutor(999);

        Libro datos = new Libro();
        datos.setTitulo("Libro de prueba");
        datos.setIsbn("ISBN-AUTOR-999");
        datos.setCategoria(categoria);
        datos.setAutores(List.of(autor));

        when(categoriaRepository.findById(1))
                .thenReturn(Optional.of(categoria));

        when(autorRepository.findById(999))
                .thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> libroService.registrarLibro(datos)
        );

        verify(libroRepository, never())
                .save(any(Libro.class));
    }

    @Test
    void debeRechazarTituloVacio() {

        Libro datos = new Libro();
        datos.setTitulo("   ");

        assertThrows(
                IllegalArgumentException.class,
                () -> libroService.registrarLibro(datos)
        );

        verify(libroRepository, never())
                .save(any(Libro.class));
    }

    @Test
    void debeRechazarActualizarLibroInexistente() {

        Libro datos = new Libro();
        datos.setTitulo("Libro actualizado");

        when(libroRepository.findById(999))
                .thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> libroService.actualizarLibro(
                        999,
                        datos
                )
        );

        verify(libroRepository, never())
                .save(any(Libro.class));
    }
}

package com.biblioteca.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.biblioteca.app.model.Autor;
import com.biblioteca.app.model.Categoria;
import com.biblioteca.app.model.Editorial;
import com.biblioteca.app.model.Libro;
import com.biblioteca.app.service.LibroService;

@ExtendWith(MockitoExtension.class)
class LibroControllerTest {

    @Mock
    private LibroService libroService;

    private MockMvc mockMvc;
    private Libro libro;

    private static final String LIBRO_JSON = """
            {
                "titulo": "Programacion en Java",
                "isbn": "9781234567890",
                "stock": 10,
                "precio": 85.50,
                "estado": true,
                "categoria": {
                    "idCategoria": 1
                },
                "editorial": {
                    "idEditorial": 2
                },
                "autores": [
                    {
                        "idAutor": 3
                    }
                ]
            }
            """;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new LibroController(libroService))
                .build();

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1);
        categoria.setNombre("Programacion");

        Editorial editorial = new Editorial();
        editorial.setIdEditorial(2);
        editorial.setNombre("Editorial STM");

        Autor autor = new Autor();
        autor.setIdAutor(3);
        autor.setNombres("Juan");
        autor.setApellidos("Perez");

        libro = new Libro();
        libro.setIdLibro(10);
        libro.setTitulo("Programacion en Java");
        libro.setIsbn("9781234567890");
        libro.setStock(10);
        libro.setPrecio(85.50);
        libro.setEstado(true);
        libro.setCategoria(categoria);
        libro.setEditorial(editorial);
        libro.setAutores(List.of(autor));
    }

    // 1. Listar libros
    @Test
    void debeListarLibros() throws Exception {
        when(libroService.listarLibros())
                .thenReturn(List.of(libro));

        mockMvc.perform(get("/api/libros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idLibro").value(10))
                .andExpect(jsonPath("$[0].titulo")
                        .value("Programacion en Java"))
                .andExpect(jsonPath("$[0].idCategoria").value(1))
                .andExpect(jsonPath("$[0].idEditorial").value(2))
                .andExpect(jsonPath("$[0].autores[0].idAutor")
                        .value(3));

        verify(libroService).listarLibros();
    }

    // 2. Buscar libro existente
    @Test
    void debeBuscarLibroPorId() throws Exception {
        when(libroService.obtenerLibroPorId(10))
                .thenReturn(Optional.of(libro));

        mockMvc.perform(get("/api/libros/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idLibro").value(10))
                .andExpect(jsonPath("$.titulo")
                        .value("Programacion en Java"))
                .andExpect(jsonPath("$.isbn")
                        .value("9781234567890"));

        verify(libroService).obtenerLibroPorId(10);
    }

    // 3. Buscar libro inexistente
    @Test
    void debeRetornar404SiLibroNoExiste() throws Exception {
        when(libroService.obtenerLibroPorId(999))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/libros/999"))
                .andExpect(status().isNotFound());

        verify(libroService).obtenerLibroPorId(999);
    }

    // 4. Registrar libro
    @Test
    void debeRegistrarLibro() throws Exception {
        when(libroService.registrarLibro(any(Libro.class)))
                .thenReturn(libro);

        mockMvc.perform(post("/api/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LIBRO_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idLibro").value(10))
                .andExpect(jsonPath("$.titulo")
                        .value("Programacion en Java"))
                .andExpect(jsonPath("$.stock").value(10))
                .andExpect(jsonPath("$.estado").value(true));

        verify(libroService).registrarLibro(any(Libro.class));
    }

    // 5. Actualizar libro
    @Test
    void debeActualizarLibro() throws Exception {
        libro.setTitulo("Java avanzado");

        when(libroService.actualizarLibro(
                eq(10), any(Libro.class)))
                .thenReturn(libro);

        mockMvc.perform(put("/api/libros/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LIBRO_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idLibro").value(10))
                .andExpect(jsonPath("$.titulo")
                        .value("Java avanzado"));

        verify(libroService).actualizarLibro(
                eq(10), any(Libro.class));
    }

    // 6. Cambiar estado
    @Test
    void debeCambiarEstadoDelLibro() throws Exception {
        libro.setEstado(false);

        when(libroService.cambiarEstado(10, false))
                .thenReturn(libro);

        mockMvc.perform(patch("/api/libros/10/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "estado": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idLibro").value(10))
                .andExpect(jsonPath("$.estado").value(false));

        verify(libroService).cambiarEstado(10, false);
    }

    // 7. Rechazar categoria inexistente
    @Test
    void debeRechazarCategoriaInexistente() throws Exception {
        when(libroService.registrarLibro(any(Libro.class)))
                .thenThrow(new NoSuchElementException(
                        "La categoria indicada no existe"
                ));

        mockMvc.perform(post("/api/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LIBRO_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje")
                        .value("La categoria indicada no existe"));

        verify(libroService).registrarLibro(any(Libro.class));
    }

    // 8. Rechazar editorial inexistente
    @Test
    void debeRechazarEditorialInexistente() throws Exception {
        when(libroService.registrarLibro(any(Libro.class)))
                .thenThrow(new NoSuchElementException(
                        "La editorial indicada no existe"
                ));

        mockMvc.perform(post("/api/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LIBRO_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje")
                        .value("La editorial indicada no existe"));

        verify(libroService).registrarLibro(any(Libro.class));
    }

    // 9. Rechazar titulo invalido
    @Test
    void debeRechazarLibroConTituloVacio() throws Exception {
        when(libroService.registrarLibro(any(Libro.class)))
                .thenThrow(new IllegalArgumentException(
                        "El titulo del libro es obligatorio"
                ));

        mockMvc.perform(post("/api/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje")
                        .value("El titulo del libro es obligatorio"));
    }

    // 10. Rechazar actualizacion de libro inexistente
    @Test
    void debeRechazarActualizacionDeLibroInexistente()
            throws Exception {

        when(libroService.actualizarLibro(
                eq(999), any(Libro.class)))
                .thenThrow(new NoSuchElementException(
                        "Libro con ID 999 no encontrado"
                ));

        mockMvc.perform(put("/api/libros/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LIBRO_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje")
                        .value("Libro con ID 999 no encontrado"));
    }

    // 11. Rechazar cambio de estado sin valor
    @Test
    void debeRechazarCambioDeEstadoSinValor()
            throws Exception {

        when(libroService.cambiarEstado(10, null))
                .thenThrow(new IllegalArgumentException(
                        "El estado del libro es obligatorio"
                ));

        mockMvc.perform(patch("/api/libros/10/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje")
                        .value("El estado del libro es obligatorio"));
    }
}

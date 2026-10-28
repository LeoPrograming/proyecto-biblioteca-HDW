package com.biblioteca.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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
                "fechaPublic": "2026-10-27",
                "precioTiempo": 85.50,
                "imagen": "java.jpg",
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
                .standaloneSetup(
                        new LibroController(libroService)
                )
                .build();

        Categoria categoria =
                new Categoria();

        categoria.setIdCategoria(1);
        categoria.setNombre("Programacion");

        Editorial editorial =
                new Editorial();

        editorial.setIdEditorial(2);
        editorial.setNombre("Editorial STM");

        Autor autor =
                new Autor();

        autor.setIdAutor(3);
        autor.setNombres("Juan");
        autor.setApellidos("Perez");

        libro = new Libro();

        libro.setIdLibro(10);
        libro.setTitulo("Programacion en Java");
        libro.setIsbn("9781234567890");

        libro.setFechaPublic(
                LocalDate.of(
                        2026,
                        10,
                        27
                )
        );

        libro.setPrecioTiempo(
                new BigDecimal("85.50")
        );

        libro.setImagen(
                "java.jpg"
        );

        libro.setEstado(true);
        libro.setCategoria(categoria);
        libro.setEditorial(editorial);
        libro.setAutores(
                List.of(autor)
        );
    }

    @Test
    void debeListarLibros()
            throws Exception {

        when(libroService.listarLibros())
                .thenReturn(
                        List.of(libro)
                );

        mockMvc.perform(
                        get("/api/libros")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].idLibro")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$[0].titulo")
                                .value("Programacion en Java")
                )
                .andExpect(
                        jsonPath("$[0].fechaPublic")
                                .value("2026-10-27")
                )
                .andExpect(
                        jsonPath("$[0].precioTiempo")
                                .value(85.50)
                )
                .andExpect(
                        jsonPath("$[0].imagen")
                                .value("java.jpg")
                )
                .andExpect(
                        jsonPath("$[0].idCategoria")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].idEditorial")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].autores[0].idAutor")
                                .value(3)
                );

        verify(libroService)
                .listarLibros();
    }

    @Test
    void debeBuscarLibroPorId()
            throws Exception {

        when(
                libroService.obtenerLibroPorId(10)
        ).thenReturn(
                Optional.of(libro)
        );

        mockMvc.perform(
                        get("/api/libros/10")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.idLibro")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.titulo")
                                .value("Programacion en Java")
                )
                .andExpect(
                        jsonPath("$.isbn")
                                .value("9781234567890")
                )
                .andExpect(
                        jsonPath("$.precioTiempo")
                                .value(85.50)
                );

        verify(libroService)
                .obtenerLibroPorId(10);
    }

    @Test
    void debeRetornar404SiLibroNoExiste()
            throws Exception {

        when(
                libroService.obtenerLibroPorId(999)
        ).thenReturn(
                Optional.empty()
        );

        mockMvc.perform(
                        get("/api/libros/999")
                )
                .andExpect(
                        status().isNotFound()
                );

        verify(libroService)
                .obtenerLibroPorId(999);
    }

    @Test
    void debeRegistrarLibro()
            throws Exception {

        when(
                libroService.registrarLibro(
                        any(Libro.class)
                )
        ).thenReturn(libro);

        mockMvc.perform(
                        post("/api/libros")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(LIBRO_JSON)
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.idLibro")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.titulo")
                                .value("Programacion en Java")
                )
                .andExpect(
                        jsonPath("$.fechaPublic")
                                .value("2026-10-27")
                )
                .andExpect(
                        jsonPath("$.precioTiempo")
                                .value(85.50)
                )
                .andExpect(
                        jsonPath("$.imagen")
                                .value("java.jpg")
                )
                .andExpect(
                        jsonPath("$.estado")
                                .value(true)
                );

        verify(libroService)
                .registrarLibro(
                        any(Libro.class)
                );
    }

    @Test
    void debeActualizarLibro()
            throws Exception {

        libro.setTitulo(
                "Java avanzado"
        );

        when(
                libroService.actualizarLibro(
                        eq(10),
                        any(Libro.class)
                )
        ).thenReturn(libro);

        mockMvc.perform(
                        put("/api/libros/10")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(LIBRO_JSON)
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.idLibro")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.titulo")
                                .value("Java avanzado")
                );

        verify(libroService)
                .actualizarLibro(
                        eq(10),
                        any(Libro.class)
                );
    }

    @Test
    void debeCambiarEstadoDelLibro()
            throws Exception {

        libro.setEstado(false);

        when(
                libroService.cambiarEstado(
                        10,
                        false
                )
        ).thenReturn(libro);

        mockMvc.perform(
                        patch(
                                "/api/libros/10/estado"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                            "estado": false
                                        }
                                        """)
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.idLibro")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.estado")
                                .value(false)
                );

        verify(libroService)
                .cambiarEstado(
                        10,
                        false
                );
    }

    @Test
    void debeRechazarCategoriaInexistente()
            throws Exception {

        when(
                libroService.registrarLibro(
                        any(Libro.class)
                )
        ).thenThrow(
                new NoSuchElementException(
                        "La categoria indicada no existe"
                )
        );

        mockMvc.perform(
                        post("/api/libros")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(LIBRO_JSON)
                )
                .andExpect(
                        status().isNotFound()
                )
                .andExpect(
                        jsonPath("$.mensaje")
                                .value(
                                        "La categoria indicada no existe"
                                )
                );
    }

    @Test
    void debeRechazarEditorialInexistente()
            throws Exception {

        when(
                libroService.registrarLibro(
                        any(Libro.class)
                )
        ).thenThrow(
                new NoSuchElementException(
                        "La editorial indicada no existe"
                )
        );

        mockMvc.perform(
                        post("/api/libros")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(LIBRO_JSON)
                )
                .andExpect(
                        status().isNotFound()
                )
                .andExpect(
                        jsonPath("$.mensaje")
                                .value(
                                        "La editorial indicada no existe"
                                )
                );
    }

    @Test
    void debeRechazarLibroConTituloVacio()
            throws Exception {

        when(
                libroService.registrarLibro(
                        any(Libro.class)
                )
        ).thenThrow(
                new IllegalArgumentException(
                        "El titulo del libro es obligatorio"
                )
        );

        mockMvc.perform(
                        post("/api/libros")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                            "titulo": ""
                                        }
                                        """)
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.mensaje")
                                .value(
                                        "El titulo del libro es obligatorio"
                                )
                );
    }

    @Test
    void debeRechazarActualizacionDeLibroInexistente()
            throws Exception {

        when(
                libroService.actualizarLibro(
                        eq(999),
                        any(Libro.class)
                )
        ).thenThrow(
                new NoSuchElementException(
                        "Libro con ID 999 no encontrado"
                )
        );

        mockMvc.perform(
                        put("/api/libros/999")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(LIBRO_JSON)
                )
                .andExpect(
                        status().isNotFound()
                )
                .andExpect(
                        jsonPath("$.mensaje")
                                .value(
                                        "Libro con ID 999 no encontrado"
                                )
                );
    }

    @Test
    void debeRechazarCambioDeEstadoSinValor()
            throws Exception {

        when(
                libroService.cambiarEstado(
                        10,
                        null
                )
        ).thenThrow(
                new IllegalArgumentException(
                        "El estado del libro es obligatorio"
                )
        );

        mockMvc.perform(
                        patch(
                                "/api/libros/10/estado"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("{}")
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.mensaje")
                                .value(
                                        "El estado del libro es obligatorio"
                                )
                );
    }
}
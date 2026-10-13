
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
import com.biblioteca.app.service.AutorService;

@ExtendWith(MockitoExtension.class)
class AutorControllerTest {

    @Mock
    private AutorService autorService;

    private MockMvc mockMvc;
    private Autor autor;

    private static final String AUTOR_JSON = """
            {
                "nombres": "Gabriel",
                "apellidos": "Garcia Marquez",
                "estado": true
            }
            """;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AutorController(autorService))
                .build();

        autor = new Autor();
        autor.setIdAutor(1);
        autor.setNombres("Gabriel");
        autor.setApellidos("Garcia Marquez");
        autor.setEstado(true);
    }

    // 1. Listar autores
    @Test
    void debeListarAutores() throws Exception {
        when(autorService.listarAutores())
                .thenReturn(List.of(autor));

        mockMvc.perform(get("/api/autores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idAutor").value(1))
                .andExpect(jsonPath("$[0].nombres")
                        .value("Gabriel"))
                .andExpect(jsonPath("$[0].apellidos")
                        .value("Garcia Marquez"))
                .andExpect(jsonPath("$[0].estado").value(true));

        verify(autorService).listarAutores();
    }

    // 2. Listar autores cuando no existen registros
    @Test
    void debeRetornarListaVacia() throws Exception {
        when(autorService.listarAutores())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/autores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(autorService).listarAutores();
    }

    // 3. Buscar autor existente
    @Test
    void debeBuscarAutorPorId() throws Exception {
        when(autorService.obtenerAutorPorId(1))
                .thenReturn(Optional.of(autor));

        mockMvc.perform(get("/api/autores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAutor").value(1))
                .andExpect(jsonPath("$.nombres")
                        .value("Gabriel"))
                .andExpect(jsonPath("$.apellidos")
                        .value("Garcia Marquez"));

        verify(autorService).obtenerAutorPorId(1);
    }

    // 4. Buscar autor inexistente
    @Test
    void debeRetornar404SiAutorNoExiste() throws Exception {
        when(autorService.obtenerAutorPorId(999))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/autores/999"))
                .andExpect(status().isNotFound());

        verify(autorService).obtenerAutorPorId(999);
    }

    // 5. Registrar autor
    @Test
    void debeRegistrarAutor() throws Exception {
        when(autorService.registrarAutor(any(Autor.class)))
                .thenReturn(autor);

        mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(AUTOR_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idAutor").value(1))
                .andExpect(jsonPath("$.nombres")
                        .value("Gabriel"))
                .andExpect(jsonPath("$.apellidos")
                        .value("Garcia Marquez"))
                .andExpect(jsonPath("$.estado").value(true));

        verify(autorService).registrarAutor(any(Autor.class));
    }

    // 6. Actualizar autor existente
    @Test
    void debeActualizarAutor() throws Exception {
        autor.setNombres("Gabriel actualizado");
        autor.setApellidos("Apellido actualizado");

        when(autorService.actualizarAutor(
                eq(1), any(Autor.class)))
                .thenReturn(autor);

        mockMvc.perform(put("/api/autores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(AUTOR_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAutor").value(1))
                .andExpect(jsonPath("$.nombres")
                        .value("Gabriel actualizado"))
                .andExpect(jsonPath("$.apellidos")
                        .value("Apellido actualizado"));

        verify(autorService).actualizarAutor(
                eq(1), any(Autor.class));
    }

    // 7. Actualizar autor inexistente
    @Test
    void debeRechazarActualizacionDeAutorInexistente()
            throws Exception {

        when(autorService.actualizarAutor(
                eq(999), any(Autor.class)))
                .thenThrow(new NoSuchElementException(
                        "Autor con ID 999 no encontrado"
                ));

        mockMvc.perform(put("/api/autores/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(AUTOR_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje")
                        .value("Autor con ID 999 no encontrado"));

        verify(autorService).actualizarAutor(
                eq(999), any(Autor.class));
    }

    // 8. Rechazar registro sin nombre
    @Test
    void debeRechazarAutorSinNombre() throws Exception {
        when(autorService.registrarAutor(any(Autor.class)))
                .thenThrow(new IllegalArgumentException(
                        "El nombre del autor es obligatorio"
                ));

        mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombres": "",
                                    "apellidos": "Perez"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje")
                        .value("El nombre del autor es obligatorio"));

        verify(autorService).registrarAutor(any(Autor.class));
    }

    // 9. Rechazar actualizacion con nombre invalido
    @Test
    void debeRechazarActualizacionConNombreVacio()
            throws Exception {

        when(autorService.actualizarAutor(
                eq(1), any(Autor.class)))
                .thenThrow(new IllegalArgumentException(
                        "El nombre del autor es obligatorio"
                ));

        mockMvc.perform(put("/api/autores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombres": "   ",
                                    "apellidos": "Perez"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje")
                        .value("El nombre del autor es obligatorio"));
    }
}


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

import com.biblioteca.app.model.Editorial;
import com.biblioteca.app.service.EditorialService;

@ExtendWith(MockitoExtension.class)
class EditorialControllerTest {

    @Mock
    private EditorialService editorialService;

    private MockMvc mockMvc;
    private Editorial editorial;

    private static final String EDITORIAL_JSON = """
            {
                "nombre": "Editorial Planeta",
                "pais": "España",
                "estado": true
            }
            """;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new EditorialController(editorialService))
                .build();

        editorial = new Editorial();
        editorial.setIdEditorial(1);
        editorial.setNombre("Editorial Planeta");
        editorial.setPais("España");
        editorial.setEstado(true);
    }

    // 1. Listar editoriales
    @Test
    void debeListarEditoriales() throws Exception {
        when(editorialService.listarEditoriales())
                .thenReturn(List.of(editorial));

        mockMvc.perform(get("/api/editoriales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idEditorial").value(1))
                .andExpect(jsonPath("$[0].nombre")
                        .value("Editorial Planeta"))
                .andExpect(jsonPath("$[0].pais")
                        .value("España"))
                .andExpect(jsonPath("$[0].estado").value(true));

        verify(editorialService).listarEditoriales();
    }

    // 2. Listar editoriales sin registros
    @Test
    void debeRetornarListaVacia() throws Exception {
        when(editorialService.listarEditoriales())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/editoriales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(editorialService).listarEditoriales();
    }

    // 3. Buscar editorial existente
    @Test
    void debeBuscarEditorialPorId() throws Exception {
        when(editorialService.obtenerEditorialPorId(1))
                .thenReturn(Optional.of(editorial));

        mockMvc.perform(get("/api/editoriales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEditorial").value(1))
                .andExpect(jsonPath("$.nombre")
                        .value("Editorial Planeta"))
                .andExpect(jsonPath("$.pais")
                        .value("España"));

        verify(editorialService).obtenerEditorialPorId(1);
    }

    // 4. Buscar editorial inexistente
    @Test
    void debeRetornar404SiEditorialNoExiste()
            throws Exception {

        when(editorialService.obtenerEditorialPorId(999))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/editoriales/999"))
                .andExpect(status().isNotFound());

        verify(editorialService).obtenerEditorialPorId(999);
    }

    // 5. Registrar nueva editorial
    @Test
    void debeRegistrarEditorial() throws Exception {
        when(editorialService.registrarEditorial(
                any(Editorial.class)))
                .thenReturn(editorial);

        mockMvc.perform(post("/api/editoriales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(EDITORIAL_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idEditorial").value(1))
                .andExpect(jsonPath("$.nombre")
                        .value("Editorial Planeta"))
                .andExpect(jsonPath("$.pais")
                        .value("España"))
                .andExpect(jsonPath("$.estado").value(true));

        verify(editorialService)
                .registrarEditorial(any(Editorial.class));
    }

    // 6. Actualizar editorial existente
    @Test
    void debeActualizarEditorial() throws Exception {
        editorial.setNombre("Editorial actualizada");
        editorial.setPais("Peru");

        when(editorialService.actualizarEditorial(
                eq(1), any(Editorial.class)))
                .thenReturn(editorial);

        mockMvc.perform(put("/api/editoriales/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(EDITORIAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEditorial").value(1))
                .andExpect(jsonPath("$.nombre")
                        .value("Editorial actualizada"))
                .andExpect(jsonPath("$.pais")
                        .value("Peru"));

        verify(editorialService).actualizarEditorial(
                eq(1), any(Editorial.class));
    }

    // 7. Actualizar editorial inexistente
    @Test
    void debeRechazarActualizacionDeEditorialInexistente()
            throws Exception {

        when(editorialService.actualizarEditorial(
                eq(999), any(Editorial.class)))
                .thenThrow(new NoSuchElementException(
                        "Editorial con ID 999 no encontrada"
                ));

        mockMvc.perform(put("/api/editoriales/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(EDITORIAL_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje")
                        .value("Editorial con ID 999 no encontrada"));

        verify(editorialService).actualizarEditorial(
                eq(999), any(Editorial.class));
    }

    // 8. Rechazar registro sin nombre
    @Test
    void debeRechazarEditorialSinNombre()
            throws Exception {

        when(editorialService.registrarEditorial(
                any(Editorial.class)))
                .thenThrow(new IllegalArgumentException(
                        "El nombre de la editorial es obligatorio"
                ));

        mockMvc.perform(post("/api/editoriales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "",
                                    "pais": "Peru"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje")
                        .value("El nombre de la editorial es obligatorio"));

        verify(editorialService)
                .registrarEditorial(any(Editorial.class));
    }

    // 9. Rechazar actualizacion con nombre vacio
    @Test
    void debeRechazarActualizacionConNombreVacio()
            throws Exception {

        when(editorialService.actualizarEditorial(
                eq(1), any(Editorial.class)))
                .thenThrow(new IllegalArgumentException(
                        "El nombre de la editorial es obligatorio"
                ));

        mockMvc.perform(put("/api/editoriales/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "   ",
                                    "pais": "Peru"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje")
                        .value("El nombre de la editorial es obligatorio"));

        verify(editorialService).actualizarEditorial(
                eq(1), any(Editorial.class));
    }
}

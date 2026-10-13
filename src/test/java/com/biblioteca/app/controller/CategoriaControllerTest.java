
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

import com.biblioteca.app.model.Categoria;
import com.biblioteca.app.service.CategoriaService;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

    @Mock
    private CategoriaService categoriaService;

    private MockMvc mockMvc;
    private Categoria categoria;

    private static final String CATEGORIA_JSON = """
            {
                "nombre": "Programacion",
                "descripcion": "Libros de desarrollo de software",
                "imagen": "programacion.jpg",
                "estado": true
            }
            """;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new CategoriaController(categoriaService))
                .build();

        categoria = new Categoria();
        categoria.setIdCategoria(1);
        categoria.setNombre("Programacion");
        categoria.setDescripcion(
                "Libros de desarrollo de software");
        categoria.setImagen("programacion.jpg");
        categoria.setEstado(true);
    }

    // 1. Listar categorias
    @Test
    void debeListarCategorias() throws Exception {
        when(categoriaService.listarCategorias())
                .thenReturn(List.of(categoria));

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCategoria").value(1))
                .andExpect(jsonPath("$[0].nombre")
                        .value("Programacion"))
                .andExpect(jsonPath("$[0].descripcion")
                        .value("Libros de desarrollo de software"))
                .andExpect(jsonPath("$[0].imagen")
                        .value("programacion.jpg"))
                .andExpect(jsonPath("$[0].estado").value(true));

        verify(categoriaService).listarCategorias();
    }

    // 2. Listar categorias sin registros
    @Test
    void debeRetornarListaVacia() throws Exception {
        when(categoriaService.listarCategorias())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(categoriaService).listarCategorias();
    }

    // 3. Buscar categoria existente
    @Test
    void debeBuscarCategoriaPorId() throws Exception {
        when(categoriaService.obtenerCategoriaPorId(1))
                .thenReturn(Optional.of(categoria));

        mockMvc.perform(get("/api/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCategoria").value(1))
                .andExpect(jsonPath("$.nombre")
                        .value("Programacion"))
                .andExpect(jsonPath("$.estado").value(true));

        verify(categoriaService).obtenerCategoriaPorId(1);
    }

    // 4. Buscar categoria inexistente
    @Test
    void debeRetornar404SiCategoriaNoExiste()
            throws Exception {

        when(categoriaService.obtenerCategoriaPorId(999))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/categorias/999"))
                .andExpect(status().isNotFound());

        verify(categoriaService).obtenerCategoriaPorId(999);
    }

    // 5. Registrar nueva categoria
    @Test
    void debeRegistrarCategoria() throws Exception {
        when(categoriaService.registrarCategoria(
                any(Categoria.class)))
                .thenReturn(categoria);

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CATEGORIA_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCategoria").value(1))
                .andExpect(jsonPath("$.nombre")
                        .value("Programacion"))
                .andExpect(jsonPath("$.imagen")
                        .value("programacion.jpg"))
                .andExpect(jsonPath("$.estado").value(true));

        verify(categoriaService)
                .registrarCategoria(any(Categoria.class));
    }

    // 6. Actualizar categoria existente
    @Test
    void debeActualizarCategoria() throws Exception {
        categoria.setNombre("Programacion avanzada");
        categoria.setDescripcion("Nueva descripcion");
        categoria.setImagen("nueva-imagen.jpg");

        when(categoriaService.actualizarCategoria(
                eq(1), any(Categoria.class)))
                .thenReturn(categoria);

        mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CATEGORIA_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCategoria").value(1))
                .andExpect(jsonPath("$.nombre")
                        .value("Programacion avanzada"))
                .andExpect(jsonPath("$.descripcion")
                        .value("Nueva descripcion"))
                .andExpect(jsonPath("$.imagen")
                        .value("nueva-imagen.jpg"));

        verify(categoriaService).actualizarCategoria(
                eq(1), any(Categoria.class));
    }

    // 7. Actualizar categoria inexistente
    @Test
    void debeRechazarActualizacionDeCategoriaInexistente()
            throws Exception {

        when(categoriaService.actualizarCategoria(
                eq(999), any(Categoria.class)))
                .thenThrow(new NoSuchElementException(
                        "Categoria con ID 999 no encontrada"
                ));

        mockMvc.perform(put("/api/categorias/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CATEGORIA_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje")
                        .value("Categoria con ID 999 no encontrada"));

        verify(categoriaService).actualizarCategoria(
                eq(999), any(Categoria.class));
    }

    // 8. Rechazar registro sin nombre
    @Test
    void debeRechazarCategoriaSinNombre()
            throws Exception {

        when(categoriaService.registrarCategoria(
                any(Categoria.class)))
                .thenThrow(new IllegalArgumentException(
                        "El nombre de la categoria es obligatorio"
                ));

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "",
                                    "descripcion": "Sin nombre"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje")
                        .value("El nombre de la categoria es obligatorio"));

        verify(categoriaService)
                .registrarCategoria(any(Categoria.class));
    }

    // 9. Rechazar actualizacion con nombre vacio
    @Test
    void debeRechazarActualizacionConNombreVacio()
            throws Exception {

        when(categoriaService.actualizarCategoria(
                eq(1), any(Categoria.class)))
                .thenThrow(new IllegalArgumentException(
                        "El nombre de la categoria es obligatorio"
                ));

        mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "   ",
                                    "descripcion": "Prueba"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje")
                        .value("El nombre de la categoria es obligatorio"));

        verify(categoriaService).actualizarCategoria(
                eq(1), any(Categoria.class));
    }
}

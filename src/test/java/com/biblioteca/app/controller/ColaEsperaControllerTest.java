package com.biblioteca.app.controller;

import com.biblioteca.app.entity.Usuario;
import com.biblioteca.app.model.ColaEspera;
import com.biblioteca.app.model.Libro;
import com.biblioteca.app.service.ColaEsperaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ColaEsperaControllerTest {

    private ColaEsperaService colaEsperaService;
    private MockMvc mockMvc;
    private ColaEspera colaEspera;

    @BeforeEach
    void setUp() {
        colaEsperaService = mock(ColaEsperaService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ColaEsperaController(colaEsperaService))
                .build();

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);

        Libro libro = new Libro();
        libro.setIdLibro(10);

        colaEspera = new ColaEspera();
        colaEspera.setIdCola(20);
        colaEspera.setUsuario(usuario);
        colaEspera.setLibro(libro);
        colaEspera.setFechaRegistro(LocalDateTime.of(2026, 10, 13, 12, 0));
        colaEspera.setEstado("en_espera");
    }

    @Test
    void debeResponderCreatedAlRegistrar() throws Exception {
        when(colaEsperaService.agregarUsuarioACola(1, 10)).thenReturn(colaEspera);

        mockMvc.perform(post("/api/cola-espera/usuario/1/libro/10"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCola").value(20))
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.idLibro").value(10))
                .andExpect(jsonPath("$.estado").value("en_espera"));

        verify(colaEsperaService).agregarUsuarioACola(1, 10);
    }

    @Test
    void debeResponderConflictAnteDuplicado() throws Exception {
        when(colaEsperaService.agregarUsuarioACola(1, 10))
                .thenThrow(new IllegalStateException(
                        "El usuario ya tiene un registro activo para este libro."
                ));

        mockMvc.perform(post("/api/cola-espera/usuario/1/libro/10"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje")
                        .value("El usuario ya tiene un registro activo para este libro."));
    }

    @Test
    void debeResponderNotFoundSiUsuarioNoExiste() throws Exception {
        when(colaEsperaService.agregarUsuarioACola(99, 10))
                .thenThrow(new IllegalArgumentException("Usuario no encontrado."));

        mockMvc.perform(post("/api/cola-espera/usuario/99/libro/10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Usuario no encontrado."));
    }

    @Test
    void debeResponderNotFoundSiLibroNoExiste() throws Exception {
        when(colaEsperaService.agregarUsuarioACola(1, 99))
                .thenThrow(new IllegalArgumentException("Libro no encontrado."));

        mockMvc.perform(post("/api/cola-espera/usuario/1/libro/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Libro no encontrado."));
    }

    @Test
    void debeConsultarColaDeLibro() throws Exception {
        when(colaEsperaService.consultarColaPorLibro(10)).thenReturn(List.of(colaEspera));

        mockMvc.perform(get("/api/cola-espera/libro/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCola").value(20))
                .andExpect(jsonPath("$[0].idLibro").value(10));

        verify(colaEsperaService).consultarColaPorLibro(10);
    }

    @Test
    void debeConsultarRegistrosDeUsuario() throws Exception {
        when(colaEsperaService.consultarRegistrosPorUsuario(1)).thenReturn(List.of(colaEspera));

        mockMvc.perform(get("/api/cola-espera/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCola").value(20))
                .andExpect(jsonPath("$[0].idUsuario").value(1));

        verify(colaEsperaService).consultarRegistrosPorUsuario(1);
    }

    @Test
    void debeMarcarRegistroComoAtendido() throws Exception {
        colaEspera.setEstado("atendido");
        when(colaEsperaService.marcarComoAtendido(20)).thenReturn(colaEspera);

        mockMvc.perform(put("/api/cola-espera/20/atendido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCola").value(20))
                .andExpect(jsonPath("$.estado").value("atendido"));

        verify(colaEsperaService).marcarComoAtendido(20);
    }

    @Test
    void debeResponderNotFoundSiRegistroNoExisteAlMarcarAtendido() throws Exception {
        when(colaEsperaService.marcarComoAtendido(99))
                .thenThrow(new IllegalArgumentException("Registro de cola no encontrado."));

        mockMvc.perform(put("/api/cola-espera/99/atendido"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Registro de cola no encontrado."));
    }
}

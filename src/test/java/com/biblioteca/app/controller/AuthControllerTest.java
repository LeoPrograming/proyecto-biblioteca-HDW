package com.biblioteca.app.controller;

import com.biblioteca.app.dto.LoginDTO;
import com.biblioteca.app.entity.TipoUsuario;
import com.biblioteca.app.entity.Usuario;
import com.biblioteca.app.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private AuthService authService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);

        AuthController authController = new AuthController(authService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();
    }

    @Test
    void debeAutenticarCorrectamenteYCrearSesion() throws Exception {

        TipoUsuario tipoUsuario = new TipoUsuario();
        tipoUsuario.setNombre("Administrador");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setUsuario("admin");
        usuario.setTipoUsuario(tipoUsuario);
        usuario.setEstado(true);

        when(authService.autenticar(any(LoginDTO.class)))
                .thenReturn(Optional.of(usuario));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usuario": "admin",
                                  "contrasena": "clave123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje")
                        .value("Autenticación correcta"))
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.usuario").value("admin"))
                .andExpect(jsonPath("$.tipoUsuario")
                        .value("Administrador"))
                .andExpect(jsonPath("$.contrasena")
                        .doesNotExist());

        verify(authService, times(1))
                .autenticar(any(LoginDTO.class));
    }

    @Test
    void debeRechazarCredencialesIncorrectas() throws Exception {

        when(authService.autenticar(any(LoginDTO.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usuario": "admin",
                                  "contrasena": "incorrecta"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje")
                        .value("Credenciales incorrectas"))
                .andExpect(jsonPath("$.contrasena")
                        .doesNotExist());
    }

    @Test
    void debeUsarMensajeGenericoParaUsuarioInexistente() throws Exception {

        when(authService.autenticar(any(LoginDTO.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usuario": "noexiste",
                                  "contrasena": "clave123"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje")
                        .value("Credenciales incorrectas"));
    }

    @Test
    void debeRechazarCamposVacios() throws Exception {

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usuario": "",
                                  "contrasena": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje")
                        .value("Datos de entrada inválidos"));

        verify(authService, never())
                .autenticar(any(LoginDTO.class));
    }

    @Test
    void noDebeCrearSesionCuandoLasCredencialesSonIncorrectas()
            throws Exception {

        when(authService.autenticar(any(LoginDTO.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usuario": "admin",
                                  "contrasena": "incorrecta"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(request().sessionAttribute(
                        "usuarioId", nullValue()));
    }

    @Test
    void debeRenovarSesionPreviaAlAutenticar()
            throws Exception {

        TipoUsuario tipoUsuario = new TipoUsuario();
        tipoUsuario.setNombre("Administrador");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setUsuario("admin");
        usuario.setTipoUsuario(tipoUsuario);
        usuario.setEstado(true);

        when(authService.autenticar(any(LoginDTO.class)))
                .thenReturn(Optional.of(usuario));

        MockHttpSession sesionAnterior = new MockHttpSession();
        String idAnterior = sesionAnterior.getId();

        var resultado = mockMvc.perform(post("/api/auth/login")
                        .session(sesionAnterior)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usuario": "admin",
                                  "contrasena": "clave123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(request()
                        .sessionAttribute("usuarioId", 1))
                .andExpect(request()
                        .sessionAttribute("usuario", "admin"))
                .andExpect(request()
                        .sessionAttribute(
                                "tipoUsuario",
                                "Administrador"))
                .andReturn();

        var nuevaSesion =
                resultado.getRequest().getSession(false);

        org.junit.jupiter.api.Assertions.assertNotNull(
                nuevaSesion);

        org.junit.jupiter.api.Assertions.assertNotEquals(
                idAnterior,
                nuevaSesion.getId());
    }
}
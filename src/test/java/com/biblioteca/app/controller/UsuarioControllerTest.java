
package com.biblioteca.app.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.biblioteca.app.dto.UsuarioDTO;
import com.biblioteca.app.entity.TipoUsuario;
import com.biblioteca.app.entity.Usuario;
import com.biblioteca.app.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    private MockMvc mockMvc;
    private Usuario usuario;
    private UsuarioDTO usuarioDTO;
    private MockHttpSession sesionAdmin;

    private static final String USUARIO_JSON = """
            {
                "idTipoUsuario": 1,
                "dni": "12345678",
                "usuario": "admin01",
                "contrasena": "Clave123",
                "nombres": "Carlos",
                "apellidos": "Perez",
                "correoE": "carlos@example.com",
                "telefono": "999999999",
                "direccion": "Ica"
            }
            """;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new UsuarioController(usuarioService))
                .build();

        TipoUsuario tipo = new TipoUsuario();
        tipo.setIdTipoUsuario(1);
        tipo.setNombre("Administrador");

        usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setTipoUsuario(tipo);
        usuario.setDni("12345678");
        usuario.setUsuario("admin01");
        usuario.setContrasena("HASH_PRIVADO");
        usuario.setNombres("Carlos");
        usuario.setApellidos("Perez");
        usuario.setCorreoE("carlos@example.com");
        usuario.setEstado(true);

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setIdUsuario(1);
        usuarioDTO.setIdTipoUsuario(1);
        usuarioDTO.setDni("12345678");
        usuarioDTO.setUsuario("admin01");
        usuarioDTO.setNombres("Carlos");
        usuarioDTO.setApellidos("Perez");
        usuarioDTO.setCorreoE("carlos@example.com");
        usuarioDTO.setEstado(true);

        // Valor intencional para verificar que no se exponga
        usuarioDTO.setContrasena("CLAVE_PRIVADA");

        sesionAdmin = new MockHttpSession();
        sesionAdmin.setAttribute("usuarioId", 1);
        sesionAdmin.setAttribute(
                "tipoUsuario", "Administrador");
    }

    // 1. Listar usuarios
    @Test
    void debeListarUsuarios() throws Exception {

        when(usuarioService.listarUsuarios())
                .thenReturn(List.of(usuario));

        when(usuarioService.convertirADTO(usuario))
                .thenReturn(usuarioDTO);

        mockMvc.perform(get("/api/usuarios")
                        .session(sesionAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario").value(1))
                .andExpect(jsonPath("$[0].dni")
                        .value("12345678"))
                .andExpect(jsonPath("$[0].usuario")
                        .value("admin01"))
                .andExpect(jsonPath("$[0].contrasena")
                        .doesNotExist());

        verify(usuarioService).listarUsuarios();
    }

    // 2. Listar usuarios cuando no hay registros
    @Test
    void debeRetornarListaVacia() throws Exception {

        when(usuarioService.listarUsuarios())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/usuarios")
                        .session(sesionAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // 3. Buscar usuario por ID
    @Test
    void debeBuscarUsuarioPorId() throws Exception {

        when(usuarioService.buscarPorId(1))
                .thenReturn(Optional.of(usuario));

        when(usuarioService.convertirADTO(usuario))
                .thenReturn(usuarioDTO);

        mockMvc.perform(get("/api/usuarios/1")
                        .session(sesionAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.nombres")
                        .value("Carlos"))
                .andExpect(jsonPath("$.contrasena")
                        .doesNotExist());
    }

    // 4. Buscar por DNI
    @Test
    void debeBuscarUsuarioPorDni() throws Exception {

        when(usuarioService.buscarUsuarios("12345678"))
                .thenReturn(List.of(usuario));

        when(usuarioService.convertirADTO(usuario))
                .thenReturn(usuarioDTO);

        mockMvc.perform(get("/api/usuarios")
                        .session(sesionAdmin)
                        .param("buscar", "12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dni")
                        .value("12345678"));

        verify(usuarioService)
                .buscarUsuarios("12345678");
    }

    // 5. Buscar por nombre de usuario
    @Test
    void debeBuscarPorNombreUsuario() throws Exception {

        when(usuarioService.buscarUsuarios("admin01"))
                .thenReturn(List.of(usuario));

        when(usuarioService.convertirADTO(usuario))
                .thenReturn(usuarioDTO);

        mockMvc.perform(get("/api/usuarios")
                        .session(sesionAdmin)
                        .param("buscar", "admin01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuario")
                        .value("admin01"));
    }

    // 6. Registrar usuario
    @Test
    void debeRegistrarUsuario() throws Exception {

        when(usuarioService.registrar(any(UsuarioDTO.class)))
                .thenReturn(usuario);

        when(usuarioService.convertirADTO(usuario))
                .thenReturn(usuarioDTO);

        mockMvc.perform(post("/api/usuarios")
                        .session(sesionAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USUARIO_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.usuario")
                        .value("admin01"))
                .andExpect(jsonPath("$.contrasena")
                        .doesNotExist());

        verify(usuarioService)
                .registrar(argThat(datos ->
                        "Clave123".equals(
                                datos.getContrasena())));
    }

    // 7. Rechazar registro duplicado
    @Test
    void debeRechazarUsuarioDuplicado() throws Exception {

        when(usuarioService.registrar(any(UsuarioDTO.class)))
                .thenThrow(new IllegalArgumentException(
                        "DNI registrado"));

        mockMvc.perform(post("/api/usuarios")
                        .session(sesionAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USUARIO_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje")
                        .value("DNI registrado"));
    }

    // 8. Actualizar usuario
    @Test
    void debeActualizarUsuario() throws Exception {

        usuarioDTO.setNombres("Carlos actualizado");

        when(usuarioService.actualizar(
                eq(1), any(UsuarioDTO.class)))
                .thenReturn(usuario);

        when(usuarioService.convertirADTO(usuario))
                .thenReturn(usuarioDTO);

        mockMvc.perform(put("/api/usuarios/1")
                        .session(sesionAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USUARIO_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombres")
                        .value("Carlos actualizado"))
                .andExpect(jsonPath("$.contrasena")
                        .doesNotExist());
    }

    // 9. Cambiar estado del usuario
    @Test
    void debeCambiarEstadoUsuario() throws Exception {

        usuario.setEstado(false);
        usuarioDTO.setEstado(false);

        when(usuarioService.buscarPorId(1))
                .thenReturn(Optional.of(usuario));

        when(usuarioService.convertirADTO(usuario))
                .thenReturn(usuarioDTO);

        mockMvc.perform(patch("/api/usuarios/1/estado")
                        .session(sesionAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado")
                        .value(false));

        verify(usuarioService).cambiarEstado(1);
        verify(usuarioService).buscarPorId(1);
    }

    // 10. Buscar usuario inexistente
    @Test
    void debeRetornar404SiUsuarioNoExiste()
            throws Exception {

        when(usuarioService.buscarPorId(999))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/999")
                        .session(sesionAdmin))
                .andExpect(status().isNotFound());
    }

    // 11. Actualizar usuario inexistente
    @Test
    void debeRechazarActualizacionInexistente()
            throws Exception {

        when(usuarioService.actualizar(
                eq(999), any(UsuarioDTO.class)))
                .thenThrow(new IllegalArgumentException(
                        "Usuario no encontrado"));

        mockMvc.perform(put("/api/usuarios/999")
                        .session(sesionAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USUARIO_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje")
                        .value("Usuario no encontrado"));
    }

    // 12. Proteger datos sensibles
    @Test
    void noDebeExponerContrasenaNiHash()
            throws Exception {

        when(usuarioService.buscarPorId(1))
                .thenReturn(Optional.of(usuario));

        when(usuarioService.convertirADTO(usuario))
                .thenReturn(usuarioDTO);

        mockMvc.perform(get("/api/usuarios/1")
                        .session(sesionAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contrasena")
                        .doesNotExist())
                .andExpect(jsonPath("$.password")
                        .doesNotExist())
                .andExpect(resultado -> {

                    String respuesta = resultado
                            .getResponse()
                            .getContentAsString();

                    assertFalse(respuesta.contains(
                            "CLAVE_PRIVADA"));

                    assertFalse(respuesta.contains(
                            "HASH_PRIVADO"));
                });
    }

    // 13. Rechazar acceso sin iniciar sesion
    @Test
    void debeRechazarUsuarioSinSesion() throws Exception {

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(usuarioService);
    }

    // 14. Rechazar usuario sin permisos
    @Test
    void debeRechazarUsuarioNoAdministrador()
            throws Exception {

        MockHttpSession sesionLector =
                new MockHttpSession();

        sesionLector.setAttribute("usuarioId", 2);
        sesionLector.setAttribute(
                "tipoUsuario", "Lector");

        mockMvc.perform(get("/api/usuarios")
                        .session(sesionLector))
                .andExpect(status().isForbidden());

        verifyNoInteractions(usuarioService);
    }

    // 15. Rechazar registro sin contraseña
    @Test
    void debeRechazarRegistroSinContrasena()
            throws Exception {

        mockMvc.perform(post("/api/usuarios")
                        .session(sesionAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "idTipoUsuario": 1,
                                    "dni": "12345678",
                                    "usuario": "admin01",
                                    "nombres": "Carlos",
                                    "apellidos": "Perez"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje")
                        .value(
                                "La contrasena es obligatoria"));

        verify(usuarioService, never())
                .registrar(any(UsuarioDTO.class));
    }
}

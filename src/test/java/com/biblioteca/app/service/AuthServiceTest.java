package com.biblioteca.app.service;

import com.biblioteca.app.dto.LoginDTO;
import com.biblioteca.app.entity.TipoUsuario;
import com.biblioteca.app.entity.Usuario;
import com.biblioteca.app.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final AuthService authService = new AuthService(usuarioRepository);

    @Test
    void debeAutenticarUsuarioConCredencialesCorrectas() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsuario("admin");
        loginDTO.setContrasena("clave123");

        TipoUsuario tipoUsuario = new TipoUsuario();
        tipoUsuario.setNombre("Administrador");

        Usuario usuario = new Usuario();
        usuario.setUsuario("admin");
        usuario.setContrasena(BCrypt.hashpw("clave123", BCrypt.gensalt()));
        usuario.setEstado(true);
        usuario.setTipoUsuario(tipoUsuario);

        when(usuarioRepository.findByUsuario("admin"))
                .thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = authService.autenticar(loginDTO);

        assertTrue(resultado.isPresent());
        assertEquals("admin", resultado.get().getUsuario());
        assertEquals("Administrador", resultado.get().getTipoUsuario().getNombre());
    }

    @Test
    void debeRechazarContrasenaIncorrecta() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsuario("admin");
        loginDTO.setContrasena("incorrecta");

        Usuario usuario = new Usuario();
        usuario.setUsuario("admin");
        usuario.setContrasena(BCrypt.hashpw("clave123", BCrypt.gensalt()));
        usuario.setEstado(true);

        when(usuarioRepository.findByUsuario("admin"))
                .thenReturn(Optional.of(usuario));

        assertTrue(authService.autenticar(loginDTO).isEmpty());
    }

    @Test
    void debeRechazarUsuarioInexistente() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsuario("noexiste");
        loginDTO.setContrasena("clave123");

        when(usuarioRepository.findByUsuario("noexiste"))
                .thenReturn(Optional.empty());

        assertTrue(authService.autenticar(loginDTO).isEmpty());
    }

    @Test
    void debeRechazarUsuarioInactivo() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsuario("admin");
        loginDTO.setContrasena("clave123");

        Usuario usuario = new Usuario();
        usuario.setUsuario("admin");
        usuario.setContrasena(BCrypt.hashpw("clave123", BCrypt.gensalt()));
        usuario.setEstado(false);

        when(usuarioRepository.findByUsuario("admin"))
                .thenReturn(Optional.of(usuario));

        assertTrue(authService.autenticar(loginDTO).isEmpty());
    }

    @Test
    void debeRechazarHashInvalido() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsuario("admin");
        loginDTO.setContrasena("clave123");

        Usuario usuario = new Usuario();
        usuario.setUsuario("admin");
        usuario.setContrasena("hash-invalido");
        usuario.setEstado(true);

        when(usuarioRepository.findByUsuario("admin"))
                .thenReturn(Optional.of(usuario));

        assertTrue(authService.autenticar(loginDTO).isEmpty());
    }
}
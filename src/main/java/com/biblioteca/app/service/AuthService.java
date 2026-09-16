package com.biblioteca.app.service;

import com.biblioteca.app.dto.LoginDTO;
import com.biblioteca.app.entity.Usuario;
import com.biblioteca.app.repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> autenticar(LoginDTO loginDTO) {

        Optional<Usuario> usuarioEncontrado =
                usuarioRepository.findByUsuario(loginDTO.getUsuario());

        if (usuarioEncontrado.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = usuarioEncontrado.get();

        if (!Boolean.TRUE.equals(usuario.getEstado())) {
            return Optional.empty();
        }

        try {
            if (!BCrypt.checkpw(
                    loginDTO.getContrasena(),
                    usuario.getContrasena())) {

                return Optional.empty();
            }
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }

        return Optional.of(usuario);
    }
}
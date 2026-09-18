package com.biblioteca.app.service;

import com.biblioteca.app.dto.UsuarioDTO;
import com.biblioteca.app.entity.TipoUsuario;
import com.biblioteca.app.entity.Usuario;
import com.biblioteca.app.repository.TipoUsuarioRepository;
import com.biblioteca.app.repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, TipoUsuarioRepository tipoUsuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAllByOrderByIdUsuarioDesc();
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarUsuarios(String termino) {
        return usuarioRepository.findByDniContainingIgnoreCaseOrUsuarioContainingIgnoreCaseOrderByIdUsuarioDesc(termino, termino);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    @Transactional
    public Usuario registrar(UsuarioDTO dto) {
        if (usuarioRepository.existsByDni(dto.getDni())) throw new IllegalArgumentException("DNI registrado");
        if (usuarioRepository.existsByCorreoE(dto.getCorreoE())) throw new IllegalArgumentException("Correo registrado");
        if (usuarioRepository.existsByUsuario(dto.getUsuario())) throw new IllegalArgumentException("Usuario registrado");

        TipoUsuario tipo = tipoUsuarioRepository.findById(dto.getIdTipoUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de usuario no válido"));

        Usuario usuario = new Usuario();
        usuario.setTipoUsuario(tipo);
        usuario.setDni(dto.getDni());
        usuario.setUsuario(dto.getUsuario());
        usuario.setContrasena(BCrypt.hashpw(dto.getContrasena(), BCrypt.gensalt()));
        usuario.setNombres(dto.getNombres());
        usuario.setApellidos(dto.getApellidos());
        usuario.setCorreoE(dto.getCorreoE());
        usuario.setTelefono(dto.getTelefono());
        usuario.setDireccion(dto.getDireccion());
        usuario.setEstado(true); 

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizar(Integer id, UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Optional<Usuario> uDni = usuarioRepository.findByDni(dto.getDni());
        if (uDni.isPresent() && !uDni.get().getIdUsuario().equals(id)) throw new IllegalArgumentException("DNI duplicado");

        Optional<Usuario> uCorreo = usuarioRepository.findByCorreoE(dto.getCorreoE());
        if (uCorreo.isPresent() && !uCorreo.get().getIdUsuario().equals(id)) throw new IllegalArgumentException("Correo duplicado");

        TipoUsuario tipo = tipoUsuarioRepository.findById(dto.getIdTipoUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Tipo no válido"));

        usuario.setTipoUsuario(tipo);
        usuario.setDni(dto.getDni());
        usuario.setNombres(dto.getNombres());
        usuario.setApellidos(dto.getApellidos());
        usuario.setCorreoE(dto.getCorreoE());
        usuario.setTelefono(dto.getTelefono());
        usuario.setDireccion(dto.getDireccion());

        if (dto.getContrasena() != null && !dto.getContrasena().trim().isEmpty()) {
            usuario.setContrasena(BCrypt.hashpw(dto.getContrasena(), BCrypt.gensalt()));
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void cambiarEstado(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        usuario.setEstado(!usuario.getEstado());
        usuarioRepository.save(usuario);
    }

    public UsuarioDTO convertirADTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setIdTipoUsuario(usuario.getTipoUsuario().getIdTipoUsuario());
        dto.setDni(usuario.getDni());
        dto.setUsuario(usuario.getUsuario());
        dto.setNombres(usuario.getNombres());
        dto.setApellidos(usuario.getApellidos());
        dto.setCorreoE(usuario.getCorreoE());
        dto.setTelefono(usuario.getTelefono());
        dto.setDireccion(usuario.getDireccion());
        dto.setEstado(usuario.getEstado());
        return dto;
    }
}


package com.biblioteca.app.controller;

import com.biblioteca.app.dto.UsuarioDTO;
import com.biblioteca.app.entity.Usuario;
import com.biblioteca.app.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Listar usuarios o buscar por DNI/usuario
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios(
            @RequestParam(required = false) String buscar,
            HttpServletRequest request) {

        verificarAdministrador(request);

        List<Usuario> usuarios;

        if (buscar == null || buscar.isBlank()) {
            usuarios = usuarioService.listarUsuarios();
        } else {
            usuarios = usuarioService.buscarUsuarios(buscar.trim());
        }

        List<UsuarioDTO> respuesta = usuarios.stream()
                .map(usuarioService::convertirADTO)
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    // Buscar usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(
            @PathVariable Integer id,
            HttpServletRequest request) {

        verificarAdministrador(request);

        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Usuario no encontrado"
                ));

        return ResponseEntity.ok(
                usuarioService.convertirADTO(usuario)
        );
    }

    // Registrar un nuevo usuario
    @PostMapping
    public ResponseEntity<UsuarioDTO> registrar(
            @RequestBody UsuarioDTO datos,
            HttpServletRequest request) {

        verificarAdministrador(request);
        validarRegistro(datos);

        Usuario usuario = usuarioService.registrar(datos);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.convertirADTO(usuario));
    }

    // Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody UsuarioDTO datos,
            HttpServletRequest request) {

        verificarAdministrador(request);

        if (datos == null) {
            throw new IllegalArgumentException(
                    "Los datos del usuario son obligatorios"
            );
        }

        Usuario usuario = usuarioService.actualizar(id, datos);

        return ResponseEntity.ok(
                usuarioService.convertirADTO(usuario)
        );
    }

    // Activar o desactivar usuario
    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioDTO> cambiarEstado(
            @PathVariable Integer id,
            HttpServletRequest request) {

        verificarAdministrador(request);

        usuarioService.cambiarEstado(id);

        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Usuario no encontrado"
                ));

        return ResponseEntity.ok(
                usuarioService.convertirADTO(usuario)
        );
    }

    // Verificar sesion y rol de administrador
    private void verificarAdministrador(
            HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("usuarioId") == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debe iniciar sesion"
            );
        }

        Object rol = session.getAttribute("tipoUsuario");

        if (rol == null ||
                !"Administrador".equalsIgnoreCase(rol.toString())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tiene permisos para administrar usuarios"
            );
        }
    }

    // Validaciones principales del registro
    private void validarRegistro(UsuarioDTO datos) {

        if (datos == null) {
            throw new IllegalArgumentException(
                    "Los datos del usuario son obligatorios"
            );
        }

        if (datos.getIdTipoUsuario() == null) {
            throw new IllegalArgumentException(
                    "El tipo de usuario es obligatorio"
            );
        }

        if (datos.getDni() == null ||
                datos.getDni().isBlank()) {
            throw new IllegalArgumentException(
                    "El DNI es obligatorio"
            );
        }

        if (datos.getUsuario() == null ||
                datos.getUsuario().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre de usuario es obligatorio"
            );
        }

        if (datos.getNombres() == null ||
                datos.getNombres().isBlank() ||
                datos.getApellidos() == null ||
                datos.getApellidos().isBlank()) {
            throw new IllegalArgumentException(
                    "Los nombres y apellidos son obligatorios"
            );
        }

        if (datos.getContrasena() == null ||
                datos.getContrasena().isBlank()) {
            throw new IllegalArgumentException(
                    "La contrasena es obligatoria"
            );
        }
    }

    // Usuario inexistente
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> noEncontrado(
            NoSuchElementException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", ex.getMessage()));
    }

    // Datos invalidos, duplicados o usuario inexistente
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> errorValidacion(
            IllegalArgumentException ex) {

        String mensaje = ex.getMessage();

        if ("Usuario no encontrado".equals(mensaje)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", mensaje));
        }

        Set<String> duplicados = Set.of(
                "DNI registrado",
                "Correo registrado",
                "Usuario registrado",
                "DNI duplicado",
                "Correo duplicado"
        );

        if (duplicados.contains(mensaje)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensaje", mensaje));
        }

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "mensaje",
                        mensaje == null ? "Datos invalidos" : mensaje
                ));
    }
}

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

    private static final Integer TIPO_LECTOR = 3;

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    // =====================================================
    // LISTAR / BUSCAR
    // =====================================================

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios(
            @RequestParam(required = false) String buscar,
            HttpServletRequest request) {

        String rol = obtenerRolPermitido(request);

        List<Usuario> usuarios;

        if (buscar == null || buscar.isBlank()) {
            usuarios = usuarioService.listarUsuarios();
        } else {
            usuarios = usuarioService.buscarUsuarios(buscar.trim());
        }

        // Bibliotecario solamente visualiza lectores
        if (esBibliotecario(rol)) {
            usuarios = usuarios.stream()
                    .filter(this::esLector)
                    .toList();
        }

        List<UsuarioDTO> respuesta = usuarios.stream()
                .map(usuarioService::convertirADTO)
                .toList();

        return ResponseEntity.ok(respuesta);
    }


    // =====================================================
    // BUSCAR POR ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(
            @PathVariable Integer id,
            HttpServletRequest request) {

        String rol = obtenerRolPermitido(request);

        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Usuario no encontrado"
                        )
                );

        if (esBibliotecario(rol) && !esLector(usuario)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El bibliotecario solo puede consultar lectores"
            );
        }

        return ResponseEntity.ok(
                usuarioService.convertirADTO(usuario)
        );
    }


    // =====================================================
    // REGISTRAR
    // =====================================================

    @PostMapping
    public ResponseEntity<UsuarioDTO> registrar(
            @RequestBody UsuarioDTO datos,
            HttpServletRequest request) {

        String rol = obtenerRolPermitido(request);

        if (datos == null) {
            throw new IllegalArgumentException(
                    "Los datos del usuario son obligatorios"
            );
        }

        // Bibliotecario solo puede crear lectores
        if (esBibliotecario(rol)) {
            datos.setIdTipoUsuario(TIPO_LECTOR);
        }

        validarRegistro(datos);

        Usuario usuario =
                usuarioService.registrar(datos);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        usuarioService.convertirADTO(usuario)
                );
    }


    // =====================================================
    // ACTUALIZAR
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody UsuarioDTO datos,
            HttpServletRequest request) {

        String rol = obtenerRolPermitido(request);

        if (datos == null) {
            throw new IllegalArgumentException(
                    "Los datos del usuario son obligatorios"
            );
        }

        /*
         * IMPORTANTE:
         * Para Administrador mantenemos el flujo original.
         * No hacemos una búsqueda previa porque los tests
         * existentes esperan que UsuarioService.actualizar()
         * gestione esa validación.
         */
        if (esBibliotecario(rol)) {

            Usuario usuarioExistente =
                    usuarioService.buscarPorId(id)
                            .orElseThrow(() ->
                                    new NoSuchElementException(
                                            "Usuario no encontrado"
                                    )
                            );

            if (!esLector(usuarioExistente)) {

                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "El bibliotecario solo puede editar lectores"
                );
            }

            // Nunca puede transformar al lector en otro rol
            datos.setIdTipoUsuario(TIPO_LECTOR);
        }

        Usuario usuario =
                usuarioService.actualizar(id, datos);

        return ResponseEntity.ok(
                usuarioService.convertirADTO(usuario)
        );
    }


    // =====================================================
    // CAMBIAR ESTADO
    // =====================================================

    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioDTO> cambiarEstado(
            @PathVariable Integer id,
            HttpServletRequest request) {

        String rol = obtenerRolPermitido(request);

        /*
         * Para Bibliotecario sí verificamos primero
         * que el usuario sea Lector.
         */
        if (esBibliotecario(rol)) {

            Usuario usuarioExistente =
                    usuarioService.buscarPorId(id)
                            .orElseThrow(() ->
                                    new NoSuchElementException(
                                            "Usuario no encontrado"
                                    )
                            );

            if (!esLector(usuarioExistente)) {

                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "El bibliotecario solo puede cambiar el estado de lectores"
                );
            }
        }

        /*
         * Conservamos el comportamiento original:
         * cambiarEstado() y luego una única búsqueda
         * para devolver el usuario actualizado.
         */
        usuarioService.cambiarEstado(id);

        Usuario usuario =
                usuarioService.buscarPorId(id)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Usuario no encontrado"
                                )
                        );

        return ResponseEntity.ok(
                usuarioService.convertirADTO(usuario)
        );
    }


    // =====================================================
    // VALIDAR SESIÓN Y ROL
    // =====================================================

    private String obtenerRolPermitido(
            HttpServletRequest request) {

        HttpSession session =
                request.getSession(false);

        if (session == null
                || session.getAttribute("usuarioId") == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debe iniciar sesion"
            );
        }

        Object rol =
                session.getAttribute("tipoUsuario");

        if (rol == null) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No se pudo determinar el tipo de usuario"
            );
        }

        String nombreRol =
                rol.toString();

        if (!"Administrador".equalsIgnoreCase(nombreRol)
                && !"Bibliotecario".equalsIgnoreCase(nombreRol)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tiene permisos para acceder a esta operacion"
            );
        }

        return nombreRol;
    }


    // =====================================================
    // VALIDAR BIBLIOTECARIO
    // =====================================================

    private boolean esBibliotecario(String rol) {

        return "Bibliotecario".equalsIgnoreCase(rol);
    }


    // =====================================================
    // VALIDAR LECTOR
    // =====================================================

    private boolean esLector(Usuario usuario) {

        return usuario != null
                && usuario.getTipoUsuario() != null
                && usuario.getTipoUsuario().getIdTipoUsuario() != null
                && TIPO_LECTOR.equals(
                usuario.getTipoUsuario()
                        .getIdTipoUsuario()
        );
    }


    // =====================================================
    // VALIDACIONES DE REGISTRO
    // =====================================================

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

        if (datos.getDni() == null
                || datos.getDni().isBlank()) {

            throw new IllegalArgumentException(
                    "El DNI es obligatorio"
            );
        }

        if (datos.getUsuario() == null
                || datos.getUsuario().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre de usuario es obligatorio"
            );
        }

        if (datos.getNombres() == null
                || datos.getNombres().isBlank()
                || datos.getApellidos() == null
                || datos.getApellidos().isBlank()) {

            throw new IllegalArgumentException(
                    "Los nombres y apellidos son obligatorios"
            );
        }

        if (datos.getContrasena() == null
                || datos.getContrasena().isBlank()) {

            throw new IllegalArgumentException(
                    "La contrasena es obligatoria"
            );
        }
    }


    // =====================================================
    // USUARIO NO ENCONTRADO
    // =====================================================

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> noEncontrado(
            NoSuchElementException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        Map.of(
                                "mensaje",
                                ex.getMessage()
                        )
                );
    }


    // =====================================================
    // VALIDACIONES
    // =====================================================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> errorValidacion(
            IllegalArgumentException ex) {

        String mensaje =
                ex.getMessage();

        if ("Usuario no encontrado".equals(mensaje)) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "mensaje",
                                    mensaje
                            )
                    );
        }

        Set<String> duplicados =
                Set.of(
                        "DNI registrado",
                        "Correo registrado",
                        "Usuario registrado",
                        "DNI duplicado",
                        "Correo duplicado"
                );

        if (duplicados.contains(mensaje)) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            Map.of(
                                    "mensaje",
                                    mensaje
                            )
                    );
        }

        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "mensaje",
                                mensaje == null
                                        ? "Datos invalidos"
                                        : mensaje
                        )
                );
    }
}
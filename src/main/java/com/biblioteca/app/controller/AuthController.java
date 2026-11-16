package com.biblioteca.app.controller;

import com.biblioteca.app.dto.LoginDTO;
import com.biblioteca.app.entity.Usuario;
import com.biblioteca.app.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginDTO loginDTO,
            BindingResult bindingResult,
            HttpServletRequest request) {

        Map<String, Object> respuesta = new HashMap<>();

        if (bindingResult.hasErrors()) {
            respuesta.put("mensaje", "Datos de entrada inválidos");
            return ResponseEntity.badRequest().body(respuesta);
        }

        Optional<Usuario> usuarioAutenticado = authService.autenticar(loginDTO);

        if (usuarioAutenticado.isEmpty()) {
            respuesta.put("mensaje", "Credenciales incorrectas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
        }

        Usuario usuario = usuarioAutenticado.get();

        HttpSession sesionAnterior = request.getSession(false);

        if (sesionAnterior != null) {
            sesionAnterior.invalidate();
        }

        HttpSession nuevaSesion = request.getSession(true);

        nuevaSesion.setAttribute("usuarioId", usuario.getIdUsuario());
        nuevaSesion.setAttribute("usuario", usuario.getUsuario());
        nuevaSesion.setAttribute(
                "tipoUsuario",
                usuario.getTipoUsuario().getNombre()
        );

        respuesta.put("mensaje", "Autenticación correcta");
        respuesta.put("idUsuario", usuario.getIdUsuario());
        respuesta.put("usuario", usuario.getUsuario());
        respuesta.put("tipoUsuario", usuario.getTipoUsuario().getNombre());

        return ResponseEntity.ok(respuesta);
    }

    // =========================
    // CERRAR SESIÓN
    // =========================
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            HttpServletRequest request) {

        HttpSession sesion = request.getSession(false);

        if (sesion != null) {
            sesion.invalidate();
        }

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Sesión cerrada correctamente");

        return ResponseEntity.ok(respuesta);
    }
}
package com.biblioteca.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String inicio() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    // =========================
    // LECTORES
    // =========================

    @GetMapping("/lectores")
    public String lectores() {
        return "lectores/lista";
    }

    @GetMapping("/lectores/nuevo")
    public String nuevoLector() {
        return "lectores/formulario";
    }

    @GetMapping("/lectores/editar")
    public String editarLector() {
        return "lectores/editar";
    }

    // =========================
    // USUARIOS
    // =========================

    @GetMapping("/usuarios")
    public String usuarios() {
        return "usuarios/lista";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevoUsuario() {
        return "usuarios/formulario";
    }

    @GetMapping("/usuarios/editar")
    public String editarUsuario() {
        return "usuarios/editar";
    }
}
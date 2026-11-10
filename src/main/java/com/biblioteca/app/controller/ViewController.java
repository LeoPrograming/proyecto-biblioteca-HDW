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


    // =========================
    // AUTORES
    // =========================

    @GetMapping("/autores")
    public String autores() {
        return "autores/lista";
    }

    @GetMapping("/autores/nuevo")
    public String nuevoAutor() {
        return "autores/formulario";
    }

    @GetMapping("/autores/editar")
    public String editarAutor() {
        return "autores/editar";
    }


    // =========================
    // EDITORIALES
    // =========================

    @GetMapping("/editoriales")
    public String editoriales() {
        return "editoriales/lista";
    }

    @GetMapping("/editoriales/nuevo")
    public String nuevaEditorial() {
        return "editoriales/formulario";
    }

    @GetMapping("/editoriales/editar")
    public String editarEditorial() {
        return "editoriales/editar";
    }

    // =========================
// CATEGORÍAS
// =========================

    @GetMapping("/categorias")
    public String categorias() {
        return "categorias/lista";
    }

    @GetMapping("/categorias/nuevo")
    public String nuevaCategoria() {
        return "categorias/formulario";
    }

    @GetMapping("/categorias/editar")
    public String editarCategoria() {
        return "categorias/editar";
    }

    // =========================
// LIBROS
// =========================

    @GetMapping("/libros")
    public String libros() {
        return "libros/lista";
    }

    @GetMapping("/libros/nuevo")
    public String nuevoLibro() {
        return "libros/formulario";
    }

    @GetMapping("/libros/editar")
    public String editarLibro() {
        return "libros/editar";
    }

    // =========================
    // PRÉSTAMOS
    // =========================

    @GetMapping("/prestamos")
    public String prestamos() {
        return "prestamos/lista";
    }

    @GetMapping("/prestamos/nuevo")
    public String nuevoPrestamo() {
        return "prestamos/formulario";
    }

    @GetMapping("/prestamos/detalle")
    public String detallePrestamo() {
        return "prestamos/detalle";
    }

}

package com.biblioteca.app.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginDTO {

    @NotBlank(message = "Ingrese su usuario")
    private String usuario;

    @NotBlank(message = "Ingrese su contraseña")
    private String contrasena;

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
package com.biblioteca.app.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Integer idUsuario;
    private Integer idTipoUsuario;
    private String dni;
    private String usuario;
    private String contrasena;
    private String nombres;
    private String apellidos;
    private String correoE;
    private String telefono;
    private String direccion;
    private Boolean estado;
}
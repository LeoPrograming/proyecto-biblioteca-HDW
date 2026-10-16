
package com.biblioteca.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UsuarioDTO {

    private Integer idUsuario;
    private Integer idTipoUsuario;
    private String dni;
    private String usuario;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String contrasena;

    private String nombres;
    private String apellidos;
    private String correoE;
    private String telefono;
    private String direccion;
    private Boolean estado;
}

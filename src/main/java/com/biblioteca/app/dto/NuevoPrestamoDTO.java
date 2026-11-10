package com.biblioteca.app.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NuevoPrestamoDTO {

    private Integer idUsuario;
    private Integer idComprobante;
    private String serie;
    private String numero;
    private String observaciones;
    private List<Integer> idsLibros;
    private List<Integer> dias;
    private List<Double> precios;
}

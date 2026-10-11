package com.biblioteca.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_pago")
public class TipoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_pago")
    private Integer idTipoPago;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    public TipoPago() {
    }

    public TipoPago(Integer idTipoPago, String nombre) {
        this.idTipoPago = idTipoPago;
        this.nombre = nombre;
    }

    public Integer getIdTipoPago() {
        return idTipoPago;
    }

    public void setIdTipoPago(Integer idTipoPago) {
        this.idTipoPago = idTipoPago;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
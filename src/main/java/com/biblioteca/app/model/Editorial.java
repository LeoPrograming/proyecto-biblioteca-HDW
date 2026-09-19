package com.biblioteca.app.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "editorial")
public class Editorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_editorial")
    private Integer idEditorial;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "pais", length = 50)
    private String pais;

    @Column(name = "estado")
    private Boolean estado;

    @OneToMany(mappedBy = "editorial")
    private List<Libro> libros;

    public Editorial() {}

    public Integer getIdEditorial() { return idEditorial; }
    public void setIdEditorial(Integer idEditorial) { this.idEditorial = idEditorial; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public List<Libro> getLibros() { return libros; }
    public void setLibros(List<Libro> libros) { this.libros = libros; }
}
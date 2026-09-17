package com.biblioteca.app.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "autor")
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_autor")
    private Integer idAutor;

    @Column(name = "nombres", length = 120)
    private String nombres;

    @Column(name = "apellidos", length = 120)
    private String apellidos;

    @Column(name = "estado")
    private Boolean estado;

    @ManyToMany(mappedBy = "autores")
    private List<Libro> libros;

    public Autor() {}

    public Integer getIdAutor() { return idAutor; }
    public void setIdAutor(Integer idAutor) { this.idAutor = idAutor; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public List<Libro> getLibros() { return libros; }
    public void setLibros(List<Libro> libros) { this.libros = libros; }
}
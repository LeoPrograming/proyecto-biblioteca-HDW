
package com.biblioteca.app.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.app.model.Categoria;
import com.biblioteca.app.repository.CategoriaRepository;

@Service
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // Listar todas las categorias
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    // Buscar categoria por ID
    public Optional<Categoria> obtenerCategoriaPorId(Integer id) {
        return categoriaRepository.findById(id);
    }

    // Registrar una nueva categoria
    @Transactional
    public Categoria registrarCategoria(Categoria categoria) {
        validarCategoria(categoria);

        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setNombre(categoria.getNombre().trim());
        nuevaCategoria.setDescripcion(categoria.getDescripcion());
        nuevaCategoria.setImagen(categoria.getImagen());
        nuevaCategoria.setEstado(
                categoria.getEstado() != null ? categoria.getEstado() : true
        );

        return categoriaRepository.save(nuevaCategoria);
    }

    // Actualizar una categoria existente
    @Transactional
    public Categoria actualizarCategoria(Integer id, Categoria datos) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Categoria con ID " + id + " no encontrada"
                ));

        validarCategoria(datos);

        categoria.setNombre(datos.getNombre().trim());
        categoria.setDescripcion(datos.getDescripcion());
        categoria.setImagen(datos.getImagen());

        if (datos.getEstado() != null) {
            categoria.setEstado(datos.getEstado());
        }

        return categoriaRepository.save(categoria);
    }

    // Validar datos obligatorios
    private void validarCategoria(Categoria categoria) {
        if (categoria == null
                || categoria.getNombre() == null
                || categoria.getNombre().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre de la categoria es obligatorio"
            );
        }
    }
}

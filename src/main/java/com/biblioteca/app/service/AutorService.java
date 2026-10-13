
package com.biblioteca.app.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.app.model.Autor;
import com.biblioteca.app.repository.AutorRepository;

@Service
@Transactional(readOnly = true)
public class AutorService {

    private final AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    // Listar todos los autores
    public List<Autor> listarAutores() {
        return autorRepository.findAll();
    }

    // Buscar autor por ID
    public Optional<Autor> obtenerAutorPorId(Integer id) {
        return autorRepository.findById(id);
    }

    // Registrar un nuevo autor
    @Transactional
    public Autor registrarAutor(Autor autor) {
        validarAutor(autor);

        Autor nuevoAutor = new Autor();
        nuevoAutor.setNombres(autor.getNombres().trim());
        nuevoAutor.setApellidos(autor.getApellidos());
        nuevoAutor.setEstado(
                autor.getEstado() != null ? autor.getEstado() : true
        );

        return autorRepository.save(nuevoAutor);
    }

    // Actualizar un autor existente
    @Transactional
    public Autor actualizarAutor(Integer id, Autor datos) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Autor con ID " + id + " no encontrado"
                ));

        validarAutor(datos);

        autor.setNombres(datos.getNombres().trim());
        autor.setApellidos(datos.getApellidos());

        if (datos.getEstado() != null) {
            autor.setEstado(datos.getEstado());
        }

        return autorRepository.save(autor);
    }

    // Validar los datos obligatorios
    private void validarAutor(Autor autor) {
        if (autor == null
                || autor.getNombres() == null
                || autor.getNombres().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del autor es obligatorio"
            );
        }
    }
}

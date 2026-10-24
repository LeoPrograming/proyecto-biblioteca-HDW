package com.biblioteca.app.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.app.model.Editorial;
import com.biblioteca.app.repository.EditorialRepository;

@Service
@Transactional(readOnly = true)
public class EditorialService {

    private final EditorialRepository
            editorialRepository;

    public EditorialService(
            EditorialRepository editorialRepository) {

        this.editorialRepository =
                editorialRepository;
    }


    // Listar editoriales
    public List<Editorial> listarEditoriales() {

        return editorialRepository.findAll();
    }


    // Buscar editorial por ID
    public Optional<Editorial>
    obtenerEditorialPorId(Integer id) {

        return editorialRepository.findById(id);
    }


    // Registrar editorial
    @Transactional
    public Editorial registrarEditorial(
            Editorial editorial) {

        validarEditorial(editorial);

        Editorial nuevaEditorial =
                new Editorial();

        nuevaEditorial.setNombre(
                editorial
                        .getNombre()
                        .trim()
        );

        nuevaEditorial.setEstado(
                editorial.getEstado() != null
                        ? editorial.getEstado()
                        : true
        );

        return editorialRepository.save(
                nuevaEditorial
        );
    }


    // Actualizar editorial
    @Transactional
    public Editorial actualizarEditorial(
            Integer id,
            Editorial datos) {

        Editorial editorial =
                editorialRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Editorial con ID "
                                                + id
                                                + " no encontrada"
                                )
                        );

        validarEditorial(datos);

        editorial.setNombre(
                datos
                        .getNombre()
                        .trim()
        );

        if (datos.getEstado() != null) {

            editorial.setEstado(
                    datos.getEstado()
            );
        }

        return editorialRepository.save(
                editorial
        );
    }


    // Validación
    private void validarEditorial(
            Editorial editorial) {

        if (
                editorial == null ||
                        editorial.getNombre() == null ||
                        editorial.getNombre().isBlank()
        ) {

            throw new IllegalArgumentException(
                    "El nombre de la editorial es obligatorio"
            );
        }
    }
}
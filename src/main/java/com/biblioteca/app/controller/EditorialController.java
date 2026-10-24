package com.biblioteca.app.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.biblioteca.app.model.Editorial;
import com.biblioteca.app.service.EditorialService;

@RestController
@RequestMapping("/api/editoriales")
public class EditorialController {

    private final EditorialService editorialService;

    public EditorialController(
            EditorialService editorialService) {

        this.editorialService =
                editorialService;
    }


    // Listar todas las editoriales
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>>
    listarEditoriales() {

        List<Map<String, Object>> respuesta =
                editorialService
                        .listarEditoriales()
                        .stream()
                        .map(this::aRespuesta)
                        .toList();

        return ResponseEntity.ok(
                respuesta
        );
    }


    // Buscar editorial por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>>
    obtenerEditorial(
            @PathVariable Integer id) {

        return editorialService
                .obtenerEditorialPorId(id)
                .map(editorial ->
                        ResponseEntity.ok(
                                aRespuesta(editorial)
                        )
                )
                .orElseGet(() ->
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }


    // Registrar una nueva editorial
    @PostMapping
    public ResponseEntity<Map<String, Object>>
    registrarEditorial(
            @RequestBody Editorial editorial) {

        Editorial nuevaEditorial =
                editorialService
                        .registrarEditorial(
                                editorial
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        aRespuesta(
                                nuevaEditorial
                        )
                );
    }


    // Actualizar editorial
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>>
    actualizarEditorial(
            @PathVariable Integer id,
            @RequestBody Editorial datos) {

        Editorial editorialActualizada =
                editorialService
                        .actualizarEditorial(
                                id,
                                datos
                        );

        return ResponseEntity.ok(
                aRespuesta(
                        editorialActualizada
                )
        );
    }


    // Respuesta JSON
    private Map<String, Object> aRespuesta(
            Editorial editorial) {

        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        respuesta.put(
                "idEditorial",
                editorial.getIdEditorial()
        );

        respuesta.put(
                "nombre",
                editorial.getNombre()
        );

        respuesta.put(
                "estado",
                editorial.getEstado()
        );

        return respuesta;
    }


    @ExceptionHandler(
            NoSuchElementException.class
    )
    public ResponseEntity<Map<String, String>>
    manejarNoEncontrado(
            NoSuchElementException excepcion) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        Map.of(
                                "mensaje",
                                excepcion.getMessage()
                        )
                );
    }


    @ExceptionHandler(
            IllegalArgumentException.class
    )
    public ResponseEntity<Map<String, String>>
    manejarDatosInvalidos(
            IllegalArgumentException excepcion) {

        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "mensaje",
                                excepcion.getMessage()
                        )
                );
    }
}
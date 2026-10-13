
package com.biblioteca.app.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.biblioteca.app.model.Autor;
import com.biblioteca.app.service.AutorService;

@RestController
@RequestMapping("/api/autores")
public class AutorController {

    private final AutorService autorService;

    public AutorController(AutorService autorService) {
        this.autorService = autorService;
    }

    // Listar todos los autores
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarAutores() {

        List<Map<String, Object>> respuesta = autorService
                .listarAutores()
                .stream()
                .map(this::aRespuesta)
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    // Buscar autor por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerAutor(
            @PathVariable Integer id) {

        return autorService.obtenerAutorPorId(id)
                .map(autor -> ResponseEntity.ok(aRespuesta(autor)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Registrar un nuevo autor
    @PostMapping
    public ResponseEntity<Map<String, Object>> registrarAutor(
            @RequestBody Autor autor) {

        Autor nuevoAutor = autorService.registrarAutor(autor);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(aRespuesta(nuevoAutor));
    }

    // Actualizar un autor existente
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarAutor(
            @PathVariable Integer id,
            @RequestBody Autor datos) {

        Autor autorActualizado = autorService.actualizarAutor(
                id, datos
        );

        return ResponseEntity.ok(aRespuesta(autorActualizado));
    }

    // Construir respuesta JSON sin relaciones circulares
    private Map<String, Object> aRespuesta(Autor autor) {

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("idAutor", autor.getIdAutor());
        respuesta.put("nombres", autor.getNombres());
        respuesta.put("apellidos", autor.getApellidos());
        respuesta.put("estado", autor.getEstado());

        return respuesta;
    }

    // Manejar autores inexistentes
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> manejarNoEncontrado(
            NoSuchElementException excepcion) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", excepcion.getMessage()));
    }

    // Manejar datos invalidos
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarDatosInvalidos(
            IllegalArgumentException excepcion) {

        return ResponseEntity
                .badRequest()
                .body(Map.of("mensaje", excepcion.getMessage()));
    }
}

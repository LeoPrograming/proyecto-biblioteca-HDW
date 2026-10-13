
package com.biblioteca.app.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.biblioteca.app.model.Categoria;
import com.biblioteca.app.service.CategoriaService;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // Listar todas las categorias
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarCategorias() {

        List<Map<String, Object>> respuesta = categoriaService
                .listarCategorias()
                .stream()
                .map(this::aRespuesta)
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    // Buscar categoria por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerCategoria(
            @PathVariable Integer id) {

        return categoriaService.obtenerCategoriaPorId(id)
                .map(categoria -> ResponseEntity.ok(
                        aRespuesta(categoria)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Registrar una nueva categoria
    @PostMapping
    public ResponseEntity<Map<String, Object>> registrarCategoria(
            @RequestBody Categoria categoria) {

        Categoria nuevaCategoria =
                categoriaService.registrarCategoria(categoria);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(aRespuesta(nuevaCategoria));
    }

    // Actualizar una categoria existente
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarCategoria(
            @PathVariable Integer id,
            @RequestBody Categoria datos) {

        Categoria categoriaActualizada =
                categoriaService.actualizarCategoria(id, datos);

        return ResponseEntity.ok(
                aRespuesta(categoriaActualizada)
        );
    }

    // Construir respuesta JSON
    private Map<String, Object> aRespuesta(Categoria categoria) {

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("idCategoria", categoria.getIdCategoria());
        respuesta.put("nombre", categoria.getNombre());
        respuesta.put("descripcion", categoria.getDescripcion());
        respuesta.put("imagen", categoria.getImagen());
        respuesta.put("estado", categoria.getEstado());

        return respuesta;
    }

    // Manejar categorias inexistentes
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

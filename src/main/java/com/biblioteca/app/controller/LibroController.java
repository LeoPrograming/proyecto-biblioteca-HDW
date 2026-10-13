
package com.biblioteca.app.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.biblioteca.app.model.Autor;
import com.biblioteca.app.model.Libro;
import com.biblioteca.app.service.LibroService;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    // Listar todos los libros
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarLibros() {
        List<Map<String, Object>> respuesta = libroService
                .listarLibros()
                .stream()
                .map(this::aRespuesta)
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    // Buscar libro por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerLibro(
            @PathVariable Integer id) {

        return libroService.obtenerLibroPorId(id)
                .map(libro -> ResponseEntity.ok(aRespuesta(libro)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Registrar un nuevo libro
    @PostMapping
    public ResponseEntity<Map<String, Object>> registrarLibro(
            @RequestBody Libro libro) {

        Libro nuevoLibro = libroService.registrarLibro(libro);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(aRespuesta(nuevoLibro));
    }

    // Actualizar un libro existente
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarLibro(
            @PathVariable Integer id,
            @RequestBody Libro datos) {

        Libro libroActualizado = libroService.actualizarLibro(
                id, datos
        );

        return ResponseEntity.ok(aRespuesta(libroActualizado));
    }

    // Cambiar el estado de un libro
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Map<String, Object>> cambiarEstado(
            @PathVariable Integer id,
            @RequestBody Map<String, Boolean> peticion) {

        Boolean estado = peticion.get("estado");

        Libro libro = libroService.cambiarEstado(id, estado);

        return ResponseEntity.ok(aRespuesta(libro));
    }

    // Construir respuesta JSON sin referencias circulares
    private Map<String, Object> aRespuesta(Libro libro) {
        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("idLibro", libro.getIdLibro());
        respuesta.put("titulo", libro.getTitulo());
        respuesta.put("isbn", libro.getIsbn());
        respuesta.put("stock", libro.getStock());
        respuesta.put("precio", libro.getPrecio());
        respuesta.put("estado", libro.getEstado());

        respuesta.put(
                "idCategoria",
                libro.getCategoria() == null
                        ? null
                        : libro.getCategoria().getIdCategoria()
        );

        respuesta.put(
                "idEditorial",
                libro.getEditorial() == null
                        ? null
                        : libro.getEditorial().getIdEditorial()
        );

        List<Map<String, Object>> autores =
                libro.getAutores() == null
                        ? List.of()
                        : libro.getAutores().stream()
                        .map(this::autorARespuesta)
                        .toList();

        respuesta.put("autores", autores);

        return respuesta;
    }

    // Representar autor sin incluir su lista de libros
    private Map<String, Object> autorARespuesta(Autor autor) {
        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("idAutor", autor.getIdAutor());
        respuesta.put("nombres", autor.getNombres());
        respuesta.put("apellidos", autor.getApellidos());

        return respuesta;
    }

    // Recurso no encontrado
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> manejarNoEncontrado(
            NoSuchElementException excepcion) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", excepcion.getMessage()));
    }

    // Datos invalidos
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarDatosInvalidos(
            IllegalArgumentException excepcion) {

        return ResponseEntity.badRequest()
                .body(Map.of("mensaje", excepcion.getMessage()));
    }
}

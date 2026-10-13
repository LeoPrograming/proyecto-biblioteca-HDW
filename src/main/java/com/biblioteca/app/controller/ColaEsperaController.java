package com.biblioteca.app.controller;

import com.biblioteca.app.model.ColaEspera;
import com.biblioteca.app.service.ColaEsperaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cola-espera")
public class ColaEsperaController {

    private final ColaEsperaService colaEsperaService;

    public ColaEsperaController(ColaEsperaService colaEsperaService) {
        this.colaEsperaService = colaEsperaService;
    }

    @PostMapping("/usuario/{idUsuario}/libro/{idLibro}")
    public ResponseEntity<?> agregarUsuarioACola(@PathVariable Integer idUsuario,
                                                  @PathVariable Integer idLibro) {
        try {
            ColaEspera colaEspera = colaEsperaService.agregarUsuarioACola(idUsuario, idLibro);
            return ResponseEntity.status(HttpStatus.CREATED).body(aRespuesta(colaEspera));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/libro/{idLibro}")
    public ResponseEntity<List<Map<String, Object>>> consultarColaPorLibro(
            @PathVariable Integer idLibro) {
        return ResponseEntity.ok(
                colaEsperaService.consultarColaPorLibro(idLibro)
                        .stream()
                        .map(this::aRespuesta)
                        .toList()
        );
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Map<String, Object>>> consultarRegistrosPorUsuario(
            @PathVariable Integer idUsuario) {
        return ResponseEntity.ok(
                colaEsperaService.consultarRegistrosPorUsuario(idUsuario)
                        .stream()
                        .map(this::aRespuesta)
                        .toList()
        );
    }

    @PutMapping("/{idCola}/atendido")
    public ResponseEntity<?> marcarComoAtendido(@PathVariable Integer idCola) {
        try {
            return ResponseEntity.ok(
                    aRespuesta(colaEsperaService.marcarComoAtendido(idCola))
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    private Map<String, Object> aRespuesta(ColaEspera colaEspera) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("idCola", colaEspera.getIdCola());
        respuesta.put("idUsuario",
                colaEspera.getUsuario() == null ? null : colaEspera.getUsuario().getIdUsuario());
        respuesta.put("idLibro",
                colaEspera.getLibro() == null ? null : colaEspera.getLibro().getIdLibro());
        respuesta.put("fechaRegistro", colaEspera.getFechaRegistro());
        respuesta.put("estado", colaEspera.getEstado());
        return respuesta;
    }
}

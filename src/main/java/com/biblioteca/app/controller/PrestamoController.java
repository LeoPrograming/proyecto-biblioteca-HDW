package com.biblioteca.app.controller;

import com.biblioteca.app.model.DetallePrestamo;
import com.biblioteca.app.model.Prestamo;
import com.biblioteca.app.service.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @GetMapping
    public ResponseEntity<List<Prestamo>> listar() {
        return ResponseEntity.ok(prestamoService.listarPrestamos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prestamo> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(prestamoService.buscarPorId(id));
    }

    @PutMapping("/devolucion/{idDetalle}")
    public ResponseEntity<DetallePrestamo> registrarDevolucion(@PathVariable Integer idDetalle) {
        DetallePrestamo detalleDevuelto = prestamoService.registrarDevolucion(idDetalle);
        return ResponseEntity.ok(detalleDevuelto);
    }
}
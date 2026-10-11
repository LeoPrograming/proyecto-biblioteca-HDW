package com.biblioteca.app.controller;

import com.biblioteca.app.model.Pago;
import com.biblioteca.app.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @GetMapping
    public ResponseEntity<List<Pago>> listarPagos() {
        List<Pago> pagos = pagoService.obtenerTodosLosPagos();
        return ResponseEntity.ok(pagos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pago> obtenerPagoPorId(@PathVariable Integer id) {
        return pagoService.obtenerPagoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/prestamo/{idPrestamo}")
    public ResponseEntity<List<Pago>> listarPagosPorPrestamo(@PathVariable Integer idPrestamo) {
        List<Pago> pagos = pagoService.obtenerPagosPorPrestamo(idPrestamo);
        return ResponseEntity.ok(pagos);
    }

    @PostMapping
    public ResponseEntity<Pago> registrarPago(@RequestBody Pago pago) {
        Pago nuevoPago = pagoService.registrarPago(pago);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPago);
    }
}
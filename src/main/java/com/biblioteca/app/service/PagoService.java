package com.biblioteca.app.service;

import com.biblioteca.app.model.Pago;
import com.biblioteca.app.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    public List<Pago> obtenerTodosLosPagos() {
        return pagoRepository.findAll();
    }

    public Optional<Pago> obtenerPagoPorId(Integer id) {
        return pagoRepository.findById(id);
    }

    public List<Pago> obtenerPagosPorPrestamo(Integer idPrestamo) {
        return pagoRepository.findByPrestamoIdPrestamo(idPrestamo);
    }

    public Pago registrarPago(Pago pago) {
        return pagoRepository.save(pago);
    }
}
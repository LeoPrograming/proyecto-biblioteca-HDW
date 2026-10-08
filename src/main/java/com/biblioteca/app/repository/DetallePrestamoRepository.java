package com.biblioteca.app.repository;

import com.biblioteca.app.model.DetallePrestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePrestamoRepository extends JpaRepository<DetallePrestamo, Integer> {

    List<DetallePrestamo> findByPrestamo_IdPrestamo(Integer idPrestamo);
}
package com.biblioteca.app.repository;

import com.biblioteca.app.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Integer> {

    List<Prestamo> findByUsuario_IdUsuario(Integer idUsuario);

    List<Prestamo> findByEstado(String estado);
}
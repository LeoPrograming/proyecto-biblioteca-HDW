package com.biblioteca.app.repository;

import com.biblioteca.app.model.ColaEspera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColaEsperaRepository extends JpaRepository<ColaEspera, Integer> {

    List<ColaEspera> findByUsuario_IdUsuario(Integer idUsuario);

    List<ColaEspera> findByLibro_IdLibro(Integer idLibro);

    List<ColaEspera> findByEstado(String estado);

    boolean existsByUsuario_IdUsuarioAndLibro_IdLibroAndEstado(
            Integer idUsuario,
            Integer idLibro,
            String estado
    );
}

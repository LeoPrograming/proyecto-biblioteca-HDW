package com.biblioteca.app.repository;

import com.biblioteca.app.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Integer> {

    List<Prestamo> findByUsuario_IdUsuario(Integer idUsuario);

    List<Prestamo> findByEstado(String estado);

    List<Prestamo> findAllByOrderByIdPrestamoDesc();

    long countByComprobante_IdComprobante(Integer idComprobante);

    @Query("""
        SELECT p FROM Prestamo p
        LEFT JOIN p.usuario u
        LEFT JOIN p.comprobante c
        WHERE LOWER(u.nombres) LIKE LOWER(CONCAT('%', :buscar, '%'))
           OR LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :buscar, '%'))
           OR LOWER(u.usuario) LIKE LOWER(CONCAT('%', :buscar, '%'))
           OR LOWER(c.tipo) LIKE LOWER(CONCAT('%', :buscar, '%'))
           OR LOWER(p.serie) LIKE LOWER(CONCAT('%', :buscar, '%'))
           OR LOWER(p.numero) LIKE LOWER(CONCAT('%', :buscar, '%'))
           OR LOWER(p.estado) LIKE LOWER(CONCAT('%', :buscar, '%'))
           OR CAST(p.idPrestamo AS string) LIKE CONCAT('%', :buscar, '%')
           OR CAST(p.montoTotal AS string) LIKE CONCAT('%', :buscar, '%')
        ORDER BY p.idPrestamo DESC
        """)
    List<Prestamo> buscarPrestamos(@Param("buscar") String buscar);
}
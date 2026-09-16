package com.biblioteca.app.repository;

import com.biblioteca.app.entity.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoUsuarioRepository extends JpaRepository<TipoUsuario, Integer> {
    
    // Consulta necesaria para recuperar un tipo de usuario específico
    Optional<TipoUsuario> findByNombreIgnoreCase(String nombre);
}
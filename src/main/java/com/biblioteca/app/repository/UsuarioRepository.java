package com.biblioteca.app.repository;

import com.biblioteca.app.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // 1. Consulta por identificador de acceso (para el login posterior)
    Optional<Usuario> findByUsuario(String usuario);

    // 2. Consultas para validación de unicidad (evitar duplicados)
    Optional<Usuario> findByCorreoE(String correoE);
    Optional<Usuario> findByDni(String dni);

    boolean existsByUsuario(String usuario);
    boolean existsByCorreoE(String correoE);
    boolean existsByDni(String dni);

    // 3. Consultas para listados requeridos
    List<Usuario> findAllByOrderByIdUsuarioDesc();

    List<Usuario> findByDniContainingIgnoreCaseOrUsuarioContainingIgnoreCaseOrderByIdUsuarioDesc(String dni, String usuario);
}
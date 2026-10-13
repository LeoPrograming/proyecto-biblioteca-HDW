package com.biblioteca.app.service;

import com.biblioteca.app.entity.Usuario;
import com.biblioteca.app.model.ColaEspera;
import com.biblioteca.app.model.Libro;
import com.biblioteca.app.repository.ColaEsperaRepository;
import com.biblioteca.app.repository.LibroRepository;
import com.biblioteca.app.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ColaEsperaService {

    private static final String ESTADO_EN_ESPERA = "en_espera";
    private static final String ESTADO_ATENDIDO = "atendido";

    private final ColaEsperaRepository colaEsperaRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;

    public ColaEsperaService(ColaEsperaRepository colaEsperaRepository,
                             UsuarioRepository usuarioRepository,
                             LibroRepository libroRepository) {
        this.colaEsperaRepository = colaEsperaRepository;
        this.usuarioRepository = usuarioRepository;
        this.libroRepository = libroRepository;
    }

    public ColaEspera agregarUsuarioACola(Integer idUsuario, Integer idLibro) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        Libro libro = libroRepository.findById(idLibro)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado."));

        if (Boolean.TRUE.equals(libro.getEstado())) {
            throw new IllegalStateException("El libro está disponible y no requiere cola de espera.");
        }

        boolean duplicadoActivo = colaEsperaRepository
                .existsByUsuario_IdUsuarioAndLibro_IdLibroAndEstado(
                        idUsuario,
                        idLibro,
                        ESTADO_EN_ESPERA
                );

        if (duplicadoActivo) {
            throw new IllegalStateException(
                    "El usuario ya tiene un registro activo para este libro."
            );
        }

        ColaEspera colaEspera = new ColaEspera();
        colaEspera.setUsuario(usuario);
        colaEspera.setLibro(libro);
        colaEspera.setFechaRegistro(LocalDateTime.now());
        colaEspera.setEstado(ESTADO_EN_ESPERA);

        return colaEsperaRepository.save(colaEspera);
    }

    @Transactional(readOnly = true)
    public List<ColaEspera> consultarColaPorLibro(Integer idLibro) {
        return colaEsperaRepository.findByLibro_IdLibro(idLibro);
    }

    @Transactional(readOnly = true)
    public List<ColaEspera> consultarRegistrosPorUsuario(Integer idUsuario) {
        return colaEsperaRepository.findByUsuario_IdUsuario(idUsuario);
    }

    public ColaEspera marcarComoAtendido(Integer idCola) {
        ColaEspera colaEspera = colaEsperaRepository.findById(idCola)
                .orElseThrow(() -> new IllegalArgumentException("Registro de cola no encontrado."));

        colaEspera.setEstado(ESTADO_ATENDIDO);
        return colaEsperaRepository.save(colaEspera);
    }
}

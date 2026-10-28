package com.biblioteca.app.service;

import com.biblioteca.app.entity.Usuario;
import com.biblioteca.app.model.DetallePrestamo;
import com.biblioteca.app.model.Libro;
import com.biblioteca.app.model.Prestamo;
import com.biblioteca.app.repository.DetallePrestamoRepository;
import com.biblioteca.app.repository.LibroRepository;
import com.biblioteca.app.repository.PrestamoRepository;
import com.biblioteca.app.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrestamoService {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private DetallePrestamoRepository detallePrestamoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LibroRepository libroRepository;

    public List<Prestamo> listarPrestamos() {
        return (List<Prestamo>) prestamoRepository.findAll();
    }

    public Prestamo buscarPorId(Integer id) {
        return prestamoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El préstamo con ID " + id + " no existe."));
    }

    public List<Prestamo> buscarPorUsuario(Integer idUsuario) {
        // Asegúrate de que PrestamoRepository tenga el método findByUsuario_IdUsuario(Integer id)
        return prestamoRepository.findByUsuario_IdUsuario(idUsuario);
    }

    @Transactional
    public Prestamo registrarPrestamo(Prestamo prestamo, List<DetallePrestamo> detalles) {
        Usuario usuario = usuarioRepository.findById(prestamo.getUsuario().getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        
        prestamo.setUsuario(usuario);
        prestamo.setEstado("abierto");
        
        if (prestamo.getFechaPrestamo() == null) {
            prestamo.setFechaPrestamo(LocalDateTime.now());
        }

        Prestamo prestamoGuardado = prestamoRepository.save(prestamo);

        for (DetallePrestamo detalle : detalles) {
            Libro libro = libroRepository.findById(detalle.getLibro().getIdLibro())
                    .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado."));

            // Verificamos si el libro está disponible (estado = true) y tiene stock mayor a 0
            if (libro.getEstado() == null || !libro.getEstado()) {
                throw new IllegalArgumentException(
                        "El libro " + libro.getTitulo() + " no está disponible."
                );
            }

            libro.setEstado(false);

            libroRepository.save(libro);

            // Configurar el detalle
            detalle.setPrestamo(prestamoGuardado);
            detalle.setLibro(libro);
            detalle.setEstado("prestado");
            detallePrestamoRepository.save(detalle);
        }

        return prestamoGuardado;
    }

    @Transactional
    public DetallePrestamo registrarDevolucion(Integer idDetalle) {
        DetallePrestamo detalle = detallePrestamoRepository.findById(idDetalle)
                .orElseThrow(() -> new IllegalArgumentException("El detalle de préstamo no existe."));

        if ("devuelto".equalsIgnoreCase(detalle.getEstado())) {
            throw new IllegalArgumentException("Devolución duplicada: Este libro ya fue devuelto.");
        }

        // 1. Actualizar el detalle a devuelto
        detalle.setEstado("devuelto");
        detalle.setFechaDevolucion(LocalDate.now());
        detallePrestamoRepository.save(detalle);

        // 2. Liberar el libro (Aumentamos el stock y lo ponemos disponible nuevamente)
        Libro libro = detalle.getLibro();

        libro.setEstado(true);

        libroRepository.save(libro);

        // 3. Verificar si todos los detalles de este préstamo ya están devueltos para cerrarlo
        Prestamo prestamo = detalle.getPrestamo();
        List<DetallePrestamo> todosLosDetalles = detallePrestamoRepository.findByPrestamo_IdPrestamo(prestamo.getIdPrestamo());
        
        boolean todosDevueltos = todosLosDetalles.stream()
                .allMatch(d -> "devuelto".equalsIgnoreCase(d.getEstado()));

        if (todosDevueltos) {
            prestamo.setEstado("cerrado");
            prestamoRepository.save(prestamo);
        }

        return detalle;
    }
}

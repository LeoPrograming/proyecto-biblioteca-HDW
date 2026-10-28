package com.biblioteca.app.repository;

import com.biblioteca.app.entity.TipoUsuario;
import com.biblioteca.app.entity.Usuario;
import com.biblioteca.app.model.Comprobante;
import com.biblioteca.app.model.DetallePrestamo;
import com.biblioteca.app.model.Libro;
import com.biblioteca.app.model.Prestamo;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import com.biblioteca.app.model.Categoria;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DetallePrestamoRepositoryTest {

    @Autowired
    private DetallePrestamoRepository detallePrestamoRepository;

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private EntityManager entityManager;

    private Usuario crearUsuario() {
        TipoUsuario tipoUsuario = new TipoUsuario();
        tipoUsuario.setNombre("TEST_LECTOR_DETALLE");
        tipoUsuario.setDescripcion("Tipo de usuario para pruebas");
        entityManager.persist(tipoUsuario);

        Usuario usuario = new Usuario();
        usuario.setTipoUsuario(tipoUsuario);
        usuario.setDni("72000001");
        usuario.setUsuario("lector_detalle");
        usuario.setContrasena("Test123456");
        usuario.setNombres("Lector");
        usuario.setApellidos("Detalle");
        usuario.setCorreoE("lector_detalle@biblioteca.com");
        usuario.setTelefono("988888888");
        usuario.setDireccion("Dirección de prueba");
        usuario.setEstado(true);

        entityManager.persist(usuario);
        entityManager.flush();

        return usuario;
    }

    private Comprobante crearComprobante() {
        Comprobante comprobante = new Comprobante();
        comprobante.setTipo("Boleta");

        entityManager.persist(comprobante);
        entityManager.flush();

        return comprobante;
    }
    private Categoria crearCategoria() {

        Categoria categoria = new Categoria();

        categoria.setNombre("Categoria Test Detalle");
        categoria.setDescripcion("Categoria para pruebas");
        categoria.setEstado(true);

        entityManager.persist(categoria);
        entityManager.flush();

        return categoria;
    }

    private Libro crearLibro() {

        Categoria categoria = crearCategoria();

        Libro libro = new Libro();

        libro.setTitulo("Libro de prueba");
        libro.setIsbn("ISBN-TEST-001");

        libro.setPrecioTiempo(
                new BigDecimal("10.00")
        );

        libro.setEstado(true);
        libro.setCategoria(categoria);

        entityManager.persist(libro);
        entityManager.flush();

        return libro;
    }

    private Prestamo crearPrestamo(
            Usuario usuario,
            Comprobante comprobante) {

        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setComprobante(comprobante);
        prestamo.setFechaPrestamo(LocalDateTime.now());
        prestamo.setEstado("abierto");
        prestamo.setSerie("B001");
        prestamo.setNumero("000010");
        prestamo.setImpuesto(new BigDecimal("18.00"));
        prestamo.setMontoTotal(new BigDecimal("50.00"));
        prestamo.setObservaciones("Préstamo para detalle");

        return prestamoRepository.saveAndFlush(prestamo);
    }

    private DetallePrestamo crearDetalle(
            Prestamo prestamo,
            Libro libro) {

        DetallePrestamo detalle = new DetallePrestamo();

        detalle.setPrestamo(prestamo);
        detalle.setLibro(libro);
        detalle.setPrecioTiempo(new BigDecimal("5.00"));
        detalle.setTiempo(7);
        detalle.setFechaCompromiso(LocalDate.now().plusDays(7));
        detalle.setEstado("prestado");
        detalle.setSubtotal(new BigDecimal("35.00"));

        return detalle;
    }

    @Test
    @DisplayName("Debe guardar y recuperar un detalle de préstamo")
    void debeGuardarYRecuperarDetallePrestamo() {
        Usuario usuario = crearUsuario();
        Comprobante comprobante = crearComprobante();
        Libro libro = crearLibro();

        Prestamo prestamo =
                crearPrestamo(usuario, comprobante);

        DetallePrestamo detalle =
                crearDetalle(prestamo, libro);

        DetallePrestamo guardado =
                detallePrestamoRepository.saveAndFlush(detalle);

        Optional<DetallePrestamo> encontrado =
                detallePrestamoRepository.findById(
                        guardado.getIdDetallePrestamo());

        assertTrue(encontrado.isPresent());
        assertEquals("prestado", encontrado.get().getEstado());
        assertEquals(
                libro.getIdLibro(),
                encontrado.get().getLibro().getIdLibro()
        );
    }

    @Test
    @DisplayName("Debe recuperar detalles asociados a un préstamo")
    void debeRecuperarDetallesPorPrestamo() {
        Usuario usuario = crearUsuario();
        Comprobante comprobante = crearComprobante();
        Libro libro = crearLibro();

        Prestamo prestamo =
                crearPrestamo(usuario, comprobante);

        DetallePrestamo detalle =
                crearDetalle(prestamo, libro);

        detallePrestamoRepository.saveAndFlush(detalle);

        List<DetallePrestamo> detalles =
                detallePrestamoRepository
                        .findByPrestamo_IdPrestamo(
                                prestamo.getIdPrestamo());

        assertNotNull(detalles);
        assertFalse(detalles.isEmpty());
        assertEquals(
                prestamo.getIdPrestamo(),
                detalles.get(0).getPrestamo().getIdPrestamo()
        );
    }

    @Test
    @DisplayName("Debe devolver lista vacía cuando un préstamo no tiene detalles")
    void debeDevolverListaVaciaCuandoPrestamoNoTieneDetalles() {
        List<DetallePrestamo> detalles =
                detallePrestamoRepository
                        .findByPrestamo_IdPrestamo(999999);

        assertNotNull(detalles);
        assertTrue(detalles.isEmpty());
    }
}
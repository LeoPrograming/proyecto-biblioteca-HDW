package com.biblioteca.app.repository;

import com.biblioteca.app.entity.TipoUsuario;
import com.biblioteca.app.entity.Usuario;
import com.biblioteca.app.model.Comprobante;
import com.biblioteca.app.model.Prestamo;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PrestamoRepositoryTest {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private EntityManager entityManager;

    private Usuario crearUsuario() {
        TipoUsuario tipoUsuario = new TipoUsuario();
        tipoUsuario.setNombre("TEST_LECTOR_PRESTAMO");
        tipoUsuario.setDescripcion("Tipo de usuario para pruebas");
        entityManager.persist(tipoUsuario);

        Usuario usuario = new Usuario();
        usuario.setTipoUsuario(tipoUsuario);
        usuario.setDni("71000001");
        usuario.setUsuario("lector_prestamo");
        usuario.setContrasena("Test123456");
        usuario.setNombres("Lector");
        usuario.setApellidos("Prueba");
        usuario.setCorreoE("lector_prestamo@biblioteca.com");
        usuario.setTelefono("999999999");
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

    private Prestamo crearPrestamo(
            Usuario usuario,
            Comprobante comprobante,
            String estado,
            String numero) {

        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setComprobante(comprobante);
        prestamo.setFechaPrestamo(LocalDateTime.now());
        prestamo.setEstado(estado);
        prestamo.setSerie("B001");
        prestamo.setNumero(numero);
        prestamo.setImpuesto(new BigDecimal("18.00"));
        prestamo.setMontoTotal(new BigDecimal("100.00"));
        prestamo.setObservaciones("Préstamo de prueba");

        return prestamo;
    }

    @Test
    @DisplayName("Debe guardar y recuperar un préstamo")
    void debeGuardarYRecuperarPrestamo() {
        Usuario usuario = crearUsuario();
        Comprobante comprobante = crearComprobante();

        Prestamo prestamo =
                crearPrestamo(usuario, comprobante, "abierto", "000001");

        Prestamo guardado = prestamoRepository.saveAndFlush(prestamo);

        Optional<Prestamo> encontrado =
                prestamoRepository.findById(guardado.getIdPrestamo());

        assertTrue(encontrado.isPresent());
        assertEquals("abierto", encontrado.get().getEstado());
        assertEquals("000001", encontrado.get().getNumero());
        assertEquals(usuario.getIdUsuario(),
                encontrado.get().getUsuario().getIdUsuario());
    }

    @Test
    @DisplayName("Debe buscar préstamos por usuario")
    void debeBuscarPrestamosPorUsuario() {
        Usuario usuario = crearUsuario();
        Comprobante comprobante = crearComprobante();

        Prestamo prestamo =
                crearPrestamo(usuario, comprobante, "abierto", "000002");

        prestamoRepository.saveAndFlush(prestamo);

        List<Prestamo> prestamos =
                prestamoRepository.findByUsuario_IdUsuario(
                        usuario.getIdUsuario());

        assertNotNull(prestamos);
        assertFalse(prestamos.isEmpty());
        assertEquals(
                usuario.getIdUsuario(),
                prestamos.get(0).getUsuario().getIdUsuario()
        );
    }

    @Test
    @DisplayName("Debe buscar préstamos por estado")
    void debeBuscarPrestamosPorEstado() {
        Usuario usuario = crearUsuario();
        Comprobante comprobante = crearComprobante();

        Prestamo prestamo =
                crearPrestamo(usuario, comprobante, "abierto", "000003");

        prestamoRepository.saveAndFlush(prestamo);

        List<Prestamo> prestamos =
                prestamoRepository.findByEstado("abierto");

        assertNotNull(prestamos);
        assertFalse(prestamos.isEmpty());
        assertTrue(
                prestamos.stream()
                        .allMatch(p -> "abierto".equals(p.getEstado()))
        );
    }

    @Test
    @DisplayName("Debe devolver lista vacía cuando el usuario no tiene préstamos")
    void debeDevolverListaVaciaCuandoUsuarioNoTienePrestamos() {
        List<Prestamo> prestamos =
                prestamoRepository.findByUsuario_IdUsuario(999999);

        assertNotNull(prestamos);
        assertTrue(prestamos.isEmpty());
    }
}
package com.biblioteca.app.repository;

import com.biblioteca.app.entity.TipoUsuario;
import com.biblioteca.app.entity.Usuario;
import com.biblioteca.app.model.ColaEspera;
import com.biblioteca.app.model.Libro;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ColaEsperaRepositoryTest {

    @Autowired
    private ColaEsperaRepository colaEsperaRepository;

    @Autowired
    private EntityManager entityManager;

    private Usuario crearUsuario() {
        TipoUsuario tipoUsuario = new TipoUsuario();
        tipoUsuario.setNombre("TEST_LECTOR_COLA");
        tipoUsuario.setDescripcion("Tipo de usuario para pruebas de cola");
        entityManager.persist(tipoUsuario);

        Usuario usuario = new Usuario();
        usuario.setTipoUsuario(tipoUsuario);
        usuario.setDni("75000001");
        usuario.setUsuario("lector_cola");
        usuario.setContrasena("Test123456");
        usuario.setNombres("Lector");
        usuario.setApellidos("Cola");
        usuario.setCorreoE("lector_cola@biblioteca.com");
        usuario.setTelefono("999999999");
        usuario.setDireccion("Dirección de prueba");
        usuario.setEstado(true);

        entityManager.persist(usuario);
        entityManager.flush();

        return usuario;
    }

    private Libro crearLibro(String isbn, String titulo) {
        Libro libro = new Libro();
        libro.setTitulo(titulo);
        libro.setIsbn(isbn);
        libro.setStock(0);
        libro.setPrecio(20.0);
        libro.setEstado(false);

        entityManager.persist(libro);
        entityManager.flush();

        return libro;
    }

    private ColaEspera crearCola(
            Usuario usuario,
            Libro libro,
            String estado) {

        ColaEspera cola = new ColaEspera();
        cola.setUsuario(usuario);
        cola.setLibro(libro);
        cola.setFechaRegistro(LocalDateTime.now());
        cola.setEstado(estado);

        return cola;
    }

    @Test
    @DisplayName("Debe guardar un registro de cola de espera")
    void debeGuardarRegistro() {
        Usuario usuario = crearUsuario();
        Libro libro = crearLibro("ISBN-COLA-001", "Libro cola 1");

        ColaEspera cola = crearCola(usuario, libro, "en_espera");

        ColaEspera guardada = colaEsperaRepository.saveAndFlush(cola);

        Optional<ColaEspera> encontrada =
                colaEsperaRepository.findById(guardada.getIdCola());

        assertTrue(encontrada.isPresent());
        assertEquals("en_espera", encontrada.get().getEstado());
        assertEquals(usuario.getIdUsuario(),
                encontrada.get().getUsuario().getIdUsuario());
        assertEquals(libro.getIdLibro(),
                encontrada.get().getLibro().getIdLibro());
    }

    @Test
    @DisplayName("Debe buscar registros por usuario")
    void debeBuscarPorUsuario() {
        Usuario usuario = crearUsuario();
        Libro libro = crearLibro("ISBN-COLA-002", "Libro cola 2");

        colaEsperaRepository.saveAndFlush(
                crearCola(usuario, libro, "en_espera")
        );

        List<ColaEspera> resultado =
                colaEsperaRepository.findByUsuario_IdUsuario(
                        usuario.getIdUsuario());

        assertEquals(1, resultado.size());
        assertEquals(usuario.getIdUsuario(),
                resultado.get(0).getUsuario().getIdUsuario());
    }

    @Test
    @DisplayName("Debe buscar registros por libro")
    void debeBuscarPorLibro() {
        Usuario usuario = crearUsuario();
        Libro libro = crearLibro("ISBN-COLA-003", "Libro cola 3");

        colaEsperaRepository.saveAndFlush(
                crearCola(usuario, libro, "en_espera")
        );

        List<ColaEspera> resultado =
                colaEsperaRepository.findByLibro_IdLibro(
                        libro.getIdLibro());

        assertEquals(1, resultado.size());
        assertEquals(libro.getIdLibro(),
                resultado.get(0).getLibro().getIdLibro());
    }

    @Test
    @DisplayName("Debe buscar registros por estado")
    void debeBuscarPorEstado() {
        Usuario usuario = crearUsuario();

        Libro libro1 =
                crearLibro("ISBN-COLA-004", "Libro cola 4");

        Libro libro2 =
                crearLibro("ISBN-COLA-005", "Libro cola 5");

        colaEsperaRepository.saveAndFlush(
                crearCola(usuario, libro1, "en_espera")
        );

        colaEsperaRepository.saveAndFlush(
                crearCola(usuario, libro2, "atendido")
        );

        List<ColaEspera> resultado =
                colaEsperaRepository.findByEstado("en_espera");

        assertEquals(1, resultado.size());
        assertEquals("en_espera", resultado.get(0).getEstado());
    }

    @Test
    @DisplayName("Debe evitar registros duplicados activos para el mismo usuario y libro")
    void debeEvitarDuplicadosActivos() {
        Usuario usuario = crearUsuario();
        Libro libro = crearLibro("ISBN-COLA-006", "Libro cola 6");

        ColaEspera primera =
                crearCola(usuario, libro, "en_espera");

        colaEsperaRepository.saveAndFlush(primera);

        assertTrue(
                colaEsperaRepository
                        .existsByUsuario_IdUsuarioAndLibro_IdLibroAndEstado(
                                usuario.getIdUsuario(),
                                libro.getIdLibro(),
                                "en_espera"
                        )
        );

        ColaEspera duplicada =
                crearCola(usuario, libro, "en_espera");

        assertThrows(
                DataIntegrityViolationException.class,
                () -> colaEsperaRepository.saveAndFlush(duplicada)
        );
    }
}

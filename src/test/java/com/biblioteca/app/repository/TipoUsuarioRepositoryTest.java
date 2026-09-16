package com.biblioteca.app.repository;

import com.biblioteca.app.entity.TipoUsuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TipoUsuarioRepositoryTest {

    @Autowired
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Test
    @DisplayName("Debe guardar y recuperar un tipo de usuario")
    void debeGuardarYRecuperarTipoUsuario() {
        TipoUsuario tipoUsuario = new TipoUsuario();
        tipoUsuario.setNombre("TEST_TIPO_USUARIO");

        TipoUsuario guardado = tipoUsuarioRepository.saveAndFlush(tipoUsuario);

        Optional<TipoUsuario> encontrado = tipoUsuarioRepository.findById(guardado.getIdTipoUsuario());

        assertTrue(encontrado.isPresent());
        assertEquals("TEST_TIPO_USUARIO", encontrado.get().getNombre());
    }

    @Test
    @DisplayName("Debe buscar un tipo de usuario por nombre ignorando mayúsculas")
    void debeBuscarTipoUsuarioPorNombreIgnoreCase() {
        TipoUsuario tipoUsuario = new TipoUsuario();
        tipoUsuario.setNombre("TEST_LECTOR");

        tipoUsuarioRepository.saveAndFlush(tipoUsuario);

        Optional<TipoUsuario> encontrado = tipoUsuarioRepository.findByNombreIgnoreCase("test_lector");

        assertTrue(encontrado.isPresent());
        assertEquals("TEST_LECTOR", encontrado.get().getNombre());
    }

    @Test
    @DisplayName("Debe devolver Optional vacío cuando no existe el tipo de usuario")
    void debeDevolverOptionalVacioCuandoNoExisteTipoUsuario() {
        Optional<TipoUsuario> encontrado = tipoUsuarioRepository.findByNombreIgnoreCase("NO_EXISTE");

        assertTrue(encontrado.isEmpty());
    }

    @Test
    @DisplayName("Debe devolver una lista vacía cuando no hay tipos de usuario registrados")
    void debeDevolverListaVaciaCuandoNoHayRegistros() {
        tipoUsuarioRepository.deleteAll();
        tipoUsuarioRepository.flush();

        List<TipoUsuario> tipos = tipoUsuarioRepository.findAll();

        assertNotNull(tipos);
        assertTrue(tipos.isEmpty());
    }
}
package com.biblioteca.app.repository;

import com.biblioteca.app.entity.TipoUsuario;
import com.biblioteca.app.entity.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoUsuarioRepository tipoUsuarioRepository;

    private TipoUsuario crearTipoUsuario() {
        TipoUsuario tipoUsuario = new TipoUsuario();
        tipoUsuario.setNombre("TEST_LECTOR");
        return tipoUsuarioRepository.saveAndFlush(tipoUsuario);
    }

    private Usuario crearUsuario(TipoUsuario tipoUsuario) {
        Usuario usuario = new Usuario();
        usuario.setDni("70000001");
        usuario.setUsuario("usuario_test");
        usuario.setContrasena("Test123456");
        usuario.setNombres("Usuario");
        usuario.setApellidos("Prueba");
        usuario.setCorreoE("usuario_test@biblioteca.com");
        usuario.setTelefono("999999999");
        usuario.setDireccion("Dirección de prueba");
        usuario.setTipoUsuario(tipoUsuario);
        return usuario;
    }

    @Test
    @DisplayName("Debe guardar y recuperar un usuario asociado a un tipo válido")
    void debeGuardarYRecuperarUsuarioConTipoValido() {
        TipoUsuario tipoUsuario = crearTipoUsuario();

        Usuario usuario = crearUsuario(tipoUsuario);
        Usuario guardado = usuarioRepository.saveAndFlush(usuario);

        Optional<Usuario> encontrado = usuarioRepository.findById(guardado.getIdUsuario());

        assertTrue(encontrado.isPresent());
        assertEquals("usuario_test", encontrado.get().getUsuario());
        assertEquals("TEST_LECTOR", encontrado.get().getTipoUsuario().getNombre());
    }

    @Test
    @DisplayName("Debe buscar un usuario existente por identificador de acceso")
    void debeBuscarUsuarioPorIdentificadorDeAcceso() {
        TipoUsuario tipoUsuario = crearTipoUsuario();

        Usuario usuario = crearUsuario(tipoUsuario);
        usuarioRepository.saveAndFlush(usuario);

        Optional<Usuario> encontrado = usuarioRepository.findByUsuario("usuario_test");

        assertTrue(encontrado.isPresent());
        assertEquals("usuario_test", encontrado.get().getUsuario());
    }

    @Test
    @DisplayName("Debe devolver Optional vacío cuando el usuario no existe")
    void debeDevolverOptionalVacioCuandoUsuarioNoExiste() {
        Optional<Usuario> encontrado = usuarioRepository.findByUsuario("usuario_inexistente");

        assertTrue(encontrado.isEmpty());
    }

    @Test
    @DisplayName("Debe recuperar usuario por identificador")
    void debeRecuperarUsuarioPorId() {
        TipoUsuario tipoUsuario = crearTipoUsuario();

        Usuario usuario = crearUsuario(tipoUsuario);
        Usuario guardado = usuarioRepository.saveAndFlush(usuario);

        Optional<Usuario> encontrado = usuarioRepository.findById(guardado.getIdUsuario());

        assertTrue(encontrado.isPresent());
        assertEquals(guardado.getIdUsuario(), encontrado.get().getIdUsuario());
    }

    @Test
    @DisplayName("Debe devolver lista vacía cuando no hay usuarios registrados")
    void debeDevolverListaVaciaCuandoNoHayUsuarios() {
        usuarioRepository.deleteAll();
        usuarioRepository.flush();

        List<Usuario> usuarios = usuarioRepository.findAll();

        assertNotNull(usuarios);
        assertTrue(usuarios.isEmpty());
    }

    @Test
    @DisplayName("Debe actualizar un atributo permitido del usuario")
    void debeActualizarUsuario() {
        TipoUsuario tipoUsuario = crearTipoUsuario();

        Usuario usuario = crearUsuario(tipoUsuario);
        Usuario guardado = usuarioRepository.saveAndFlush(usuario);

        guardado.setNombres("Usuario Actualizado");
        usuarioRepository.saveAndFlush(guardado);

        Optional<Usuario> encontrado = usuarioRepository.findById(guardado.getIdUsuario());

        assertTrue(encontrado.isPresent());
        assertEquals("Usuario Actualizado", encontrado.get().getNombres());
    }

    @Test
    @DisplayName("Debe eliminar usuario sin eliminar su tipo de usuario")
    void debeEliminarUsuarioSinEliminarTipoUsuario() {
        TipoUsuario tipoUsuario = crearTipoUsuario();

        Usuario usuario = crearUsuario(tipoUsuario);
        Usuario guardado = usuarioRepository.saveAndFlush(usuario);

        Integer idUsuario = guardado.getIdUsuario();
        Integer idTipoUsuario = tipoUsuario.getIdTipoUsuario();

        usuarioRepository.deleteById(idUsuario);
        usuarioRepository.flush();

        Optional<Usuario> usuarioEliminado = usuarioRepository.findById(idUsuario);
        Optional<TipoUsuario> tipoUsuarioExistente = tipoUsuarioRepository.findById(idTipoUsuario);

        assertTrue(usuarioEliminado.isEmpty());
        assertTrue(tipoUsuarioExistente.isPresent());
    }

    @Test
    @DisplayName("Debe verificar existencia por usuario, DNI y correo")
    void debeVerificarExistenciaPorCamposUnicos() {
        TipoUsuario tipoUsuario = crearTipoUsuario();

        Usuario usuario = crearUsuario(tipoUsuario);
        usuarioRepository.saveAndFlush(usuario);

        assertTrue(usuarioRepository.existsByUsuario("usuario_test"));
        assertTrue(usuarioRepository.existsByDni("70000001"));
        assertTrue(usuarioRepository.existsByCorreoE("usuario_test@biblioteca.com"));
    }

    @Test
    @DisplayName("Debe mantener restricciones de unicidad del modelo")
    void debeMantenerRestriccionesDeUnicidad() {
        TipoUsuario tipoUsuario = crearTipoUsuario();

        Usuario usuario1 = crearUsuario(tipoUsuario);
        usuarioRepository.saveAndFlush(usuario1);

        Usuario usuario2 = new Usuario();
        usuario2.setDni("70000001");
        usuario2.setUsuario("usuario_test");
        usuario2.setContrasena("Test123456");
        usuario2.setNombres("Usuario");
        usuario2.setApellidos("Duplicado");
        usuario2.setCorreoE("usuario_test@biblioteca.com");
        usuario2.setTelefono("988888888");
        usuario2.setDireccion("Otra dirección");
        usuario2.setTipoUsuario(tipoUsuario);

        assertThrows(DataIntegrityViolationException.class, () -> {
            usuarioRepository.saveAndFlush(usuario2);
        });
    }

    @Test
    @DisplayName("Debe listar usuarios ordenados por ID descendente")
    void debeListarUsuariosOrdenadosPorIdDescendente() {
        TipoUsuario tipoUsuario = crearTipoUsuario();

        Usuario usuario1 = crearUsuario(tipoUsuario);
        usuario1.setDni("70000002");
        usuario1.setUsuario("usuario_test_1");
        usuario1.setCorreoE("usuario_test_1@biblioteca.com");

        Usuario usuario2 = crearUsuario(tipoUsuario);
        usuario2.setDni("70000003");
        usuario2.setUsuario("usuario_test_2");
        usuario2.setCorreoE("usuario_test_2@biblioteca.com");

        Usuario guardado1 = usuarioRepository.saveAndFlush(usuario1);
        Usuario guardado2 = usuarioRepository.saveAndFlush(usuario2);

        List<Usuario> usuarios = usuarioRepository.findAllByOrderByIdUsuarioDesc();

        assertFalse(usuarios.isEmpty());
        assertTrue(usuarios.get(0).getIdUsuario() >= usuarios.get(1).getIdUsuario());
    }

    @Test
    @DisplayName("Debe buscar usuarios por DNI o usuario")
    void debeBuscarUsuariosPorDniOUsuario() {
        TipoUsuario tipoUsuario = crearTipoUsuario();

        Usuario usuario = crearUsuario(tipoUsuario);
        usuarioRepository.saveAndFlush(usuario);

        List<Usuario> resultado = usuarioRepository
                .findByDniContainingIgnoreCaseOrUsuarioContainingIgnoreCaseOrderByIdUsuarioDesc(
                        "70000001",
                        "usuario_test"
                );

        assertFalse(resultado.isEmpty());
        assertEquals("usuario_test", resultado.get(0).getUsuario());
    }
}
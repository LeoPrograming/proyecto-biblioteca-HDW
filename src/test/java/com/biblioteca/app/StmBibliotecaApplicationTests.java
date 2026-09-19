package com.biblioteca.app;

import com.biblioteca.app.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class StmBibliotecaApplicationTests {

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private TipoUsuarioRepository tipoUsuarioRepository;

    @MockitoBean
    private AutorRepository autorRepository;

    @MockitoBean
    private CategoriaRepository categoriaRepository;

    @MockitoBean
    private EditorialRepository editorialRepository;

    @MockitoBean
    private LibroRepository libroRepository;

    @Test
    void debeCargarElContextoDeSpring() {
    }

    @Test
    void debeConfigurarElDespachadorDeSolicitudesWeb() {
    }
}
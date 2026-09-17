package com.biblioteca.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.biblioteca.app.repository.UsuarioRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration," +
                "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
})
class StmBibliotecaApplicationTests {

    @Autowired
    private ConfigurableApplicationContext context;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void debeCargarElContextoDeSpring() {
        assertTrue(
                context.isActive(),
                "El contexto de Spring debe estar activo"
        );
    }

    @Test
    void debeConfigurarElDespachadorDeSolicitudesWeb() {
        DispatcherServlet dispatcherServlet =
                context.getBean(DispatcherServlet.class);

        assertNotNull(
                dispatcherServlet,
                "Spring MVC debe disponer de un DispatcherServlet"
        );
    }
}
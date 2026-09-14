package com.biblioteca.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ServletInitializerTests {

    @Test
    void debeRegistrarLaClasePrincipalParaElDespliegueWar() {
        ServletInitializer initializer = new ServletInitializer();

        SpringApplication application = initializer
                .configure(new SpringApplicationBuilder())
                .build();

        assertTrue(
                application.getAllSources()
                        .contains(StmBibliotecaApplication.class),
                "El inicializador WAR debe registrar la clase principal"
        );
    }
}
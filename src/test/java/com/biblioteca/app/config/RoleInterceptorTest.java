package com.biblioteca.app.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoleInterceptorTest {

    private final RoleInterceptor interceptor =
            new RoleInterceptor();

    private MockHttpServletRequest peticion(String ruta, String rol) {

        MockHttpServletRequest request =
                new MockHttpServletRequest("GET", ruta);

        if (rol != null) {

            MockHttpSession session = new MockHttpSession();
            session.setAttribute("usuarioId", 1);
            session.setAttribute("tipoUsuario", rol);

            request.setSession(session);
        }

        return request;
    }

    // Devuelve "PERMITIDO" o la URL a la que se redirigió
    private String ejecutar(MockHttpServletRequest request) throws Exception {

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        boolean permitido =
                interceptor.preHandle(request, response, new Object());

        return permitido ? "PERMITIDO" : response.getRedirectedUrl();
    }

    @Test
    void sinSesionRedirigeAlLogin() throws Exception {

        assertEquals("/login", ejecutar(peticion("/dashboard", null)));
        assertEquals("/login", ejecutar(peticion("/bibliotecario", null)));
        assertEquals("/login", ejecutar(peticion("/lector/catalogo", null)));
        assertEquals("/login", ejecutar(peticion("/prestamos", null)));
    }

    @Test
    void administradorAccedeASusRutasYNoAlLector() throws Exception {

        assertEquals("PERMITIDO", ejecutar(peticion("/dashboard", "Administrador")));
        assertEquals("PERMITIDO", ejecutar(peticion("/usuarios/editar", "Administrador")));
        assertEquals("PERMITIDO", ejecutar(peticion("/lectores", "Administrador")));
        assertEquals("/dashboard", ejecutar(peticion("/lector", "Administrador")));
        assertEquals("/dashboard", ejecutar(peticion("/lector/catalogo", "Administrador")));
        assertEquals("/dashboard", ejecutar(peticion("/bibliotecario", "Administrador")));
    }

    @Test
    void administradorConVistaBibliotecarioAccedeAlPanelBibliotecario() throws Exception {

        MockHttpServletRequest request =
                peticion("/bibliotecario", "Administrador");

        request.getSession().setAttribute("vistaBibliotecarioDesdeAdmin", true);

        assertEquals("PERMITIDO", ejecutar(request));
    }

    @Test
    void bibliotecarioNoAccedeAAdministradorNiLector() throws Exception {

        assertEquals("PERMITIDO", ejecutar(peticion("/bibliotecario", "Bibliotecario")));
        assertEquals("PERMITIDO", ejecutar(peticion("/prestamos/nuevo", "Bibliotecario")));
        assertEquals("/bibliotecario", ejecutar(peticion("/dashboard", "Bibliotecario")));
        assertEquals("/bibliotecario", ejecutar(peticion("/usuarios", "Bibliotecario")));
        assertEquals("/bibliotecario", ejecutar(peticion("/lector/prestamos", "Bibliotecario")));
    }

    @Test
    void lectorSoloAccedeASuModulo() throws Exception {

        assertEquals("PERMITIDO", ejecutar(peticion("/lector/cola", "Lector")));
        assertEquals("/lector", ejecutar(peticion("/dashboard", "Lector")));
        assertEquals("/lector", ejecutar(peticion("/lectores", "Lector")));
        assertEquals("/lector", ejecutar(peticion("/bibliotecario", "Lector")));
        assertEquals("/lector", ejecutar(peticion("/prestamos", "Lector")));
    }
}
package com.biblioteca.app.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    private static final String ADMINISTRADOR = "Administrador";
    private static final String BIBLIOTECARIO = "Bibliotecario";
    private static final String LECTOR = "Lector";

    // =========================
    // RUTAS SOLO ADMINISTRADOR
    // =========================
    private static final String[] RUTAS_ADMINISTRADOR = {
            "/dashboard",
            "/usuarios",
            "/autores",
            "/editoriales",
            "/categorias",
            "/reportes",
            "/vista-bibliotecario",
            "/salir-vista-bibliotecario"
    };

    // =========================
    // RUTAS ADMINISTRADOR + BIBLIOTECARIO
    // =========================
    private static final String[] RUTAS_COMPARTIDAS = {
            "/lectores",
            "/libros",
            "/prestamos"
    };

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        // Evita que el navegador muestre páginas privadas
        // desde caché (botón "Atrás" después de cerrar sesión)
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session =
                request.getSession(false);

        if (session == null
                || session.getAttribute("usuarioId") == null
                || session.getAttribute("tipoUsuario") == null) {

            response.sendRedirect("/login");
            return false;
        }

        String rol =
                session.getAttribute("tipoUsuario").toString();

        String ruta =
                request.getRequestURI();


        // =========================
        // PANEL Y MANTENIMIENTOS DEL ADMINISTRADOR
        // =========================
        if (coincideConAlguna(ruta, RUTAS_ADMINISTRADOR)) {

            if (ADMINISTRADOR.equalsIgnoreCase(rol)) {

                // Al volver al panel Administrador se cierra
                // la "Vista como Bibliotecario"
                if (ruta.equals("/dashboard")) {
                    session.removeAttribute("vistaBibliotecarioDesdeAdmin");
                }

                return true;
            }

            redirigirSegunRol(rol, response);
            return false;
        }


        // =========================
        // GESTIÓN COMPARTIDA (ADMIN + BIBLIOTECARIO)
        // =========================
        if (coincideConAlguna(ruta, RUTAS_COMPARTIDAS)) {

            if (ADMINISTRADOR.equalsIgnoreCase(rol)
                    || BIBLIOTECARIO.equalsIgnoreCase(rol)) {

                return true;
            }

            redirigirSegunRol(rol, response);
            return false;
        }


        // =========================
        // PANEL BIBLIOTECARIO
        // =========================
        if (coincide(ruta, "/bibliotecario")) {

            // Bibliotecario real
            if (BIBLIOTECARIO.equalsIgnoreCase(rol)) {
                return true;
            }

            // Administrador usando
            // "Vista como Bibliotecario"
            if (ADMINISTRADOR.equalsIgnoreCase(rol)) {

                Object vistaBibliotecario =
                        session.getAttribute(
                                "vistaBibliotecarioDesdeAdmin"
                        );

                if (Boolean.TRUE.equals(vistaBibliotecario)) {
                    return true;
                }

                // Si el administrador escribió
                // /bibliotecario manualmente
                response.sendRedirect("/dashboard");
                return false;
            }

            redirigirSegunRol(rol, response);
            return false;
        }


        // =========================
        // MÓDULO LECTOR
        // =========================
        if (coincide(ruta, "/lector")) {

            if (LECTOR.equalsIgnoreCase(rol)) {
                return true;
            }

            redirigirSegunRol(rol, response);
            return false;
        }


        return true;
    }


    // =========================
    // COMPARACIÓN DE RUTAS
    // =========================
    // Coincide con "/base" o "/base/...".
    // Así "/lector" NO coincide con "/lectores".
    private boolean coincide(String ruta, String base) {

        return ruta.equals(base)
                || ruta.startsWith(base + "/");
    }

    private boolean coincideConAlguna(String ruta, String[] bases) {

        for (String base : bases) {

            if (coincide(ruta, base)) {
                return true;
            }
        }

        return false;
    }


    // =========================
    // REDIRECCIÓN SEGÚN ROL
    // =========================
    private void redirigirSegunRol(
            String rol,
            HttpServletResponse response)
            throws Exception {

        if (ADMINISTRADOR.equalsIgnoreCase(rol)) {

            response.sendRedirect("/dashboard");

        } else if (BIBLIOTECARIO.equalsIgnoreCase(rol)) {

            response.sendRedirect("/bibliotecario");

        } else if (LECTOR.equalsIgnoreCase(rol)) {

            response.sendRedirect("/lector");

        } else {

            response.sendRedirect("/login");
        }
    }
}
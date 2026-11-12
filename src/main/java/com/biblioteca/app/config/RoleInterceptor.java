package com.biblioteca.app.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        HttpSession session =
                request.getSession(false);

        if (session == null
                || session.getAttribute("usuarioId") == null) {

            response.sendRedirect("/login");
            return false;
        }

        Object rolSesion =
                session.getAttribute("tipoUsuario");

        if (rolSesion == null) {

            response.sendRedirect("/login");
            return false;
        }

        String rol =
                rolSesion.toString();

        String ruta =
                request.getRequestURI();


        // =========================
        // PANEL ADMINISTRADOR
        // =========================
        if (ruta.equals("/dashboard")) {

            if (!"Administrador".equalsIgnoreCase(rol)) {

                redirigirSegunRol(
                        rol,
                        response
                );

                return false;
            }
        }


        // =========================
        // PANEL BIBLIOTECARIO
        // =========================
        if (ruta.equals("/bibliotecario")
                || ruta.startsWith("/bibliotecario/")) {

            // Bibliotecario real
            if ("Bibliotecario".equalsIgnoreCase(rol)) {
                return true;
            }

            // Administrador usando
            // "Vista como Bibliotecario"
            if ("Administrador".equalsIgnoreCase(rol)) {

                Object vistaBibliotecario =
                        session.getAttribute(
                                "vistaBibliotecarioDesdeAdmin"
                        );

                if (Boolean.TRUE.equals(
                        vistaBibliotecario
                )) {

                    return true;
                }

                // Si el administrador escribió
                // /bibliotecario manualmente
                response.sendRedirect(
                        "/dashboard"
                );

                return false;
            }

            redirigirSegunRol(
                    rol,
                    response
            );

            return false;
        }


        return true;
    }


    // =========================
    // REDIRECCIÓN SEGÚN ROL
    // =========================
    private void redirigirSegunRol(
            String rol,
            HttpServletResponse response)
            throws Exception {

        if ("Administrador".equalsIgnoreCase(rol)) {

            response.sendRedirect(
                    "/dashboard"
            );

        } else if ("Bibliotecario".equalsIgnoreCase(rol)) {

            response.sendRedirect(
                    "/bibliotecario"
            );

        } else if ("Lector".equalsIgnoreCase(rol)) {

            response.sendRedirect(
                    "/lector"
            );

        } else {

            response.sendRedirect(
                    "/login"
            );
        }
    }
}
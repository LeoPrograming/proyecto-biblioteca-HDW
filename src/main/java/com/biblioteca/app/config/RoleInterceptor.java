
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
        // desde caché después de cerrar sesión.
        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession(false);

        String ruta = request.getRequestURI();
        String metodo = request.getMethod();

        boolean esApi = ruta.startsWith("/api/");

        // =========================
        // VALIDACIÓN DE SESIÓN
        // =========================
        if (session == null
                || session.getAttribute("usuarioId") == null
                || session.getAttribute("tipoUsuario") == null) {

            if (esApi) {
                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED
                );

                response.setContentType(
                        "application/json;charset=UTF-8"
                );

                response.getWriter().write(
                        "{\"mensaje\":\"Debe iniciar sesión\"}"
                );
            } else {
                response.sendRedirect("/login");
            }

            return false;
        }

        String rol = session
                .getAttribute("tipoUsuario")
                .toString();

        boolean esLector =
                LECTOR.equalsIgnoreCase(rol);

        boolean esPersonal =
                ADMINISTRADOR.equalsIgnoreCase(rol)
                        || BIBLIOTECARIO.equalsIgnoreCase(rol);

        // =========================
        // SEGURIDAD API - PRÉSTAMOS
        // =========================
        // Solo Administrador y Bibliotecario pueden
        // consultar el listado general de préstamos.
        // No afecta a POST /api/prestamos.
        if ("GET".equalsIgnoreCase(metodo)
                && "/api/prestamos".equals(ruta)
                && !esPersonal) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN
            );

            return false;
        }

        // =========================
        // SEGURIDAD API - COLA DE ESPERA
        // =========================
        // El Lector solo puede consultar
        // sus propios registros.
        String prefijoCola =
                "/api/cola-espera/usuario/";

        if ("GET".equalsIgnoreCase(metodo)
                && ruta.startsWith(prefijoCola)) {

            String idSolicitado =
                    ruta.substring(prefijoCola.length());

            if (idSolicitado.matches("\\d+")) {

                String idSesion = String.valueOf(
                        session.getAttribute("usuarioId")
                );

                if ((esLector
                        && !idSesion.equals(idSolicitado))
                        || (!esLector && !esPersonal)) {

                    response.sendError(
                            HttpServletResponse.SC_FORBIDDEN
                    );

                    return false;
                }
            }
        }

        // =========================
        // PANEL Y MANTENIMIENTOS
        // DEL ADMINISTRADOR
        // =========================
        if (coincideConAlguna(
                ruta, RUTAS_ADMINISTRADOR)) {

            if (ADMINISTRADOR.equalsIgnoreCase(rol)) {

                // Al volver al panel Administrador,
                // se cierra la Vista como Bibliotecario.
                if (ruta.equals("/dashboard")) {
                    session.removeAttribute(
                            "vistaBibliotecarioDesdeAdmin"
                    );
                }

                return true;
            }

            redirigirSegunRol(rol, response);
            return false;
        }

        // =========================
        // GESTIÓN COMPARTIDA
        // ADMIN + BIBLIOTECARIO
        // =========================
        if (coincideConAlguna(
                ruta, RUTAS_COMPARTIDAS)) {

            if (esPersonal) {
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
            // Vista como Bibliotecario
            if (ADMINISTRADOR.equalsIgnoreCase(rol)) {

                Object vistaBibliotecario =
                        session.getAttribute(
                                "vistaBibliotecarioDesdeAdmin"
                        );

                if (Boolean.TRUE.equals(vistaBibliotecario)) {
                    return true;
                }

                // Si el Administrador escribió
                // /bibliotecario manualmente.
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

            if (esLector) {
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

    private boolean coincideConAlguna(
            String ruta,
            String[] bases) {

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

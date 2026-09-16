package com.biblioteca.app.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginDTOTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void configurarValidador() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void cerrarValidador() {
        factory.close();
    }

    private LoginDTO crearLogin(String usuario, String contrasena) {
        LoginDTO dto = new LoginDTO();
        dto.setUsuario(usuario);
        dto.setContrasena(contrasena);
        return dto;
    }

    @Test
    void debeAceptarDatosValidos() {
        LoginDTO dto = crearLogin("lector_prueba", "Biblioteca@2026");

        assertTrue(validator.validate(dto).isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n", " \t\n "})
    void debeRechazarUsuarioAusenteOEnBlanco(String usuario) {
        LoginDTO dto = crearLogin(usuario, "Biblioteca@2026");
        var errores = validator.validate(dto);

        assertEquals(1, errores.size());

        var error = errores.iterator().next();
        assertEquals("usuario", error.getPropertyPath().toString());
        assertEquals("Ingrese su usuario", error.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n", " \t\n "})
    void debeRechazarContrasenaAusenteOEnBlanco(String contrasena) {
        LoginDTO dto = crearLogin("lector_prueba", contrasena);
        var errores = validator.validate(dto);

        assertEquals(1, errores.size());

        var error = errores.iterator().next();
        assertEquals("contrasena", error.getPropertyPath().toString());
        assertEquals("Ingrese su contraseña", error.getMessage());
    }

    @Test
    void debeInformarAmbosCamposObligatorios() {
        LoginDTO dto = new LoginDTO();

        Map<String, String> errores = validator.validate(dto).stream()
                .collect(Collectors.toMap(
                        error -> error.getPropertyPath().toString(),
                        error -> error.getMessage()
                ));

        assertEquals(
                Map.of(
                        "usuario", "Ingrese su usuario",
                        "contrasena", "Ingrese su contraseña"
                ),
                errores
        );
    }

    @Test
    void noDebeExigirComplejidadDeRegistroParaIniciarSesion() {
        LoginDTO dto = crearLogin("lector_prueba", "abc");

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void debeConservarLaContrasenaSinRecortarEspacios() {
        LoginDTO dto = crearLogin("lector_prueba", " Clave@2026 ");

        validator.validate(dto);

        assertEquals(" Clave@2026 ", dto.getContrasena());
    }
}
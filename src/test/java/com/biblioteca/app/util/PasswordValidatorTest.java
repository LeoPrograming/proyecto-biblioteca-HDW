package com.biblioteca.app.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "Biblioteca@2026",
            "Abcdef1!",
            "Abcdefghijklmnopqr1!"
    })
    void debeAceptarContrasenasQueCumplenLasReglas(String contrasena) {
        assertTrue(PasswordValidator.esSegura(contrasena));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n"})
    void debeRechazarContrasenasAusentesOEnBlanco(String contrasena) {
        assertFalse(PasswordValidator.esSegura(contrasena));
    }

    @Test
    void debeAceptarElMinimoDeOchoCaracteres() {
        assertTrue(PasswordValidator.esSegura("Aa1!" + "b".repeat(4)));
    }

    @Test
    void debeAceptarElMaximoDeVeinteCaracteres() {
        assertTrue(PasswordValidator.esSegura("Aa1!" + "b".repeat(16)));
    }

    @Test
    void debeRechazarSieteCaracteres() {
        assertFalse(PasswordValidator.esSegura("Aa1!" + "b".repeat(3)));
    }

    @Test
    void debeRechazarVeintiunCaracteres() {
        assertFalse(PasswordValidator.esSegura("Aa1!" + "b".repeat(17)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678",
            "biblioteca",
            "biblioteca@2026",
            "BIBLIOTECA@2026",
            "Biblioteca@",
            "Biblioteca2026"
    })
    void debeRechazarCuandoFaltaUnTipoDeCaracter(String contrasena) {
        assertFalse(PasswordValidator.esSegura(contrasena));
    }

    @Test
    void debeRechazarSaltosDeLinea() {
        assertFalse(PasswordValidator.esSegura("Biblioteca@1\n"));
    }

    @Test
    void debeMantenerElMensajeDeRequisitos() {
        assertEquals(
                "La contraseña debe tener entre 8 y 20 caracteres, "
                        + "incluir mayúscula, minúscula, número y carácter especial.",
                PasswordValidator.mensajeRequisitos()
        );
    }

    @Test
    void debeConservarElTratamientoOriginalDelEspacioComoEspecial() {
        assertTrue(PasswordValidator.esSegura("Biblioteca1 "));
    }
}
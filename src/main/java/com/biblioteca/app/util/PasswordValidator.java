package com.biblioteca.app.util;

/*
 * VALIDADOR PARA CONTRASENAS SEGURAS
 */
public class PasswordValidator {

    /*
     * REGLAS:
     * - MINIMO 8 CARACTERES
     * - MAXIMO 20 CARACTERES
     * - AL MENOS UNA MAYUSCULA
     * - AL MENOS UNA MINUSCULA
     * - AL MENOS UN NUMERO
     * - AL MENOS UN CARACTER ESPECIAL
     */
    private static final String REGEX_PASSWORD =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,20}$";

    /*
     * VALIDA SI LA CONTRASENA CUMPLE LAS REGLAS
     */
    public static boolean esSegura(String contrasena) {

        if (contrasena == null || contrasena.isBlank()) {
            return false;
        }

        return contrasena.matches(REGEX_PASSWORD);
    }

    /*
     * MENSAJE UNICO PARA MOSTRAR AL USUARIO
     */
    public static String mensajeRequisitos() {
        return "La contraseña debe tener entre 8 y 20 caracteres, incluir mayúscula, minúscula, número y carácter especial.";
    }
}
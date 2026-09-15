package com.biblioteca.app;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class MysqlFailureScenariosIT {

    private static final int PROCESS_TIMEOUT_SECONDS = 15;

@Test
@Timeout(20)
void debeFallarSiFaltaUnaVariableObligatoria() throws Exception {
    ProcessResult result = ejecutarAplicacion(
            "--spring.datasource.username=usuario_prueba",
            "--spring.datasource.password=password_prueba"
    );

    assertNotEquals(0, result.exitCode());
    assertTrue(
            result.output().contains("DB_URL"),
            "El arranque debio fallar por ausencia de DB_URL"
    );
}

    @Test
    @Timeout(20)
    void debeFallarConCredencialesInvalidas() throws Exception {
        ProcessResult result = ejecutarAplicacion(
                "--spring.datasource.url=jdbc:mysql://127.0.0.1:3306/stm_biblioteca?connectTimeout=1000&socketTimeout=1000",
                "--spring.datasource.username=usuario_invalido_stm",
                "--spring.datasource.password=password_invalido_stm",
                "--spring.datasource.hikari.connection-timeout=1000",
                "--spring.datasource.hikari.validation-timeout=1000",
                "--spring.datasource.hikari.initialization-fail-timeout=1000"
        );

        assertNotEquals(0, result.exitCode());
        assertTrue(
                result.output().contains("Access denied"),
                "El arranque debio fallar por credenciales invalidas"
        );
    }

    @Test
    @Timeout(20)
    void debeFallarSiMysqlNoEstaDisponible() throws Exception {
        ProcessResult result = ejecutarAplicacion(
                "--spring.datasource.url=jdbc:mysql://127.0.0.1:1/stm_biblioteca?connectTimeout=1000&socketTimeout=1000",
                "--spring.datasource.username=usuario_prueba",
                "--spring.datasource.password=password_prueba",
                "--spring.datasource.hikari.connection-timeout=1000",
                "--spring.datasource.hikari.validation-timeout=1000",
                "--spring.datasource.hikari.initialization-fail-timeout=1000"
        );

        assertNotEquals(0, result.exitCode());
        assertTrue(
                result.output().contains("Communications link failure")
                        || result.output().contains("Connection refused"),
                "El arranque debio fallar porque MySQL no estaba disponible"
        );
    }

    private ProcessResult ejecutarAplicacion(String... argumentos) throws Exception {
        Path war = localizarWar();
        Path directorioTemporal = Files.createTempDirectory("stm-biblioteca-it-");
        Path log = Files.createTempFile("stm-biblioteca-it-", ".log");

        List<String> comando = new ArrayList<>();
        comando.add(localizarJava());
        comando.add("-jar");
        comando.add(war.toString());
        comando.add("--spring.main.web-application-type=none");
        comando.add("--spring.main.banner-mode=off");
        comando.addAll(List.of(argumentos));

        ProcessBuilder processBuilder = new ProcessBuilder(comando);
        processBuilder.directory(directorioTemporal.toFile());
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(log.toFile());

        Map<String, String> entorno = processBuilder.environment();
        entorno.remove("DB_URL");
        entorno.remove("DB_USERNAME");
        entorno.remove("DB_PASSWORD");

        long inicio = System.nanoTime();
        Process process = processBuilder.start();

        boolean finalizo = process.waitFor(PROCESS_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        if (!finalizo) {
            process.destroyForcibly();
            process.waitFor(5, TimeUnit.SECONDS);
            fail("La aplicacion excedio el tiempo limite de "
                    + PROCESS_TIMEOUT_SECONDS + " segundos");
        }

        Duration duracion = Duration.ofNanos(System.nanoTime() - inicio);
        String output = Files.readString(log, StandardCharsets.UTF_8);

        assertTrue(
                duracion.getSeconds() < PROCESS_TIMEOUT_SECONDS,
                "El escenario negativo excedio el tiempo de espera permitido"
        );

        return new ProcessResult(process.exitValue(), output);
    }

    private Path localizarWar() throws IOException {
        Path target = Path.of("target").toAbsolutePath();

        try (Stream<Path> archivos = Files.list(target)) {
            return archivos
                    .filter(path -> path.getFileName().toString().endsWith(".war"))
                    .filter(path -> !path.getFileName().toString().endsWith(".war.original"))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalStateException("No se encontro el WAR en target"));
        }
    }

    private String localizarJava() {
        boolean windows = System.getProperty("os.name")
                .toLowerCase()
                .contains("win");

        String ejecutable = windows ? "java.exe" : "java";

        return Path.of(
                System.getProperty("java.home"),
                "bin",
                ejecutable
        ).toString();
    }

    private record ProcessResult(int exitCode, String output) {
    }
}
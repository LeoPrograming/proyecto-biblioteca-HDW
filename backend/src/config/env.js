function getConfig(env = process.env) {
    if (!env.PORT) {
        throw new Error(
            "Configuración inválida: falta la variable obligatoria PORT."
        );
    }

    const port = Number(env.PORT);

    if (!Number.isInteger(port) || port < 1 || port > 65535) {
        throw new Error(
            "Configuración inválida: PORT debe ser un número entero entre 1 y 65535."
        );
    }

    return { port };
}

module.exports = { getConfig };
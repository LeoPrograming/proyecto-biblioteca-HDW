const { createApp } = require("./app");
const { getConfig } = require("./config/env");

let server;

try {
    const { port } = getConfig();
    const app = createApp();

    server = app.listen(port, () => {
        console.log(`TicketFlow backend ejecutándose en puerto ${port}`);
    });
} catch (error) {
    console.error(error.message);
    process.exit(1);
}

function shutdown(signal) {
    console.log(`\n${signal} recibido. Cerrando servidor...`);

    if (!server) {
        process.exit(0);
    }

    server.close(() => {
        console.log("Servidor cerrado correctamente.");
        process.exit(0);
    });
}

process.on("SIGINT", () => shutdown("SIGINT"));
process.on("SIGTERM", () => shutdown("SIGTERM"));
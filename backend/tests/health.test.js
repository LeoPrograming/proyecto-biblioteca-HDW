const test = require("node:test");
const assert = require("node:assert/strict");

const { createApp } = require("../src/app");
const { getConfig } = require("../src/config/env");

test("health devuelve HTTP 200 y el JSON esperado", async () => {
    const app = createApp();

    const server = app.listen(0);

    await new Promise((resolve) => {
        server.once("listening", resolve);
    });

    const address = server.address();

    const response = await fetch(
        `http://127.0.0.1:${address.port}/health`
    );

    assert.equal(response.status, 200);

    const body = await response.json();

    assert.deepEqual(body, {
        status: "UP",
        service: "ticketflow-backend"
    });

    await new Promise((resolve, reject) => {
        server.close((error) => {
            if (error) {
                reject(error);
                return;
            }

            resolve();
        });
    });
});

test("la configuración falla claramente cuando falta PORT", () => {
    assert.throws(
        () => getConfig({}),
        /falta la variable obligatoria PORT/
    );
});

test("rechaza un PORT inválido", () => {
    assert.throws(
        () => getConfig({ PORT: "abc" }),
        /PORT debe ser un número entero/
    );
});
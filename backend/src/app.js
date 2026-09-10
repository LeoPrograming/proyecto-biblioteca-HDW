const express = require("express");
const healthRoutes = require("./routes/health.routes");

function createApp() {
    const app = express();

    app.use(express.json());

    app.use(healthRoutes);

    return app;
}

module.exports = { createApp };
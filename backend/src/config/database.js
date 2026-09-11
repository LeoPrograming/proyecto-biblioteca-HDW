"use strict";

const { Sequelize } = require("sequelize");
const configurations = require("../../config/config");

let sequelize = null;

function getDatabaseConfig() {
    const environment = process.env.NODE_ENV || "development";
    const config = configurations[environment];

    if (!config) {
        throw new Error(
            `No existe configuración de base de datos para el entorno ${environment}.`
        );
    }

    const missingVariables = [];

    if (!config.host) {
        missingVariables.push("DB_HOST");
    }

    if (!config.database) {
        missingVariables.push("DB_NAME");
    }

    if (!config.username) {
        missingVariables.push("DB_USER");
    }

    if (missingVariables.length > 0) {
        throw new Error(
            `Faltan variables de base de datos: ${missingVariables.join(", ")}.`
        );
    }

    return config;
}

function getSequelize() {
    if (!sequelize) {
        const config = getDatabaseConfig();

        sequelize = new Sequelize(
            config.database,
            config.username,
            config.password || "",
            {
                host: config.host,
                port: config.port,
                dialect: config.dialect,
                timezone: config.timezone,
                logging: config.logging,
            }
        );
    }

    return sequelize;
}

async function checkDatabaseConnection() {
    const connection = getSequelize();
    await connection.authenticate();
}

module.exports = {
    getSequelize,
    checkDatabaseConnection,
};
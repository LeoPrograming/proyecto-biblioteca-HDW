"use strict";

const express = require("express");
const {
    checkDatabaseConnection,
} = require("../config/database");

const router = express.Router();

router.get("/health", (req, res) => {
    res.status(200).json({
        status: "UP",
        service: "ticketflow-backend",
    });
});

router.get("/health/live", (req, res) => {
    res.status(200).json({
        status: "UP",
        service: "ticketflow-backend",
    });
});

router.get("/health/ready", async (req, res) => {
    try {
        await checkDatabaseConnection();

        return res.status(200).json({
            status: "READY",
            service: "ticketflow-backend",
            database: "UP",
        });
    } catch (error) {
        return res.status(503).json({
            status: "NOT_READY",
            service: "ticketflow-backend",
            database: "DOWN",
        });
    }
});

module.exports = router;
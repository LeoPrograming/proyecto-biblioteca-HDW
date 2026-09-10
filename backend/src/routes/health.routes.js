const express = require("express");

const router = express.Router();

router.get("/health", (req, res) => {
    res.status(200).json({
        status: "UP",
        service: "ticketflow-backend"
    });
});

module.exports = router;
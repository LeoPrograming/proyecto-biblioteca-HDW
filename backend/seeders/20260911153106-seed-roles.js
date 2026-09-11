"use strict";

module.exports = {
  async up(queryInterface) {
    const now = new Date();

    await queryInterface.bulkInsert("roles", [
      {
        code: "SOLICITANTE",
        name: "Solicitante",
        created_at: now,
        updated_at: now,
      },
      {
        code: "AGENTE",
        name: "Agente",
        created_at: now,
        updated_at: now,
      },
      {
        code: "ADMINISTRADOR",
        name: "Administrador",
        created_at: now,
        updated_at: now,
      },
    ]);
  },

  async down(queryInterface, Sequelize) {
    await queryInterface.bulkDelete("roles", {
      code: {
        [Sequelize.Op.in]: [
          "SOLICITANTE",
          "AGENTE",
          "ADMINISTRADOR",
        ],
      },
    });
  },
};
"use strict";

module.exports = {
  async up(queryInterface) {
    const now = new Date();

    await queryInterface.bulkInsert("priorities", [
      {
        code: "BAJA",
        name: "Baja",
        created_at: now,
        updated_at: now,
      },
      {
        code: "MEDIA",
        name: "Media",
        created_at: now,
        updated_at: now,
      },
      {
        code: "ALTA",
        name: "Alta",
        created_at: now,
        updated_at: now,
      },
      {
        code: "CRITICA",
        name: "Crítica",
        created_at: now,
        updated_at: now,
      },
    ]);
  },

  async down(queryInterface, Sequelize) {
    await queryInterface.bulkDelete("priorities", {
      code: {
        [Sequelize.Op.in]: [
          "BAJA",
          "MEDIA",
          "ALTA",
          "CRITICA",
        ],
      },
    });
  },
};
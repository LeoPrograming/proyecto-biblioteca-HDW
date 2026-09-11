"use strict";

module.exports = {
  async up(queryInterface) {
    const [priorities] = await queryInterface.sequelize.query(`
      SELECT id
      FROM priorities
      WHERE code = 'MEDIA'
      LIMIT 1
    `);

    if (priorities.length === 0) {
      throw new Error(
          "No existe la prioridad MEDIA. Ejecute primero el seed de prioridades."
      );
    }

    const now = new Date();

    await queryInterface.bulkInsert("categories", [
      {
        name: "General",
        default_priority_id: priorities[0].id,
        is_active: true,
        created_at: now,
        updated_at: now,
      },
    ]);
  },

  async down(queryInterface) {
    await queryInterface.bulkDelete("categories", {
      name: "General",
    });
  },
};
"use strict";

module.exports = {
  async up(queryInterface) {
    const [priorities] = await queryInterface.sequelize.query(`
      SELECT id, code
      FROM priorities
      WHERE code IN ('BAJA', 'MEDIA', 'ALTA', 'CRITICA')
    `);

    const priorityMap = {};

    for (const priority of priorities) {
      priorityMap[priority.code] = priority.id;
    }

    const requiredPriorities = [
      "BAJA",
      "MEDIA",
      "ALTA",
      "CRITICA",
    ];

    for (const code of requiredPriorities) {
      if (!priorityMap[code]) {
        throw new Error(
            `No existe la prioridad ${code}. Ejecute primero el seed de prioridades.`
        );
      }
    }

    const now = new Date();

    await queryInterface.bulkInsert("sla_policies", [
      {
        priority_id: priorityMap.BAJA,
        version: 1,
        response_minutes: 480,
        resolution_minutes: 4320,
        warning_percent: 80,
        is_active: true,
        created_at: now,
        updated_at: now,
      },
      {
        priority_id: priorityMap.MEDIA,
        version: 1,
        response_minutes: 240,
        resolution_minutes: 2880,
        warning_percent: 80,
        is_active: true,
        created_at: now,
        updated_at: now,
      },
      {
        priority_id: priorityMap.ALTA,
        version: 1,
        response_minutes: 120,
        resolution_minutes: 1440,
        warning_percent: 75,
        is_active: true,
        created_at: now,
        updated_at: now,
      },
      {
        priority_id: priorityMap.CRITICA,
        version: 1,
        response_minutes: 60,
        resolution_minutes: 480,
        warning_percent: 70,
        is_active: true,
        created_at: now,
        updated_at: now,
      },
    ]);
  },

  async down(queryInterface) {
    await queryInterface.bulkDelete("sla_policies", {
      version: 1,
    });
  },
};
"use strict";

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable("areas", {
      id: {
        type: Sequelize.BIGINT.UNSIGNED,
        autoIncrement: true,
        primaryKey: true,
        allowNull: false,
      },

      company_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: false,
        references: {
          model: "companies",
          key: "id",
        },
        onUpdate: "CASCADE",
        onDelete: "RESTRICT",
      },

      name: {
        type: Sequelize.STRING(120),
        allowNull: false,
      },

      is_active: {
        type: Sequelize.BOOLEAN,
        allowNull: false,
        defaultValue: true,
      },

      created_at: {
        type: Sequelize.DATE,
        allowNull: false,
        defaultValue: Sequelize.literal("CURRENT_TIMESTAMP"),
      },

      updated_at: {
        type: Sequelize.DATE,
        allowNull: false,
        defaultValue: Sequelize.literal(
            "CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
        ),
      },
    });

    // Una empresa no puede tener dos áreas con el mismo nombre.
    await queryInterface.addConstraint("areas", {
      fields: ["company_id", "name"],
      type: "unique",
      name: "uq_areas_company_name",
    });

    // Permite validar posteriormente la relación company_id + area_id
    // desde users, qr_contexts y tickets.
    await queryInterface.addConstraint("areas", {
      fields: ["company_id", "id"],
      type: "unique",
      name: "uq_areas_company_id",
    });

    // Índice para consultas de áreas por empresa.
    await queryInterface.addIndex("areas", ["company_id"], {
      name: "idx_areas_company",
    });
  },

  async down(queryInterface) {
    await queryInterface.dropTable("areas");
  },
};
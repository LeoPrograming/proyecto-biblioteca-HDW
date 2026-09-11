"use strict";

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable("sla_policies", {
      id: {
        type: Sequelize.BIGINT.UNSIGNED,
        autoIncrement: true,
        primaryKey: true,
        allowNull: false,
      },

      priority_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: false,
      },

      version: {
        type: Sequelize.INTEGER.UNSIGNED,
        allowNull: false,
      },

      response_minutes: {
        type: Sequelize.INTEGER.UNSIGNED,
        allowNull: false,
      },

      resolution_minutes: {
        type: Sequelize.INTEGER.UNSIGNED,
        allowNull: false,
      },

      warning_percent: {
        type: Sequelize.TINYINT.UNSIGNED,
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

    await queryInterface.addConstraint("sla_policies", {
      fields: ["priority_id"],
      type: "foreign key",
      name: "fk_sla_policies_priority",
      references: {
        table: "priorities",
        field: "id",
      },
      onUpdate: "RESTRICT",
      onDelete: "RESTRICT",
    });

    await queryInterface.addConstraint("sla_policies", {
      fields: ["priority_id", "version"],
      type: "unique",
      name: "uq_sla_priority_version",
    });

    await queryInterface.addConstraint("sla_policies", {
      fields: ["priority_id", "id"],
      type: "unique",
      name: "uq_sla_priority_id",
    });

    await queryInterface.addConstraint("sla_policies", {
      fields: ["version"],
      type: "check",
      name: "chk_sla_version_positive",
      where: Sequelize.literal("version > 0"),
    });

    await queryInterface.addConstraint("sla_policies", {
      fields: ["response_minutes"],
      type: "check",
      name: "chk_sla_response_positive",
      where: Sequelize.literal("response_minutes > 0"),
    });

    await queryInterface.addConstraint("sla_policies", {
      fields: ["resolution_minutes"],
      type: "check",
      name: "chk_sla_resolution_positive",
      where: Sequelize.literal("resolution_minutes > 0"),
    });

    await queryInterface.addConstraint("sla_policies", {
      fields: ["resolution_minutes", "response_minutes"],
      type: "check",
      name: "chk_sla_resolution_gte_response",
      where: Sequelize.literal(
          "resolution_minutes >= response_minutes"
      ),
    });

    await queryInterface.addConstraint("sla_policies", {
      fields: ["warning_percent"],
      type: "check",
      name: "chk_sla_warning_percent",
      where: Sequelize.literal(
          "warning_percent BETWEEN 1 AND 99"
      ),
    });

    await queryInterface.addIndex(
        "sla_policies",
        ["priority_id"],
        {
          name: "idx_sla_priority",
        }
    );

    await queryInterface.addIndex(
        "sla_policies",
        ["priority_id", "is_active"],
        {
          name: "idx_sla_priority_active",
        }
    );
  },

  async down(queryInterface) {
    await queryInterface.dropTable("sla_policies");
  },
};
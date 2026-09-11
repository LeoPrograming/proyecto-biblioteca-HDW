"use strict";

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable("ticket_actions", {
      id: {
        type: Sequelize.BIGINT.UNSIGNED,
        autoIncrement: true,
        primaryKey: true,
        allowNull: false,
      },

      ticket_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: false,
        references: {
          model: "tickets",
          key: "id",
        },
        onDelete: "RESTRICT",
      },

      author_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: false,
        references: {
          model: "users",
          key: "id",
        },
        onDelete: "RESTRICT",
      },

      action_type: {
        type: Sequelize.STRING(60),
        allowNull: false,
      },

      description: {
        type: Sequelize.TEXT,
        allowNull: false,
      },

      started_at: {
        type: Sequelize.DATE,
        allowNull: false,
        defaultValue: Sequelize.literal("CURRENT_TIMESTAMP"),
      },

      ended_at: {
        type: Sequelize.DATE,
        allowNull: true,
      },

      created_at: {
        type: Sequelize.DATE,
        allowNull: false,
        defaultValue: Sequelize.literal("CURRENT_TIMESTAMP"),
      },
    });

    await queryInterface.addConstraint("ticket_actions", {
      fields: ["action_type"],
      type: "check",
      name: "chk_ticket_actions_type_not_empty",
      where: Sequelize.literal(
          "CHAR_LENGTH(TRIM(action_type)) > 0"
      ),
    });

    await queryInterface.addConstraint("ticket_actions", {
      fields: ["description"],
      type: "check",
      name: "chk_ticket_actions_description_not_empty",
      where: Sequelize.literal(
          "CHAR_LENGTH(TRIM(description)) > 0"
      ),
    });

    await queryInterface.addConstraint("ticket_actions", {
      fields: ["started_at", "ended_at"],
      type: "check",
      name: "chk_ticket_actions_dates",
      where: Sequelize.literal(
          "ended_at IS NULL OR ended_at >= started_at"
      ),
    });

    await queryInterface.addIndex(
        "ticket_actions",
        ["ticket_id", "started_at"],
        {
          name: "idx_ticket_actions_ticket_started",
        }
    );

    await queryInterface.addIndex(
        "ticket_actions",
        ["author_id"],
        {
          name: "idx_ticket_actions_author",
        }
    );
  },

  async down(queryInterface) {
    await queryInterface.dropTable("ticket_actions");
  },
};
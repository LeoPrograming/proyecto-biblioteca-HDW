"use strict";

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable("ticket_history", {
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

      event_type: {
        type: Sequelize.STRING(100),
        allowNull: false,
      },

      actor_type: {
        type: Sequelize.ENUM("HUMANO", "SISTEMA"),
        allowNull: false,
      },

      user_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: true,
        references: {
          model: "users",
          key: "id",
        },
        onDelete: "RESTRICT",
      },

      before_values: {
        type: Sequelize.JSON,
        allowNull: true,
      },

      after_values: {
        type: Sequelize.JSON,
        allowNull: true,
      },

      occurred_at: {
        type: Sequelize.DATE,
        allowNull: false,
        defaultValue: Sequelize.literal("CURRENT_TIMESTAMP"),
      },
    });

    await queryInterface.addConstraint("ticket_history", {
      fields: ["actor_type", "user_id"],
      type: "check",
      name: "chk_ticket_history_actor",
      where: Sequelize.literal(`
    (
      actor_type = 'HUMANO'
      AND user_id IS NOT NULL
    )
    OR
    (
      actor_type = 'SISTEMA'
      AND user_id IS NULL
    )
  `),
    });

    await queryInterface.addConstraint("ticket_history", {
      fields: ["event_type"],
      type: "check",
      name: "chk_ticket_history_event_not_empty",
      where: Sequelize.literal(
          "CHAR_LENGTH(TRIM(event_type)) > 0"
      ),
    });

    await queryInterface.addIndex(
        "ticket_history",
        ["ticket_id", "occurred_at"],
        {
          name: "idx_ticket_history_ticket_occurred",
        }
    );

    await queryInterface.addIndex(
        "ticket_history",
        ["event_type"],
        {
          name: "idx_ticket_history_event_type",
        }
    );

    await queryInterface.addIndex(
        "ticket_history",
        ["user_id"],
        {
          name: "idx_ticket_history_user",
        }
    );
  },

  async down(queryInterface) {
    await queryInterface.dropTable("ticket_history");
  },
};
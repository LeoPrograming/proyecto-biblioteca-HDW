"use strict";

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable("notification_deliveries", {
      id: {
        type: Sequelize.BIGINT.UNSIGNED,
        autoIncrement: true,
        primaryKey: true,
        allowNull: false,
      },

      notification_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: false,
        references: {
          model: "notifications",
          key: "id",
        },
        onUpdate: "CASCADE",
        onDelete: "RESTRICT",
      },

      channel: {
        type: Sequelize.ENUM("EMAIL", "WHATSAPP"),
        allowNull: false,
      },

      /*
       * El Maestro no define un conjunto canónico de estados
       * de entrega, por eso no se fuerza un ENUM prematuramente.
       */
      status: {
        type: Sequelize.STRING(30),
        allowNull: false,
        defaultValue: "PENDIENTE",
      },

      attempts: {
        type: Sequelize.TINYINT.UNSIGNED,
        allowNull: false,
        defaultValue: 0,
      },

      next_attempt_at: {
        type: Sequelize.DATE,
        allowNull: true,
      },

      sent_at: {
        type: Sequelize.DATE,
        allowNull: true,
      },

      last_error: {
        type: Sequelize.STRING(1000),
        allowNull: true,
      },

      lock_until: {
        type: Sequelize.DATE,
        allowNull: true,
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

    await queryInterface.addConstraint("notification_deliveries", {
      fields: ["notification_id", "channel"],
      type: "unique",
      name: "uq_notification_deliveries_notification_channel",
    });

    await queryInterface.addConstraint("notification_deliveries", {
      fields: ["attempts"],
      type: "check",
      name: "chk_notification_deliveries_attempts",
      where: Sequelize.literal(
          "attempts BETWEEN 0 AND 3"
      ),
    });

    await queryInterface.addConstraint("notification_deliveries", {
      fields: ["status"],
      type: "check",
      name: "chk_notification_deliveries_status_not_empty",
      where: Sequelize.literal(
          "CHAR_LENGTH(TRIM(status)) > 0"
      ),
    });

    await queryInterface.addIndex(
        "notification_deliveries",
        ["next_attempt_at"],
        {
          name: "idx_notification_deliveries_next_attempt",
        }
    );

    await queryInterface.addIndex(
        "notification_deliveries",
        ["status", "next_attempt_at", "lock_until"],
        {
          name: "idx_notification_deliveries_processing",
        }
    );
  },

  async down(queryInterface) {
    await queryInterface.dropTable("notification_deliveries");
  },
};
"use strict";

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable("notifications", {
      id: {
        type: Sequelize.BIGINT.UNSIGNED,
        autoIncrement: true,
        primaryKey: true,
        allowNull: false,
      },

      event_key: {
        type: Sequelize.STRING(180),
        allowNull: false,
      },

      recipient_user_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: false,
        references: {
          model: "users",
          key: "id",
        },
        onUpdate: "CASCADE",
        onDelete: "RESTRICT",
      },

      ticket_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: false,
        references: {
          model: "tickets",
          key: "id",
        },
        onUpdate: "CASCADE",
        onDelete: "RESTRICT",
      },

      title: {
        type: Sequelize.STRING(180),
        allowNull: false,
      },

      message: {
        type: Sequelize.TEXT,
        allowNull: false,
      },

      read_at: {
        type: Sequelize.DATE,
        allowNull: true,
      },

      created_at: {
        type: Sequelize.DATE,
        allowNull: false,
        defaultValue: Sequelize.literal("CURRENT_TIMESTAMP"),
      },
    });

    await queryInterface.addConstraint("notifications", {
      fields: ["event_key", "recipient_user_id"],
      type: "unique",
      name: "uq_notifications_event_recipient",
    });

    await queryInterface.addConstraint("notifications", {
      fields: ["event_key"],
      type: "check",
      name: "chk_notifications_event_key_not_empty",
      where: Sequelize.literal(
          "CHAR_LENGTH(TRIM(event_key)) > 0"
      ),
    });

    await queryInterface.addConstraint("notifications", {
      fields: ["title"],
      type: "check",
      name: "chk_notifications_title_not_empty",
      where: Sequelize.literal(
          "CHAR_LENGTH(TRIM(title)) > 0"
      ),
    });

    await queryInterface.addConstraint("notifications", {
      fields: ["message"],
      type: "check",
      name: "chk_notifications_message_not_empty",
      where: Sequelize.literal(
          "CHAR_LENGTH(TRIM(message)) > 0"
      ),
    });

    await queryInterface.addIndex(
        "notifications",
        ["recipient_user_id", "read_at", "created_at"],
        {
          name: "idx_notifications_recipient_read_created",
        }
    );

    await queryInterface.addIndex(
        "notifications",
        ["ticket_id"],
        {
          name: "idx_notifications_ticket",
        }
    );
  },

  async down(queryInterface) {
    await queryInterface.dropTable("notifications");
  },
};
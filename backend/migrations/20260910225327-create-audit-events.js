"use strict";

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable("audit_events", {
      id: {
        type: Sequelize.BIGINT.UNSIGNED,
        autoIncrement: true,
        primaryKey: true,
        allowNull: false,
      },

      entity_type: {
        type: Sequelize.STRING(80),
        allowNull: false,
      },

      entity_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: false,
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

    await queryInterface.addConstraint("audit_events", {
      fields: ["actor_type", "user_id"],
      type: "check",
      name: "chk_audit_events_actor",
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

    await queryInterface.addConstraint("audit_events", {
      fields: ["entity_type"],
      type: "check",
      name: "chk_audit_events_entity_not_empty",
      where: Sequelize.literal(
          "CHAR_LENGTH(TRIM(entity_type)) > 0"
      ),
    });

    await queryInterface.addConstraint("audit_events", {
      fields: ["event_type"],
      type: "check",
      name: "chk_audit_events_event_not_empty",
      where: Sequelize.literal(
          "CHAR_LENGTH(TRIM(event_type)) > 0"
      ),
    });

    await queryInterface.addIndex(
        "audit_events",
        ["entity_type", "entity_id"],
        {
          name: "idx_audit_events_entity",
        }
    );

    await queryInterface.addIndex(
        "audit_events",
        ["occurred_at"],
        {
          name: "idx_audit_events_occurred",
        }
    );

    await queryInterface.addIndex(
        "audit_events",
        ["user_id"],
        {
          name: "idx_audit_events_user",
        }
    );
  },

  async down(queryInterface) {
    await queryInterface.dropTable("audit_events");
  },
};
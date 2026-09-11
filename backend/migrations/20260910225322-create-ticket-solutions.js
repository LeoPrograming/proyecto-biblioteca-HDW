"use strict";

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable("ticket_solutions", {
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

      proposed_by_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: false,
        references: {
          model: "users",
          key: "id",
        },
        onDelete: "RESTRICT",
      },

      body: {
        type: Sequelize.TEXT,
        allowNull: false,
      },

      proposed_at: {
        type: Sequelize.DATE,
        allowNull: false,
        defaultValue: Sequelize.literal("CURRENT_TIMESTAMP"),
      },

      resolution_due_at_snapshot: {
        type: Sequelize.DATE,
        allowNull: false,
      },

      decision: {
        type: Sequelize.ENUM(
            "PENDIENTE",
            "ACEPTADA",
            "RECHAZADA",
            "RETIRADA"
        ),
        allowNull: false,
        defaultValue: "PENDIENTE",
      },

      decided_by_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: true,
        references: {
          model: "users",
          key: "id",
        },
      },

      decided_at: {
        type: Sequelize.DATE,
        allowNull: true,
      },

      reason: {
        type: Sequelize.STRING(1000),
        allowNull: true,
      },

      created_at: {
        type: Sequelize.DATE,
        allowNull: false,
        defaultValue: Sequelize.literal("CURRENT_TIMESTAMP"),
      },
    });

    await queryInterface.addConstraint("ticket_solutions", {
      fields: ["body"],
      type: "check",
      name: "chk_ticket_solutions_body_length",
      where: Sequelize.literal(
          "CHAR_LENGTH(body) BETWEEN 10 AND 10000"
      ),
    });

    await queryInterface.addConstraint("ticket_solutions", {
      fields: [
        "decision",
        "decided_by_id",
        "decided_at",
        "reason"
      ],
      type: "check",
      name: "chk_ticket_solutions_decision_data",
      where: Sequelize.literal(`
    (
      decision = 'PENDIENTE'
      AND decided_by_id IS NULL
      AND decided_at IS NULL
      AND reason IS NULL
    )
    OR
    (
      decision <> 'PENDIENTE'
      AND decided_by_id IS NOT NULL
      AND decided_at IS NOT NULL
    )
  `),
    });

    await queryInterface.addConstraint("ticket_solutions", {
      fields: ["decision", "reason"],
      type: "check",
      name: "chk_ticket_solutions_rejection_reason",
      where: Sequelize.literal(`
    decision <> 'RECHAZADA'
    OR (
      reason IS NOT NULL
      AND CHAR_LENGTH(reason) BETWEEN 10 AND 1000
    )
  `),
    });

    /*
     * Garantiza en MySQL que un ticket tenga como máximo
     * una propuesta con decision PENDIENTE.
     */
    await queryInterface.sequelize.query(`
      ALTER TABLE ticket_solutions
      ADD COLUMN pending_ticket_id BIGINT UNSIGNED
      GENERATED ALWAYS AS (
        CASE
          WHEN decision = 'PENDIENTE' THEN ticket_id
          ELSE NULL
        END
      ) STORED,
      ADD CONSTRAINT uq_ticket_solutions_one_pending
      UNIQUE (pending_ticket_id);
    `);

    await queryInterface.addIndex(
        "ticket_solutions",
        ["ticket_id", "proposed_at"],
        {
          name: "idx_ticket_solutions_ticket_proposed",
        }
    );

    await queryInterface.addIndex(
        "ticket_solutions",
        ["proposed_by_id"],
        {
          name: "idx_ticket_solutions_proposer",
        }
    );
  },

  async down(queryInterface) {
    await queryInterface.dropTable("ticket_solutions");
  },
};
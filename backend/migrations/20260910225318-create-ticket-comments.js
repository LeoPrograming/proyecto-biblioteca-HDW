"use strict";

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable("ticket_comments", {
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
        onUpdate: "CASCADE",
        onDelete: "RESTRICT",
      },

      author_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: false,
        references: {
          model: "users",
          key: "id",
        },
        onUpdate: "CASCADE",
        onDelete: "RESTRICT",
      },

      body: {
        type: Sequelize.TEXT,
        allowNull: false,
      },

      visibility: {
        type: Sequelize.ENUM("PUBLICO", "INTERNO"),
        allowNull: false,
      },

      created_at: {
        type: Sequelize.DATE,
        allowNull: false,
        defaultValue: Sequelize.literal("CURRENT_TIMESTAMP"),
      },
    });

    await queryInterface.addConstraint("ticket_comments", {
      fields: ["ticket_id", "id"],
      type: "unique",
      name: "uq_ticket_comments_ticket_id",
    });

    await queryInterface.addConstraint("ticket_comments", {
      fields: ["body"],
      type: "check",
      name: "chk_ticket_comments_body_length",
      where: Sequelize.literal(
          "CHAR_LENGTH(body) BETWEEN 1 AND 5000"
      ),
    });

    await queryInterface.addIndex(
        "ticket_comments",
        ["ticket_id", "created_at"],
        {
          name: "idx_ticket_comments_ticket_created",
        }
    );

    await queryInterface.addIndex(
        "ticket_comments",
        ["author_id"],
        {
          name: "idx_ticket_comments_author",
        }
    );

    await queryInterface.addIndex(
        "ticket_comments",
        ["ticket_id", "visibility"],
        {
          name: "idx_ticket_comments_ticket_visibility",
        }
    );
  },

  async down(queryInterface) {
    await queryInterface.dropTable("ticket_comments");
  },
};
"use strict";

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable("ticket_attachments", {
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

      comment_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: true,
      },

      uploaded_by_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: false,
        references: {
          model: "users",
          key: "id",
        },
        onUpdate: "CASCADE",
        onDelete: "RESTRICT",
      },

      original_name: {
        type: Sequelize.STRING(255),
        allowNull: false,
      },

      storage_key: {
        type: Sequelize.STRING(500),
        allowNull: false,
        unique: true,
      },

      mime_type: {
        type: Sequelize.STRING(100),
        allowNull: false,
      },

      size_bytes: {
        type: Sequelize.BIGINT.UNSIGNED,
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

    /*
     * Si existe comment_id, fuerza que ese comentario
     * pertenezca al mismo ticket.
     */
    await queryInterface.addConstraint("ticket_attachments", {
      fields: ["ticket_id", "comment_id"],
      type: "foreign key",
      name: "fk_ticket_attachments_comment_same_ticket",
      references: {
        table: "ticket_comments",
        fields: ["ticket_id", "id"],
      },
      onUpdate: "CASCADE",
      onDelete: "RESTRICT",
    });

    await queryInterface.addConstraint("ticket_attachments", {
      fields: ["size_bytes"],
      type: "check",
      name: "chk_ticket_attachments_size_positive",
      where: Sequelize.literal(
          "size_bytes > 0"
      ),
    });

    await queryInterface.addIndex(
        "ticket_attachments",
        ["ticket_id", "created_at"],
        {
          name: "idx_ticket_attachments_ticket_created",
        }
    );

    await queryInterface.addIndex(
        "ticket_attachments",
        ["comment_id"],
        {
          name: "idx_ticket_attachments_comment",
        }
    );

    await queryInterface.addIndex(
        "ticket_attachments",
        ["uploaded_by_id"],
        {
          name: "idx_ticket_attachments_uploader",
        }
    );
  },

  async down(queryInterface) {
    await queryInterface.dropTable("ticket_attachments");
  },
};
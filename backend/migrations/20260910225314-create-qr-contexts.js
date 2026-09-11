"use strict";

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable("qr_contexts", {
      id: {
        type: Sequelize.BIGINT.UNSIGNED,
        autoIncrement: true,
        primaryKey: true,
        allowNull: false,
      },

      token_hash: {
        type: Sequelize.STRING(255),
        allowNull: false,
        unique: true,
      },

      company_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: false,
      },

      area_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: false,
      },

      location: {
        type: Sequelize.STRING(180),
        allowNull: true,
      },

      is_active: {
        type: Sequelize.BOOLEAN,
        allowNull: false,
        defaultValue: true,
      },

      expires_at: {
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

    await queryInterface.addConstraint("qr_contexts", {
      fields: ["company_id", "area_id"],
      type: "foreign key",
      name: "fk_qr_contexts_company_area",
      references: {
        table: "areas",
        fields: ["company_id", "id"],
      },
      onUpdate: "CASCADE",
      onDelete: "RESTRICT",
    });

    await queryInterface.addIndex(
        "qr_contexts",
        ["company_id", "area_id"],
        {
          name: "idx_qr_contexts_company_area",
        }
    );

    await queryInterface.addIndex(
        "qr_contexts",
        ["is_active", "expires_at"],
        {
          name: "idx_qr_contexts_active_expiration",
        }
    );
    await queryInterface.addConstraint("qr_contexts", {
      fields: ["company_id", "area_id", "id"],
      type: "unique",
      name: "uq_qr_context_company_area_id",
    });
  },

  async down(queryInterface) {
    await queryInterface.dropTable("qr_contexts");
  },
};
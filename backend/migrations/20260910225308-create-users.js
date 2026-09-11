"use strict";

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable("users", {
      id: {
        type: Sequelize.BIGINT.UNSIGNED,
        autoIncrement: true,
        primaryKey: true,
        allowNull: false,
      },

      role_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: false,
        references: {
          model: "roles",
          key: "id",
        },
        onUpdate: "CASCADE",
        onDelete: "RESTRICT",
      },

      company_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: true,
      },

      area_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: true,
      },

      first_name: {
        type: Sequelize.STRING(100),
        allowNull: false,
      },

      last_name: {
        type: Sequelize.STRING(120),
        allowNull: false,
      },

      email: {
        type: Sequelize.STRING(150),
        allowNull: false,
        unique: true,
      },

      phone_e164: {
        type: Sequelize.STRING(20),
        allowNull: true,
        unique: true,
      },

      password_hash: {
        type: Sequelize.STRING(255),
        allowNull: false,
      },

      is_active: {
        type: Sequelize.BOOLEAN,
        allowNull: false,
        defaultValue: true,
      },

      token_version: {
        type: Sequelize.INTEGER.UNSIGNED,
        allowNull: false,
        defaultValue: 0,
      },

      must_change_password: {
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

    await queryInterface.addConstraint("users", {
      fields: ["company_id", "area_id"],
      type: "foreign key",
      name: "fk_users_company_area",
      references: {
        table: "areas",
        fields: ["company_id", "id"],
      },
      onUpdate: "CASCADE",
      onDelete: "RESTRICT",
    });

    await queryInterface.addConstraint("users", {
      fields: ["company_id", "area_id"],
      type: "check",
      name: "chk_users_company_area_pair",
      where: Sequelize.literal(
          "(company_id IS NULL AND area_id IS NULL) OR " +
          "(company_id IS NOT NULL AND area_id IS NOT NULL)"
      ),
    });

    await queryInterface.addConstraint("users", {
      fields: ["token_version"],
      type: "check",
      name: "chk_users_token_version",
      where: {
        token_version: {
          [Sequelize.Op.gte]: 0,
        },
      },
    });

    await queryInterface.addIndex("users", ["role_id"], {
      name: "idx_users_role",
    });

    await queryInterface.addIndex("users", ["company_id", "area_id"], {
      name: "idx_users_company_area",
    });

    await queryInterface.addIndex("users", ["is_active"], {
      name: "idx_users_active",
    });
  },

  async down(queryInterface) {
    await queryInterface.dropTable("users");
  },
};
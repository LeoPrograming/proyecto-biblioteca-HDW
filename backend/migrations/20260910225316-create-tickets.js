"use strict";

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable("tickets", {
      id: {
        type: Sequelize.BIGINT.UNSIGNED,
        autoIncrement: true,
        primaryKey: true,
        allowNull: false,
      },

      code: {
        type: Sequelize.STRING(20),
        allowNull: false,
        unique: true,
      },

      title: {
        type: Sequelize.STRING(180),
        allowNull: false,
      },

      description: {
        type: Sequelize.TEXT,
        allowNull: false,
      },

      ticket_type: {
        type: Sequelize.ENUM("INCIDENTE", "SOLICITUD"),
        allowNull: false,
      },

      requester_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: false,
        references: {
          model: "users",
          key: "id",
        },
        onUpdate: "CASCADE",
        onDelete: "RESTRICT",
      },

      created_by_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: false,
        references: {
          model: "users",
          key: "id",
        },
        onUpdate: "CASCADE",
        onDelete: "RESTRICT",
      },

      assigned_agent_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: true,
        references: {
          model: "users",
          key: "id",
        },
        onUpdate: "CASCADE",
        onDelete: "RESTRICT",
      },

      company_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: false,
      },

      area_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: false,
      },

      company_name_snapshot: {
        type: Sequelize.STRING(150),
        allowNull: false,
      },

      area_name_snapshot: {
        type: Sequelize.STRING(120),
        allowNull: false,
      },

      qr_context_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: true,
      },

      category_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: false,
        references: {
          model: "categories",
          key: "id",
        },
        onUpdate: "CASCADE",
        onDelete: "RESTRICT",
      },

      priority_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: false,
        references: {
          model: "priorities",
          key: "id",
        },
        onUpdate: "CASCADE",
        onDelete: "RESTRICT",
      },

      status: {
        type: Sequelize.ENUM(
            "NUEVO",
            "PENDIENTE",
            "SOLUCIONADO",
            "CERRADO"
        ),
        allowNull: false,
        defaultValue: "NUEVO",
      },

      attention_type: {
        type: Sequelize.ENUM(
            "POR_DEFINIR",
            "REMOTA",
            "PRESENCIAL"
        ),
        allowNull: false,
        defaultValue: "POR_DEFINIR",
      },

      creation_channel: {
        type: Sequelize.ENUM("PORTAL", "AGENTE"),
        allowNull: false,
      },

      location_snapshot: {
        type: Sequelize.STRING(255),
        allowNull: false,
      },

      sla_policy_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: false,
      },

      response_minutes_snapshot: {
        type: Sequelize.INTEGER.UNSIGNED,
        allowNull: false,
      },

      resolution_minutes_snapshot: {
        type: Sequelize.INTEGER.UNSIGNED,
        allowNull: false,
      },

      warning_percent_snapshot: {
        type: Sequelize.TINYINT.UNSIGNED,
        allowNull: false,
      },

      first_response_due_at: {
        type: Sequelize.DATE,
        allowNull: false,
      },

      resolution_due_at: {
        type: Sequelize.DATE,
        allowNull: false,
      },

      first_response_at: {
        type: Sequelize.DATE,
        allowNull: true,
      },

      first_response_breach_at: {
        type: Sequelize.DATE,
        allowNull: true,
      },

      resolution_breach_at: {
        type: Sequelize.DATE,
        allowNull: true,
      },

      sla_revision: {
        type: Sequelize.INTEGER.UNSIGNED,
        allowNull: false,
        defaultValue: 1,
      },

      version: {
        type: Sequelize.INTEGER.UNSIGNED,
        allowNull: false,
        defaultValue: 1,
      },

      solution_cycle: {
        type: Sequelize.INTEGER.UNSIGNED,
        allowNull: false,
        defaultValue: 0,
      },

      closed_at: {
        type: Sequelize.DATE,
        allowNull: true,
      },

      closed_by_id: {
        type: Sequelize.BIGINT.UNSIGNED,
        allowNull: true,
        references: {
          model: "users",
          key: "id",
        },
        onUpdate: "CASCADE",
        onDelete: "RESTRICT",
      },

      closure_type: {
        type: Sequelize.ENUM(
            "CONFIRMADO",
            "ADMINISTRATIVO"
        ),
        allowNull: true,
      },

      closure_reason: {
        type: Sequelize.TEXT,
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

    // FK COMPUESTA: EMPRESA + ÁREA
    // Garantiza que el área pertenezca realmente a la empresa.

    await queryInterface.addConstraint("tickets", {
      fields: ["company_id", "area_id"],
      type: "foreign key",
      name: "fk_tickets_company_area",
      references: {
        table: "areas",
        fields: ["company_id", "id"],
      },
      onUpdate: "CASCADE",
      onDelete: "RESTRICT",
    });

    // FK COMPUESTA: QR + EMPRESA + ÁREA
    // Si existe QR, debe corresponder al mismo contexto.

    await queryInterface.addConstraint("tickets", {
      fields: ["company_id", "area_id", "qr_context_id"],
      type: "foreign key",
      name: "fk_tickets_qr_context",
      references: {
        table: "qr_contexts",
        fields: ["company_id", "area_id", "id"],
      },
      onUpdate: "CASCADE",
      onDelete: "RESTRICT",
    });

    // FK COMPUESTA: PRIORIDAD + POLÍTICA SLA
    // Evita enlazar una política SLA de otra prioridad.

    await queryInterface.addConstraint("tickets", {
      fields: ["priority_id", "sla_policy_id"],
      type: "foreign key",
      name: "fk_tickets_priority_sla_policy",
      references: {
        table: "sla_policies",
        fields: ["priority_id", "id"],
      },
      onUpdate: "CASCADE",
      onDelete: "RESTRICT",
    });

    // VALIDACIONES DE TÍTULO Y DESCRIPCIÓN
    // Maestro:
    // title: 5..180
    // description: 10..10000

    await queryInterface.addConstraint("tickets", {
      fields: ["title"],
      type: "check",
      name: "chk_tickets_title_length",
      where: Sequelize.literal(
          "CHAR_LENGTH(title) BETWEEN 5 AND 180"
      ),
    });

    await queryInterface.addConstraint("tickets", {
      fields: ["description"],
      type: "check",
      name: "chk_tickets_description_length",
      where: Sequelize.literal(
          "CHAR_LENGTH(description) BETWEEN 10 AND 10000"
      ),
    });

    // VALIDACIONES SLA

    await queryInterface.addConstraint("tickets", {
      fields: ["response_minutes_snapshot"],
      type: "check",
      name: "chk_tickets_response_minutes_positive",
      where: Sequelize.literal(
          "response_minutes_snapshot > 0"
      ),
    });

    await queryInterface.addConstraint("tickets", {
      fields: [
        "response_minutes_snapshot",
        "resolution_minutes_snapshot",
      ],
      type: "check",
      name: "chk_tickets_resolution_minutes",
      where: Sequelize.literal(
          "resolution_minutes_snapshot >= response_minutes_snapshot"
      ),
    });

    await queryInterface.addConstraint("tickets", {
      fields: ["warning_percent_snapshot"],
      type: "check",
      name: "chk_tickets_warning_percent",
      where: Sequelize.literal(
          "warning_percent_snapshot BETWEEN 1 AND 99"
      ),
    });

    // Los vencimientos deben conservar orden lógico.
    await queryInterface.addConstraint("tickets", {
      fields: [
        "first_response_due_at",
        "resolution_due_at",
      ],
      type: "check",
      name: "chk_tickets_due_dates",
      where: Sequelize.literal(
          "resolution_due_at >= first_response_due_at"
      ),
    });

    // CONCURRENCIA / REVISIONES

    await queryInterface.addConstraint("tickets", {
      fields: ["version"],
      type: "check",
      name: "chk_tickets_version_positive",
      where: Sequelize.literal("version > 0"),
    });

    await queryInterface.addConstraint("tickets", {
      fields: ["sla_revision"],
      type: "check",
      name: "chk_tickets_sla_revision_positive",
      where: Sequelize.literal("sla_revision > 0"),
    });

    // ASIGNACIÓN
    // Soporte no debe atender su propio ticket.

    await queryInterface.addConstraint("tickets", {
      fields: ["requester_id", "assigned_agent_id"],
      type: "check",
      name: "chk_tickets_agent_not_requester",
      where: Sequelize.literal(
          "assigned_agent_id IS NULL OR assigned_agent_id <> requester_id"
      ),
    });

    // COHERENCIA DEL CIERRE

    await queryInterface.addConstraint("tickets", {
      fields: [
        "status",
        "closed_at",
        "closed_by_id",
        "closure_type",
        "closure_reason",
      ],
      type: "check",
      name: "chk_tickets_closure_data",
      where: Sequelize.literal(`
        (
          status <> 'CERRADO'
          AND closed_at IS NULL
          AND closed_by_id IS NULL
          AND closure_type IS NULL
          AND closure_reason IS NULL
        )
        OR
        (
          status = 'CERRADO'
          AND closed_at IS NOT NULL
          AND closed_by_id IS NOT NULL
          AND closure_type IS NOT NULL
        )
      `),
    });

    // Cierre administrativo exige motivo.
    await queryInterface.addConstraint("tickets", {
      fields: ["closure_type", "closure_reason"],
      type: "check",
      name: "chk_tickets_admin_closure_reason",
      where: Sequelize.literal(`
        closure_type IS NULL
        OR closure_type <> 'ADMINISTRATIVO'
        OR (
          closure_reason IS NOT NULL
          AND CHAR_LENGTH(TRIM(closure_reason)) > 0
        )
      `),
    });

    // COHERENCIA TEMPORAL

    await queryInterface.addConstraint("tickets", {
      fields: ["created_at", "first_response_at"],
      type: "check",
      name: "chk_tickets_first_response_date",
      where: Sequelize.literal(
          "first_response_at IS NULL OR first_response_at >= created_at"
      ),
    });

    await queryInterface.addConstraint("tickets", {
      fields: ["created_at", "closed_at"],
      type: "check",
      name: "chk_tickets_closed_date",
      where: Sequelize.literal(
          "closed_at IS NULL OR closed_at >= created_at"
      ),
    });

    // ÍNDICES PRIORITARIOS EXIGIDOS POR EL MAESTRO

    await queryInterface.addIndex(
        "tickets",
        ["requester_id", "created_at"],
        {
          name: "idx_tickets_requester_created",
        }
    );

    await queryInterface.addIndex(
        "tickets",
        ["assigned_agent_id", "status"],
        {
          name: "idx_tickets_assigned_status",
        }
    );

    await queryInterface.addIndex(
        "tickets",
        ["status", "first_response_due_at"],
        {
          name: "idx_tickets_status_response_due",
        }
    );

    await queryInterface.addIndex(
        "tickets",
        ["status", "resolution_due_at"],
        {
          name: "idx_tickets_status_resolution_due",
        }
    );

    // ÍNDICES PARA REPORTES Y CONSULTAS

    await queryInterface.addIndex(
        "tickets",
        ["company_id", "area_id", "created_at"],
        {
          name: "idx_tickets_company_area_created",
        }
    );

    await queryInterface.addIndex(
        "tickets",
        ["category_id"],
        {
          name: "idx_tickets_category",
        }
    );

    await queryInterface.addIndex(
        "tickets",
        ["priority_id"],
        {
          name: "idx_tickets_priority",
        }
    );

    await queryInterface.addIndex(
        "tickets",
        ["status", "created_at"],
        {
          name: "idx_tickets_status_created",
        }
    );
  },

  async down(queryInterface) {
    await queryInterface.dropTable("tickets");
  },
};
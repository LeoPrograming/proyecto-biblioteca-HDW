# 🎫 TicketFlow

Sistema web colaborativo para la **gestión de tickets de soporte TI**, desarrollado como proyecto académico del curso **Herramientas de Desarrollo**.

TicketFlow busca centralizar el registro, asignación, atención, solución y cierre de incidencias y solicitudes de soporte, manteniendo trazabilidad, control de acceso, SLA, notificaciones y evidencia de cada atención.

---

## 📌 Estado del proyecto

> 🚧 **Proyecto en etapa inicial de desarrollo**

Actualmente el equipo está iniciando la construcción del proyecto a partir de la documentación maestra y el backlog técnico.

Las funcionalidades descritas en la documentación representan el alcance planificado y se incorporarán progresivamente mediante Issues, ramas y Pull Requests.

No debe asumirse que una funcionalidad está implementada hasta que haya sido integrada y validada en la rama `main`.

---

## 🎯 Objetivo

Desarrollar colaborativamente una aplicación web para gestionar el ciclo completo de tickets de soporte TI, aplicando:

- control de versiones con Git;
- colaboración mediante GitHub;
- Issues y ramas por unidad de trabajo;
- Pull Requests;
- integración continua;
- pruebas;
- documentación progresiva;
- despliegue en Internet.

---

## 🧩 Alcance general

TicketFlow contempla:

- autenticación de usuarios;
- control de acceso por roles;
- creación y seguimiento de tickets;
- creación propia y asistida;
- gestión sencilla de múltiples empresas y áreas;
- creación contextual mediante QR;
- asignación y reasignación;
- respuestas públicas y notas internas;
- acciones técnicas;
- adjuntos;
- propuestas de solución;
- aceptación o rechazo de soluciones;
- cierre administrativo;
- SLA de primera respuesta y solución;
- notificaciones internas;
- correo electrónico;
- auditoría;
- reportes;
- exportación CSV;
- despliegue y recuperación;
- integración opcional con WhatsApp.

---

## 👥 Roles

TicketFlow utiliza tres roles:

### SOLICITANTE

Registra y consulta sus propios tickets.

### AGENTE

Consulta tickets operativos y atiende aquellos que le han sido asignados.

### ADMINISTRADOR

Administra usuarios, empresas, áreas, categorías, SLA, QR, asignaciones, auditoría y reportes.

> Compartir empresa no permite a un solicitante consultar tickets de otros usuarios.

---

## 🏢 Gestión multiempresa

TicketFlow atenderá varias empresas utilizando:

- una sola aplicación;
- una sola base de datos;
- administración global;
- equipo de soporte compartido.

Cada solicitante estará asociado a una Empresa y Área.

Los agentes podrán atender tickets de todas las empresas.

---

## 🛠️ Stack tecnológico

### Frontend

- Angular
- TypeScript
- Angular Router
- HttpClient
- Reactive Forms

### Backend

- Node.js LTS
- Express
- JavaScript modular
- OpenAPI

### Persistencia

- MySQL
- Sequelize
- mysql2

### Colaboración y DevOps

- Git
- GitHub
- GitHub Issues
- GitHub Projects
- Pull Requests
- GitHub Actions

> Las versiones exactas y dependencias se incorporarán progresivamente cuando las Issues correspondientes las necesiten.

---

## 🏗️ Arquitectura prevista

La aplicación seguirá una arquitectura web:

Usuario
↓
Angular
↓
HTTP / HTTPS
↓
Node.js + Express
↓
Sequelize
↓
MySQL

El backend seguirá una separación de responsabilidades basada en:

- Routes
- Controllers
- Services
- Repositories
- Models
- Validators
- Integrations
- Jobs

Las carpetas se crearán progresivamente cuando sean necesarias.

No se crearán módulos vacíos únicamente para anticipar funcionalidades futuras.

---

## 📁 Estructura prevista

La estructura evolucionará progresivamente hacia una organización similar a:

    ticketflow/
    ├── README.md
    ├── backend/
    │   ├── README.md
    │   ├── .env.example
    │   └── src/
    ├── frontend/
    │   ├── README.md
    │   └── src/
    ├── docs/
    └── .github/
        └── workflows/

> Esta estructura representa el destino general del proyecto y no significa que todas las carpetas deban existir desde el inicio.

---

## 🌿 Flujo de trabajo con Git y GitHub

El desarrollo se organiza mediante:

**1 Issue = 1 responsable principal = 1 rama = 1 Pull Request**

Las ramas siguen la convención:

    <area>/tf-<NN>-<descripcion-breve>

Áreas permitidas:

- `backend`
- `frontend`
- `test`
- `deploy`
- `docs`

Ejemplos:

    backend/tf-01-inicio
    backend/tf-05-autenticacion
    frontend/tf-28-base-angular
    test/tf-44-calidad-frontend
    deploy/tf-46-despliegue

---

## 🔄 Regla de trabajo sobre `main`

`main` representa la última versión integrada y estable del proyecto.

Antes de comenzar una Issue:

1. actualizar `main`;
2. comprobar que esté estable;
3. revisar la implementación existente;
4. reutilizar soluciones ya disponibles;
5. crear la rama de la Issue desde ese `main`.

Una Issue posterior no debe volver a implementar una solución que ya exista correctamente.

La regla es:

**REUTILIZAR → EXTENDER → ADAPTAR → CREAR SOLO SI REALMENTE FALTA**

Está prohibido realizar desarrollo funcional directamente sobre `main` una vez completado el bootstrap inicial del repositorio.

---

## 🔀 Integración

Antes de integrar una Issue:

1. sincronizar nuevamente la rama con el `main` vigente;
2. revisar cambios realizados por otros integrantes;
3. resolver conflictos;
4. comprobar posibles duplicaciones lógicas;
5. ejecutar pruebas;
6. comprobar integración;
7. ejecutar regresión;
8. abrir Pull Request.

El proyecto utiliza una estrategia común de:

**Squash and merge**

Después del Merge se debe comprobar que el nuevo `main` continúe estable.

---

## 📚 Documentación

La documentación técnica se actualizará progresivamente junto con el código.

Los documentos principales del proyecto son:

- Documentación Maestra de TicketFlow.
- Backlog Técnico Completo de Issues.

El Documento Maestro define el comportamiento y las reglas del proyecto.

El Backlog define las Issues, dependencias, criterios, pruebas y planificación.

El código integrado en `main` representa el estado técnico real implementado.

---

## 🔐 Seguridad

El proyecto deberá mantener como principios:

- contraseñas almacenadas únicamente mediante hash;
- autenticación con expiración y revocación;
- autorización efectiva en backend;
- separación por rol, propiedad y asignación;
- protección contra acceso horizontal;
- secretos fuera del repositorio;
- `.env` local;
- `.env.example` sin credenciales;
- validación de entradas;
- archivos privados protegidos.

Los guards de Angular mejoran la experiencia de navegación, pero **no sustituyen la autorización del backend**.

---

## 📦 Dependencias

Las dependencias se incorporan únicamente cuando una Issue realmente las necesita.

Antes de agregar un paquete debe comprobarse si:

1. ya existe una solución equivalente;
2. Node.js, Angular, JavaScript o TypeScript pueden resolver la necesidad;
3. una dependencia ya instalada puede reutilizarse.

No se instalarán paquetes anticipadamente para funcionalidades futuras.

---

## 💬 WhatsApp

La integración con `whatsapp-web.js` es complementaria y opcional.

No forma parte del camino crítico del proyecto.

El núcleo de TicketFlow debe funcionar correctamente aunque WhatsApp:

- no esté implementado;
- permanezca desactivado;
- pierda sesión;
- presente errores.

---

## QR

La creación contextual mediante QR sí forma parte del núcleo del proyecto.

El QR:

- no autentica;
- no contiene JWT;
- no concede permisos;
- no crea automáticamente tickets;
- únicamente transporta contexto validado posteriormente por el backend.

---

## 🧪 Calidad

Cada Issue deberá comprobar:

### Prueba individual

La funcionalidad nueva cumple su objetivo.

### Integración

Funciona correctamente junto con sus dependencias.

### Regresión

Las funcionalidades ya integradas continúan funcionando.

Principio:

**Issue nueva + funcionalidades anteriores + `main` actual = TicketFlow funcionando correctamente.**

---

## 📅 Meta del proyecto

La meta interna del equipo es completar el alcance definido de TicketFlow a más tardar en la **semana 15**, incluyendo:

- desarrollo;
- pruebas;
- integración;
- despliegue;
- documentación;
- validación final.

---

## 🎓 Proyecto académico

**Curso:** Herramientas de Desarrollo  
**Periodo académico:** 2026-2  
**Equipo:** 5 integrantes

El proyecto busca demostrar el uso real de herramientas de desarrollo colaborativo mediante:

- Issues;
- ramas;
- commits;
- Pull Requests;
- resolución de conflictos;
- CI/CD;
- pruebas;
- despliegue;
- trazabilidad de contribuciones.

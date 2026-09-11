# TicketFlow Backend

Backend de TicketFlow desarrollado con Node.js, Express, Sequelize y MySQL.

Actualmente el backend incluye la arquitectura base de Express, configuración mediante variables de entorno, esquema MySQL versionado con migraciones Sequelize, datos iniciales mediante seeders y endpoints de salud para comprobar el estado del proceso y la disponibilidad de la base de datos.

## Tecnologías actuales

- Node.js
- Express
- MySQL
- Sequelize
- mysql2
- Sequelize CLI

No se utilizan dumps SQL manuales como fuente de verdad del esquema. La estructura de la base de datos se administra mediante migraciones Sequelize.

## Requisitos

Antes de ejecutar el backend se necesita:

- Node.js instalado.
- npm disponible.
- MySQL en ejecución.
- Una base de datos MySQL vacía para TicketFlow.
- Credenciales locales con permisos sobre dicha base.

MySQL Workbench puede utilizarse para inspeccionar la base de datos, pero no es necesario para ejecutar el proyecto.

## Instalación

Desde la carpeta `backend`:

```bash
npm ci
```

`npm ci` utiliza el `package-lock.json` versionado para instalar las dependencias definidas por el proyecto.

## Configuración local

El archivo `.env` es local y no debe versionarse.

Crear el archivo a partir de la plantilla:

### PowerShell

```powershell
Copy-Item .env.example .env
```

### Linux / macOS

```bash
cp .env.example .env
```

Configurar las variables correspondientes:

```env
PORT=3000

DB_HOST=127.0.0.1
DB_PORT=3306
DB_NAME=ticketflow
DB_USER=
DB_PASSWORD=
```

Cada integrante debe utilizar sus propias credenciales MySQL.

No se deben almacenar contraseñas ni credenciales reales en `.env.example`, README, commits, logs o evidencias del proyecto.

## Preparación de MySQL

Crear una base de datos vacía:

```sql
CREATE DATABASE ticketflow
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

La creación de tablas, relaciones, restricciones e índices se realiza exclusivamente mediante las migraciones del proyecto.

No se deben crear manualmente las tablas desde MySQL Workbench.

## Migraciones

Ejecutar todas las migraciones pendientes:

```bash
npm run db:migrate
```

Consultar su estado:

```bash
npm run db:migrate:status
```

Revertir la última migración:

```bash
npm run db:migrate:undo
```

Actualmente el esquema estructural de TicketFlow contempla 17 entidades principales:

- `roles`
- `users`
- `categories`
- `priorities`
- `sla_policies`
- `tickets`
- `ticket_comments`
- `ticket_attachments`
- `ticket_actions`
- `ticket_solutions`
- `ticket_history`
- `audit_events`
- `notifications`
- `notification_deliveries`
- `companies`
- `areas`
- `qr_contexts`

Las migraciones contienen las claves primarias, claves foráneas, restricciones `NOT NULL`, `UNIQUE`, `CHECK`, valores predeterminados e índices requeridos por el esquema actual.

Una migración que ya haya sido integrada y aplicada en ambientes compartidos no debe reescribirse. Los cambios posteriores del esquema deben realizarse mediante nuevas migraciones justificadas.

## Seeds

Cargar los datos iniciales:

```bash
npm run db:seed
```

Los seeders actuales preparan los datos mínimos necesarios para el esquema:

- roles;
- prioridades;
- categoría inicial;
- políticas SLA iniciales.

Los seeds ejecutados se registran mediante Sequelize para evitar volver a insertar los mismos datos en una segunda ejecución.

Para comprobarlo:

```bash
npm run db:seed
```

Si todos los seeders ya fueron ejecutados, Sequelize responderá:

```text
No seeders found.
```

Para revertir los seeders registrados:

```bash
npm run db:seed:undo
```

## Flujo de instalación desde cero

Para reproducir el backend en un equipo nuevo:

1. Instalar las dependencias:

```bash
npm ci
```

2. Copiar `.env.example` a `.env`.

3. Configurar las credenciales MySQL locales.

4. Crear la base de datos vacía `ticketflow`.

5. Ejecutar las migraciones:

```bash
npm run db:migrate
```

6. Ejecutar los seeds:

```bash
npm run db:seed
```

7. Iniciar Express:

```bash
npm start
```

8. Comprobar el estado del backend y de MySQL mediante los endpoints de salud.

## Ejecución

Iniciar el servidor:

```bash
npm start
```

Con la configuración actual y:

```env
PORT=3000
```

el backend queda disponible en:

```text
http://localhost:3000
```

El proceso admite cierre mediante `SIGINT` y `SIGTERM`.

En PowerShell se puede detener normalmente con:

```text
Ctrl + C
```

## Endpoints de salud

### GET /health

Comprueba el endpoint de salud conservado desde la arquitectura inicial.

```text
GET http://localhost:3000/health
```

Respuesta esperada:

```json
{
  "status": "UP",
  "service": "ticketflow-backend"
}
```

Estado HTTP:

```text
200
```

### GET /health/live

Comprueba que el proceso Express está ejecutándose.

```text
GET http://localhost:3000/health/live
```

Respuesta esperada:

```json
{
  "status": "UP",
  "service": "ticketflow-backend"
}
```

Estado HTTP:

```text
200
```

Este endpoint no depende de la disponibilidad de MySQL.

### GET /health/ready

Comprueba que el backend puede conectarse realmente a MySQL mediante Sequelize.

```text
GET http://localhost:3000/health/ready
```

Cuando MySQL está disponible:

```json
{
  "status": "READY",
  "service": "ticketflow-backend",
  "database": "UP"
}
```

Estado HTTP:

```text
200
```

Cuando MySQL no está disponible:

```json
{
  "status": "NOT_READY",
  "service": "ticketflow-backend",
  "database": "DOWN"
}
```

Estado HTTP:

```text
503
```

El endpoint no devuelve credenciales, mensajes SQL internos ni detalles sensibles de conexión.

## Pruebas

Ejecutar las pruebas disponibles:

```bash
npm test
```

También puede verificarse la sintaxis de las migraciones:

```powershell
Get-ChildItem .\migrations\*.js | ForEach-Object {
    node --check $_.FullName
}
```

Y de los seeders:

```powershell
Get-ChildItem .\seeders\*.js | ForEach-Object {
    node --check $_.FullName
}
```

## Verificaciones de base de datos

Comprobar las tablas existentes:

```sql
USE ticketflow;

SHOW TABLES;
```

Consultar las migraciones aplicadas:

```sql
SELECT * FROM SequelizeMeta;
```

Consultar los seeders registrados:

```sql
SELECT * FROM SequelizeData;
```

También pueden utilizarse `SHOW CREATE TABLE`, `SHOW INDEX` e `INFORMATION_SCHEMA` para inspeccionar restricciones, índices y tipos creados por las migraciones.

Estas consultas son únicamente de inspección. Las migraciones Sequelize continúan siendo la fuente técnica de verdad del esquema.

## Zona horaria

La conexión MySQL del backend utiliza UTC:

```text
+00:00
```

Las fechas persistidas por el sistema deben conservarse en UTC. La presentación posterior al usuario utilizará la zona horaria definida por TicketFlow para la interfaz.

## Errores comunes de conexión

### MySQL no está iniciado

`/health/live` puede continuar respondiendo `200`, pero:

```text
/health/ready
```

responderá:

```text
503 NOT_READY
```

Iniciar MySQL y volver a realizar la consulta.

### Credenciales incorrectas

Revisar en `.env`:

```env
DB_USER=
DB_PASSWORD=
```

No modificar `.env.example` con credenciales personales.

### Puerto MySQL incorrecto

Comprobar:

```env
DB_PORT=3306
```

y verificar que corresponda al puerto utilizado por la instalación local de MySQL.

### Base de datos inexistente

Crear previamente:

```sql
CREATE DATABASE ticketflow
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

y confirmar:

```env
DB_NAME=ticketflow
```

### Migraciones pendientes

Consultar:

```bash
npm run db:migrate:status
```

y, si existen migraciones pendientes:

```bash
npm run db:migrate
```

## Seguridad de configuración

No se deben versionar:

- `.env`
- credenciales reales;
- contraseñas;
- secretos;
- backups privados;
- exportaciones privadas de la base;
- `node_modules`;
- logs locales.

Sí deben mantenerse versionados los artefactos necesarios para reproducir el proyecto, entre ellos:

- `.env.example`;
- `package.json`;
- `package-lock.json`;
- migraciones;
- seeders;
- código fuente;
- documentación técnica.

## Estado actual

Actualmente se encuentra implementado:

- servidor Express ejecutable;
- puerto configurable;
- cierre limpio del servidor;
- endpoint `/health`;
- endpoint `/health/live`;
- endpoint `/health/ready`;
- conexión Sequelize con MySQL;
- esquema estructural de 17 entidades;
- migraciones versionadas;
- relaciones, restricciones e índices del esquema actual;
- seeds mínimos reproducibles;
- configuración local mediante `.env`;
- persistencia configurada en UTC.

Las funcionalidades de negocio que correspondan a Issues posteriores no se consideran implementadas por el hecho de que sus tablas ya existan.
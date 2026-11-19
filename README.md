# STM Biblioteca

[![CI](https://github.com/LeoPrograming/proyecto-biblioteca-HDW/actions/workflows/ci.yml/badge.svg)](https://github.com/LeoPrograming/proyecto-biblioteca-HDW/actions/workflows/ci.yml)

Sistema de gestión bibliotecaria desarrollado con Java y Spring Boot.

El proyecto avanza mediante Issues, ramas de trabajo, pruebas automatizadas y Pull Requests.

## Tercer avance: seguimiento de incidentes e integración continua

- **GitHub Issues:** los errores y mejoras se registran como issues con etiquetas de tipo (`bug`, `enhancement`) y de prioridad (`P1-Crítica`, `P2-Alta`, `P3-Media`, `P4-Baja`), cada uno con un responsable asignado.
- **Plantilla de reporte de error:** `.github/ISSUE_TEMPLATE/reporte-de-error.md` define los campos del reporte: pasos para reproducir, resultado esperado, resultado actual, evidencia, rol y prioridad sugerida.
- **Flujo de resolución:** cada error se corrige en una rama `fix/issue-n`, el commit menciona `#n` y el Pull Request incluye `Closes #n` para cerrar el issue automáticamente (ejemplo: issue #74 resuelto con el PR #80).
- **Integración continua:** el workflow `.github/workflows/ci.yml` de GitHub Actions se ejecuta en cada push y en cada Pull Request hacia `main`. Instala Java 17, levanta un MySQL temporal, compila el proyecto y ejecuta las pruebas automáticas.

## Estado actual

Las Issues 01 a 04 están integradas. La Issue 06 incorpora los DTO y las validaciones de autenticación.

| Issue | Alcance | Estado |
|---|---|---|
| 01 | Estructura base del backend | Integrada |
| 02 | Conexión con MySQL mediante variables de entorno | Integrada |
| 03 | Entidades Usuario y TipoUsuario | Integrada |
| 04 | Repositorios de usuarios y tipos de usuario | Integrada |
| 06 | DTO y validaciones de autenticación | En validación para integración |
| 05 | Servicio y flujo de autenticación | Depende de las Issues 04 y 06 |

La autenticación funcional y las pantallas se incorporarán en las tareas correspondientes.

## Tecnologías

| Componente | Versión o configuración |
|---|---|
| Java | JDK 17 |
| Spring Boot | 4.0.6 |
| Gestión de dependencias | Maven Wrapper |
| Backend web | Spring MVC |
| Persistencia | Spring Data JPA / Hibernate |
| Base de datos | MySQL |
| Driver JDBC | MySQL Connector/J |
| Validación | Jakarta Bean Validation |
| Empaquetado | WAR |
| Pruebas | JUnit y Spring Boot Test |
| Ejecución de pruebas | Maven Surefire y Maven Failsafe |

## Requisitos

- JDK 17.
- Git.
- Acceso a Internet para la descarga inicial de Maven y dependencias.
- MySQL disponible para ejecutar la aplicación y las pruebas de persistencia.
- Una base de desarrollo preparada con el esquema correspondiente.
- Una base exclusiva y desechable para pruebas.
- Variables de conexión configuradas localmente.

No es necesario instalar Maven por separado: el repositorio incluye Maven Wrapper.

## Organización del proyecto

Paquete base: `com.biblioteca.app`.

| Ubicación | Responsabilidad |
|---|---|
| `controller` | Recepción de solicitudes |
| `service` | Lógica de negocio |
| `repository` | Acceso a datos |
| `entity` | Entidades persistentes |
| `dto` | Transferencia y validación de datos de entrada |
| `config` | Configuración de la aplicación |
| `util` | Utilidades compartidas |
| `src/main/resources` | Configuración principal |
| `src/test/java` | Pruebas automatizadas |
| `src/test/resources` | Configuración de pruebas |
| `database` | Script SQL del proyecto |

Los paquetes preparados para funcionalidades posteriores contienen archivos `package-info.java`.

### Archivos principales

| Archivo | Propósito |
|---|---|
| `StmBibliotecaApplication.java` | Punto de entrada de Spring Boot |
| `ServletInitializer.java` | Configuración del inicio mediante despliegue WAR |
| `entity/Usuario.java` | Modelo persistente del usuario |
| `entity/TipoUsuario.java` | Modelo persistente del tipo de usuario |
| `repository/UsuarioRepository.java` | Operaciones y consultas de usuarios |
| `repository/TipoUsuarioRepository.java` | Operaciones y consultas de tipos de usuario |
| `dto/LoginDTO.java` | Datos de entrada para inicio de sesión |
| `util/PasswordValidator.java` | Validación reutilizable de complejidad de contraseñas |

Las rutas Java de esta tabla parten de `src/main/java/com/biblioteca/app`.

## Configurar Java 17

En PowerShell, ajustar la ruta a la instalación local del JDK:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

java -version
.\mvnw.cmd -version
```

Ambos comandos deben indicar Java 17.

Estas variables se aplican a la terminal actual. Si se abre otra terminal, comprobar nuevamente la versión.

En el IDE, seleccionar JDK 17 tanto para el proyecto como para la ejecución de Maven.

## Variables de entorno

La configuración separa la conexión de desarrollo de la conexión de pruebas.

| Variable | Propósito |
|---|---|
| `DB_URL` | URL JDBC de la base de desarrollo |
| `DB_USERNAME` | Usuario de la base de desarrollo |
| `DB_PASSWORD` | Contraseña de la base de desarrollo |
| `TEST_DB_URL` | URL JDBC de la base exclusiva de pruebas |
| `TEST_DB_USERNAME` | Usuario de la base de pruebas |
| `TEST_DB_PASSWORD` | Contraseña de la base de pruebas |

### Configuración mediante .env

Si todavía no existe el archivo local, crearlo desde la plantilla:

```powershell
Copy-Item .\.env.example .\.env
```

No sobrescribir un `.env` existente que ya tenga configuración local.

Completar sus valores en el equipo:

```properties
DB_URL=jdbc:mysql://127.0.0.1:3306/stm_biblioteca
DB_USERNAME=TU_USUARIO_LOCAL
DB_PASSWORD=TU_PASSWORD_LOCAL

TEST_DB_URL=jdbc:mysql://127.0.0.1:3307/stm_biblioteca_test
TEST_DB_USERNAME=TU_USUARIO_DE_PRUEBAS
TEST_DB_PASSWORD=TU_PASSWORD_DE_PRUEBAS
```

Los valores anteriores son ejemplos. Ajustar los puertos, usuarios y contraseñas al entorno local.

Las dos bases pueden estar en el mismo servidor y puerto, pero deben ser bases diferentes. La base de pruebas debe ser desechable.

La configuración principal carga el archivo mediante:

```properties
spring.config.import=optional:file:.env[.properties]
```

El archivo se interpreta como propiedades de Java. Usar líneas `CLAVE=valor`, sin prefijos `export`. Las comillas no se eliminan como en un script de shell.

Ejecutar la aplicación y Maven desde la raíz del repositorio para que puedan localizar el archivo.

### Variables del sistema o de la terminal

También pueden proporcionarse las variables directamente al proceso desde el entorno.

Las variables de entorno tienen prioridad sobre los valores del archivo `.env`.

No guardar comandos con contraseñas reales en documentación, scripts versionados ni evidencias.

### Configuración en el IDE

- Abrir la raíz del repositorio.
- Seleccionar JDK 17.
- Mantener la raíz como directorio de trabajo.
- Configurar las variables de entorno o utilizar el `.env` local.
- Mantener los archivos locales con credenciales fuera de Git.

## Preparación de MySQL

### Base de desarrollo

La aplicación utiliza una base con el esquema de STM Biblioteca.

El repositorio incluye:

```text
database/stm_biblioteca.sql
```

Revisar el script antes de ejecutarlo y utilizarlo únicamente en la base prevista. No importarlo sobre una base con información que deba conservarse sin revisar previamente sus sentencias.

La configuración principal utiliza:

```properties
spring.jpa.hibernate.ddl-auto=none
```

Hibernate no crea ni actualiza automáticamente las tablas de desarrollo. El esquema debe prepararse por separado.

### Base de pruebas

Crear una base exclusiva para las pruebas, por ejemplo:

```sql
CREATE DATABASE IF NOT EXISTS stm_biblioteca_test
    CHARACTER SET utf8mb4;
```

Configurar un usuario con los permisos necesarios sobre esa base.

El perfil `test` utiliza:

```properties
spring.jpa.hibernate.ddl-auto=create-drop
```

Esta configuración crea y elimina las tablas mapeadas durante el ciclo de vida del contexto de pruebas.

**Nunca apuntar `TEST_DB_URL` a una base de producción ni a una base con datos que deban conservarse.**

## Modelo de usuarios

### Usuario

La entidad `Usuario` se vincula con la tabla `usuario`.

Incluye:

- Identificador.
- Tipo de usuario.
- DNI.
- Nombre de usuario.
- Contraseña.
- Nombres.
- Apellidos.
- Correo electrónico.
- Teléfono.
- Dirección.
- Estado.

El atributo de contraseña está destinado a almacenar el hash utilizado por la autenticación.

### TipoUsuario

Representa el tipo asociado a cada usuario.

`Usuario` mantiene una relación muchos a uno con `TipoUsuario`.

### Repositorios

`UsuarioRepository` y `TipoUsuarioRepository` utilizan Spring Data JPA.

Las consultas de usuarios incluyen:

- Búsqueda por nombre de usuario.
- Búsqueda por correo y DNI.
- Comprobación de existencia por usuario, correo y DNI.
- Listado por identificador descendente.
- Búsqueda parcial por DNI o nombre de usuario.

La consulta prevista para autenticación es:

```java
findByUsuario(String usuario);
```

La búsqueda de un registro no equivale a autenticarlo. La comprobación de contraseña y estado corresponde al servicio de autenticación.

## DTO y validaciones de autenticación

### LoginDTO

`LoginDTO` transporta los datos de inicio de sesión sin utilizar directamente la entidad persistente.

| Campo | Regla | Mensaje |
|---|---|---|
| `usuario` | Obligatorio mediante `@NotBlank` | Ingrese su usuario |
| `contrasena` | Obligatorio mediante `@NotBlank` | Ingrese su contraseña |

Se rechazan valores nulos, vacíos o compuestos únicamente por espacios en blanco.

Ejemplos con datos ficticios:

| Usuario | Contraseña | Resultado de validación del DTO |
|---|---|---|
| `lector_prueba` | `Biblioteca@2026` | Válido |
| Vacío | Un valor no vacío | Usuario inválido |
| `lector_prueba` | Solo espacios | Contraseña inválida |
| Nulo | Nula | Ambos campos inválidos |

El DTO no comprueba si la cuenta existe ni si la contraseña coincide con la almacenada.

No exige formato de correo porque el acceso utiliza el campo `usuario`.

La contraseña se conserva tal como se recibe: no se recorta ni modifica.

### PasswordValidator

Utilidad para validar complejidad durante la creación o cambio de contraseñas.

Conserva las reglas del proyecto original:

- Entre 8 y 20 caracteres.
- Al menos una letra minúscula de `a-z`.
- Al menos una letra mayúscula de `A-Z`.
- Al menos un número.
- Al menos un carácter fuera de `A-Z`, `a-z` y `0-9`.

La expresión original admite el espacio como carácter especial. Los saltos de línea no se aceptan.

Métodos disponibles:

```java
//PasswordValidator.esSegura(contrasena);
//PasswordValidator.mensajeRequisitos();
```

El mensaje de requisitos es:

> La contraseña debe tener entre 8 y 20 caracteres, incluir mayúscula, minúscula, número y carácter especial.

### Integración con autenticación

El controlador que reciba `LoginDTO` deberá activar su validación mediante `@Valid` y manejar los errores correspondientes.

Los servicios pueden utilizar `jakarta.validation.Validator` cuando necesiten validación programática.

La complejidad de `PasswordValidator` no se aplica al inicio de sesión. La verificación de credenciales mediante BCrypt corresponde a la Issue 05.

Validar el formato de una entrada no significa conceder acceso.

Los errores deben comunicar el campo y el mensaje correspondiente, sin registrar ni devolver contraseñas, hashes o valores rechazados sensibles.

## Pruebas automatizadas

### Organización

| Grupo | Clases | Requiere MySQL |
|---|---|---|
| Base | `StmBibliotecaApplicationTests`, `ServletInitializerTests` | No |
| DTO y validaciones | `LoginDTOTest`, `PasswordValidatorTest` | No |
| Repositorios | `UsuarioRepositoryTest`, `TipoUsuarioRepositoryTest` | Sí, base de pruebas |
| Integración de conexión | `MysqlConnectionIT`, `MysqlFailureScenariosIT` | Sí, según el escenario |

Maven Surefire ejecuta las clases de pruebas habituales, incluidas las de repositorios.

Maven Failsafe ejecuta las clases terminadas en `IT` durante la fase de integración.

### Pruebas de la Issue 06

| Clase | Casos, contando entradas parametrizadas |
|---|---:|
| `LoginDTOTest` | 18 |
| `PasswordValidatorTest` | 22 |
| Total | 40 |

Se comprueban:

- Entradas válidas.
- Valores nulos y vacíos.
- Campos compuestos únicamente por espacios.
- Mensajes de validación.
- Conservación de la contraseña recibida.
- Separación entre validación de login y complejidad.
- Longitudes de 7, 8, 20 y 21 caracteres.
- Ausencia de los tipos de caracteres requeridos.
- Rechazo de saltos de línea.
- Tratamiento original del espacio como carácter especial.

### Ejecutar únicamente DTO y validaciones

No requiere conexión con MySQL:

```powershell
.\mvnw.cmd "-Dtest=LoginDTOTest,PasswordValidatorTest" test
```

### Ejecutar pruebas base, DTO y validaciones

```powershell
.\mvnw.cmd "-Dtest=StmBibliotecaApplicationTests,ServletInitializerTests,LoginDTOTest,PasswordValidatorTest" test
```

### Ejecutar la suite de Surefire

Requiere configurar las variables `TEST_DB_*` y disponer de la base de pruebas:

```powershell
.\mvnw.cmd clean test
```

Este comando también ejecuta las pruebas de repositorios.

### Validación completa

Con las conexiones de desarrollo y pruebas configuradas:

```powershell
.\mvnw.cmd clean verify
```

Este comando:

1. Compila el proyecto.
2. Ejecuta las pruebas de Surefire.
3. Genera el WAR.
4. Ejecuta las pruebas de integración de Failsafe.
5. Verifica sus resultados.

El resultado esperado es:

```text
BUILD SUCCESS
```

Las pruebas de la Issue 06 se han ejecutado satisfactoriamente durante su desarrollo. La regresión completa debe comprobarse antes de integrar el Pull Request.

### Particularidad de una prueba heredada

El escenario de credenciales inválidas de `MysqlFailureScenariosIT`
utiliza actualmente una URL fija hacia:

```text
jdbc:mysql://127.0.0.1:3306/stm_biblioteca
```

Ese escenario necesita un servidor disponible en dicha dirección para comprobar específicamente el rechazo de credenciales.

Si el servidor utiliza otro puerto, revisar esa prueba antes de ejecutar la validación completa. Un fallo de conectividad no demuestra un rechazo de credenciales.

### Consultar resultados

Resultados de Surefire:

```powershell
Get-Content .\target\surefire-reports\*.txt
```

Resultados de Failsafe:

```powershell
Get-Content .\target\failsafe-reports\*.txt
```

La prueba de `ServletInitializer` verifica su configuración. No sustituye una prueba de despliegue en un Tomcat externo.

## Ejecutar la aplicación

Después de generar el WAR, con Java 17 activo y las variables `DB_*` configuradas:

```powershell
java -jar .\target\stm-biblioteca-0.0.1-SNAPSHOT.war
```

El puerto predeterminado es 8080.

Para utilizar otro puerto:

```powershell
java -jar .\target\stm-biblioteca-0.0.1-SNAPSHOT.war --server.port=8081
```

El arranque correcto se identifica mediante:

```text
Started StmBibliotecaApplication
```

Detener la aplicación con `Ctrl + C`.

En esta etapa no existen pantallas ni endpoints de negocio incorporados por la Issue 06. Una respuesta 404 al visitar la raíz no implica que el servidor haya fallado.

## Credenciales y archivos locales

- No guardar credenciales reales en código, configuración versionada o documentación.
- Mantener `.env` fuera de Git.
- Versionar únicamente `.env.example` con ejemplos sin secretos.
- Mantener separadas las variables de desarrollo y pruebas.
- No incluir credenciales dentro de la URL JDBC.
- No publicar contraseñas ni hashes en logs, capturas o Pull Requests.
- Utilizar datos ficticios en las pruebas.
- Mantener `target/` y los archivos del IDE fuera del repositorio.

Comprobar la exclusión del archivo local:

```powershell
git check-ignore .env
```

Comprobar qué archivos de entorno están versionados:

```powershell
git ls-files -- ".env" ".env.*"
```

Solo debe aparecer la plantilla `.env.example`, si está incorporada al repositorio.

## Problemas frecuentes

| Problema | Comprobación |
|---|---|
| Maven utiliza otra versión de Java | Revisar `JAVA_HOME`, `Path` y `.\mvnw.cmd -version` |
| No se encuentra `mvnw.cmd` | Ejecutar desde la raíz del repositorio |
| Falta una variable de conexión | Revisar el entorno o el `.env` local |
| MySQL rechaza la conexión | Comprobar servidor, puerto, usuario y permisos |
| Fallan las pruebas de repositorios | Revisar las variables `TEST_DB_*` y la base de pruebas |
| Puerto HTTP ocupado | Iniciar con `--server.port=8081` u otro puerto disponible |
| Error `illegal character: '\ufeff'` | Guardar el archivo Java en UTF-8 sin BOM |
| Respuesta 404 en la raíz | Comprobar si existe una ruta implementada para esa dirección |

## Flujo de trabajo

1. Revisar la Issue y sus dependencias.
2. Actualizar `main`.
3. Crear o actualizar la rama asignada.
4. Implementar los cambios correspondientes.
5. Agregar pruebas del comportamiento incorporado.
6. Ejecutar las pruebas específicas y la regresión.
7. Actualizar la documentación.
8. Revisar archivos y exclusión de secretos.
9. Crear el commit y publicar la rama.
10. Abrir un Pull Request hacia `main`.
11. Revisar e integrar el Pull Request.
12. Actualizar `main` localmente y cerrar la Issue.

La rama de la Issue 06 es:

```text
backend/integracion-dto
```

Las dependencias se incorporan cuando una Issue las necesita.

## Próximos avances

Al integrar la Issue 06, las tareas de autenticación y servicio inicial de usuarios podrán utilizar el DTO y las validaciones disponibles, siempre que sus demás dependencias estén completas.

## Autenticación básica

La autenticación del backend se realiza mediante el siguiente endpoint:

**Método:** `POST`

**Ruta:**

`/api/auth/login`

### Datos de entrada

La solicitud debe enviarse en formato JSON:

```json
{
  "usuario": "nombre_usuario",
  "contrasena": "contraseña"
}

### Issue 09 - Servicio inicial de usuarios
* Se implementó `UsuarioService` manteniendo la lógica de negocio separada de los controladores.
* Operaciones incluidas: `listarUsuarios`, `buscarUsuarios`, `buscarPorId`, `registrar`, `actualizar`, `cambiarEstado` y `convertirADTO`.
* Se incorporaron validaciones de unicidad y encriptación BCrypt antes de persistir en MySQL.
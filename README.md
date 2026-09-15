# STM Biblioteca

Sistema de gestión bibliotecaria desarrollado como proyecto académico. El desarrollo se organiza mediante Issues, ramas de trabajo y Pull Requests.

## Estado actual

Segunda etapa: conexión del backend con MySQL mediante variables de entorno.

Implementado:

- Configuración de Spring Boot y Maven.
- Organización inicial por capas.
- Clase principal de la aplicación.
- Inicializador para despliegue WAR.
- Spring Data JPA.
- Driver MySQL Connector/J.
- Conexión con MySQL mediante `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`.
- Carga local opcional de variables desde archivo `.env`.
- Hibernate configurado con `ddl-auto=none` para no modificar automáticamente la base existente.
- Pruebas base independientes de MySQL.
- Pruebas de integración de conexión, `SELECT 1` y escenarios de fallo.

En esta etapa no se incorporan entidades, repositorios ni tablas de negocio nuevas.

## Tecnologías

| Componente | Versión o configuración |
|---|---|
| Java | JDK 17 |
| Spring Boot | 4.0.6 |
| Gestión de dependencias | Maven Wrapper |
| Backend web | Spring MVC |
| Empaquetado | WAR |
| Pruebas | JUnit y Spring Boot Test |
| Persistencia | Spring Data JPA / Hibernate |
| Base de datos | MySQL |
| Driver JDBC | MySQL Connector/J |
| Pruebas de integración | Maven Failsafe |

## Requisitos

- JDK 17 instalado.
- Git.
- Acceso a Internet para descargar Maven y las dependencias durante la primera ejecución.
- MySQL disponible para ejecutar las pruebas de integración.
- Base de datos local `stm_biblioteca`.
- Variables de conexión configuradas mediante `.env` o variables de entorno del sistema.

## Organización del código

Paquete base: `com.biblioteca.app`.

| Paquete | Responsabilidad |
|---|---|
| controller | Recepción de solicitudes |
| service | Lógica de negocio |
| repository | Acceso a datos |
| entity | Entidades del sistema |
| dto | Transferencia de datos |
| config | Configuración de la aplicación |
| util | Utilidades compartidas |

Los paquetes se preparan mediante archivos `package-info.java`.

Archivos principales:

- `src/main/java/com/biblioteca/app/StmBibliotecaApplication.java`
- `src/main/java/com/biblioteca/app/ServletInitializer.java`
- `src/main/resources/application.properties`
- `src/test/java/com/biblioteca/app/StmBibliotecaApplicationTests.java`
- `src/test/java/com/biblioteca/app/ServletInitializerTests.java`
- `src/test/java/com/biblioteca/app/MysqlConnectionIT.java`
- `src/test/java/com/biblioteca/app/MysqlFailureScenariosIT.java`

## Configurar Java 17 en Windows

Desde PowerShell, ajustar la ruta a la instalación local del JDK:

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
$env:Path="$env:JAVA_HOME\bin;$env:Path"

java -version
.\mvnw.cmd -version
```

Ambos comandos deben indicar Java 17.

Estas variables se aplican a la terminal actual. En el IDE también debe seleccionarse JDK 17 para el proyecto y para Maven.

## Configurar la conexión con MySQL

La aplicación requiere las siguientes variables:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

No deben guardarse credenciales reales en archivos versionados.

### Desarrollo local con `.env`

Desde la raíz del proyecto:

```powershell
Copy-Item .\.env.example .\.env
```

Editar únicamente el archivo `.env` local:

```properties
DB_URL=jdbc:mysql://127.0.0.1:3306/stm_biblioteca
DB_USERNAME=TU_USUARIO_LOCAL
DB_PASSWORD=TU_PASSWORD_LOCAL
```

El archivo `.env` está excluido de Git.

Spring Boot lo carga mediante:

```properties
spring.config.import=optional:file:.env[.properties]
```

Por ello, la aplicación debe iniciarse teniendo como directorio de trabajo la raíz del repositorio.

### PowerShell sin `.env`

También pueden definirse las variables únicamente para la terminal actual:

```powershell
$env:DB_URL="jdbc:mysql://127.0.0.1:3306/stm_biblioteca"
$env:DB_USERNAME="TU_USUARIO_LOCAL"
$env:DB_PASSWORD="TU_PASSWORD_LOCAL"
```

Las variables de entorno tienen prioridad sobre los valores cargados desde `.env`.

### VS Code / IDE

- Abrir como workspace la raíz del repositorio.
- Seleccionar JDK 17 para Java y Maven.
- Mantener la raíz del repositorio como directorio de trabajo de la aplicación.
- Crear `.env` a partir de `.env.example`.
- No agregar `.env` al control de versiones.

La aplicación puede ejecutarse desde la terminal integrada o mediante la configuración de ejecución del IDE, siempre que el directorio de trabajo sea la raíz del proyecto.

## Compilar, probar y empaquetar

### Pruebas base sin MySQL

Desde la raíz del repositorio:

```powershell
.\mvnw.cmd clean test
```

Este comando ejecuta las pruebas base heredadas del backend.

Estas pruebas no necesitan `.env`, credenciales ni un servidor MySQL disponible.

### Validación completa con MySQL

Con `.env` configurado y MySQL disponible:

```powershell
.\mvnw.cmd clean verify
```

Este comando:

1. Compila el proyecto.
2. Ejecuta las pruebas base mediante Maven Surefire.
3. Genera el archivo WAR.
4. Ejecuta las pruebas de integración terminadas en `IT` mediante Maven Failsafe.

El artefacto generado es:

```text
target/stm-biblioteca-0.0.1-SNAPSHOT.war
```

El resultado esperado cuando todas las validaciones pasan es:

```text
BUILD SUCCESS
```

## Ejecutar la aplicación

Con Java 17 activo y la configuración de conexión disponible mediante `.env` o variables de entorno:

```powershell
java -jar .\target\stm-biblioteca-0.0.1-SNAPSHOT.war
```

El puerto predeterminado es 8080. Si está ocupado, puede utilizarse otro:

```powershell
java -jar .\target\stm-biblioteca-0.0.1-SNAPSHOT.war --server.port=8081
```

El arranque correcto se identifica mediante el mensaje:

```text
Started StmBibliotecaApplication
```

Para detener la aplicación, presionar `Ctrl + C`.

En esta etapa todavía no se incorporan endpoints ni funcionalidades de negocio. Una respuesta 404 al visitar la raíz no implica que el servidor haya fallado.

## Pruebas automatizadas

### Pruebas base

| Prueba | Propósito |
|---|---|
| `debeCargarElContextoDeSpring` | Comprobar que el contexto base de Spring está activo |
| `debeConfigurarElDespachadorDeSolicitudesWeb` | Comprobar la configuración del DispatcherServlet |
| `debeRegistrarLaClasePrincipalParaElDespliegueWar` | Comprobar la clase registrada por el inicializador WAR |

Estas pruebas se ejecutan mediante Maven Surefire y no necesitan MySQL.

### Pruebas de integración MySQL

| Prueba | Propósito |
|---|---|
| `debeConectarseCorrectamenteAMysql` | Comprobar una conexión JDBC real con MySQL |
| `debeEjecutarSelectUno` | Ejecutar `SELECT 1` y verificar que devuelve `1` |
| `debeFallarSiFaltaUnaVariableObligatoria` | Comprobar que la aplicación falla cuando falta configuración obligatoria |
| `debeFallarConCredencialesInvalidas` | Comprobar el rechazo de credenciales incorrectas |
| `debeFallarSiMysqlNoEstaDisponible` | Comprobar el fallo controlado cuando MySQL no está disponible |

Las pruebas de integración utilizan Maven Failsafe y corresponden a las clases cuyo nombre termina en `IT`.

Los escenarios negativos utilizan tiempos de espera limitados para evitar bloqueos prolongados.

Los resultados de las pruebas base pueden consultarse con:

```powershell
Get-Content .\target\surefire-reports\*.txt
```

Los resultados de las pruebas de integración pueden consultarse con:

```powershell
Get-Content .\target\failsafe-reports\*.txt
```

La prueba del inicializador WAR verifica su configuración. No sustituye una prueba de despliegue en un servidor Tomcat externo.

## Configuración de persistencia

La configuración principal utiliza:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=none
```

La estrategia:

```properties
spring.jpa.hibernate.ddl-auto=none
```

evita que Hibernate cree, elimine o modifique automáticamente las tablas existentes.

La base de datos `stm_biblioteca` ya existe y en esta Issue únicamente se valida la infraestructura de conexión y persistencia.

No se crean entidades ni repositorios de negocio en esta etapa.

## Variables de entorno y credenciales

La conexión utiliza exclusivamente:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

Reglas de seguridad:

- `.env` es exclusivamente local y está ignorado por Git.
- `.env.example` documenta la configuración sin secretos.
- `application.properties` no contiene usuario ni contraseña reales.
- La URL JDBC no contiene usuario ni contraseña.
- Las pruebas no contienen credenciales reales.
- `target/` permanece fuera del repositorio.
- Los archivos propios del IDE permanecen fuera del repositorio.
- No deben incluirse secretos en commits ni Pull Requests.

## Flujo de trabajo

1. Crear la Issue con su alcance y criterios de aceptación.
2. Actualizar `main` y crear o utilizar la rama correspondiente.
3. Implementar únicamente los cambios de la Issue.
4. Ejecutar las pruebas base.
5. Ejecutar las pruebas de integración cuando corresponda.
6. Revisar los cambios con Git.
7. Registrar los cambios mediante commits.
8. Publicar la rama.
9. Crear un Pull Request hacia `main` y vincular la Issue.
10. Revisar e integrar el Pull Request.
11. Actualizar `main` localmente.

Las dependencias se incorporan conforme sean necesarias para cada Issue.

## Siguiente avance

Incorporar las entidades y funcionalidades de persistencia correspondientes a las siguientes Issues, reutilizando la infraestructura JPA configurada en esta etapa.

## Issue 04 - Repositorios de Usuario
* Se implementó `UsuarioRepository` con consultas de validación (DNI, Correo, Usuario) y de autenticación (`findByUsuario`).
* Se implementó `TipoUsuarioRepository`.
* Las interfaces extienden `JpaRepository` y son compatibles con Java 17.
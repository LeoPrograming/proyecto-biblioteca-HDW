# STM Biblioteca

Sistema de gestión bibliotecaria desarrollado como proyecto académico. El desarrollo se organiza mediante Issues, ramas de trabajo y Pull Requests.

## Estado actual

Primera etapa: estructura base del backend.

Implementado:

- Configuración de Spring Boot y Maven.
- Organización inicial por capas.
- Clase principal de la aplicación.
- Inicializador para despliegue WAR.
- Configuración base de la aplicación.
- Pruebas automatizadas de contexto, configuración web e inicializador WAR.

La conexión con MySQL y las funcionalidades de negocio se incorporarán en los siguientes avances.

## Tecnologías

| Componente | Versión o configuración |
|---|---|
| Java | JDK 17 |
| Spring Boot | 4.0.6 |
| Gestión de dependencias | Maven Wrapper |
| Backend web | Spring MVC |
| Empaquetado | WAR |
| Pruebas | JUnit y Spring Boot Test |

## Requisitos

- JDK 17 instalado.
- Git.
- Acceso a Internet para descargar Maven y las dependencias durante la primera ejecución.

En esta etapa no se requiere una base de datos.

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

## Configurar Java 17 en Windows

Desde PowerShell, ajustar la ruta a la instalación local del JDK:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

java -version
.\mvnw.cmd -version
```

Ambos comandos deben indicar Java 17.

Estas variables se aplican a la terminal actual. En el IDE también debe seleccionarse JDK 17 para el proyecto y para Maven.

## Compilar, probar y empaquetar

Desde la raíz del repositorio:

```powershell
.\mvnw.cmd clean verify
```

Este comando compila el código, ejecuta las pruebas y genera:

```text
target/stm-biblioteca-0.0.1-SNAPSHOT.war
```

El resultado esperado es `BUILD SUCCESS`.

## Ejecutar la aplicación

Con Java 17 activo:

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

Esta etapa todavía no incorpora pantallas ni endpoints de negocio. Una respuesta 404 al visitar la raíz no implica que el servidor haya fallado.

## Pruebas automatizadas

| Prueba | Propósito |
|---|---|
| debeCargarElContextoDeSpring | Comprobar que el contexto de Spring está activo |
| debeConfigurarElDespachadorDeSolicitudesWeb | Comprobar la configuración del DispatcherServlet |
| debeRegistrarLaClasePrincipalParaElDespliegueWar | Comprobar la clase registrada por el inicializador WAR |

Consultar los resultados después de ejecutar las pruebas:

```powershell
Get-Content .\target\surefire-reports\*.txt
```

La prueba del inicializador verifica su configuración. No sustituye una prueba de despliegue en un servidor Tomcat externo.

## Flujo de trabajo

1. Crear la Issue con su alcance y criterios de aceptación.
2. Actualizar main y crear la rama correspondiente.
3. Implementar los cambios y sus pruebas.
4. Ejecutar las validaciones.
5. Revisar y registrar los cambios mediante commits.
6. Publicar la rama.
7. Crear un Pull Request hacia main y vincular la Issue.
8. Revisar e integrar el Pull Request.
9. Actualizar main localmente.

Las dependencias se incorporan conforme sean necesarias para cada Issue.

## Siguiente avance

Configurar la conexión con MySQL, una vez integrada la estructura base.

## Variables de entorno y credenciales

Las credenciales se configurarán mediante variables de entorno.

- No se guardarán contraseñas, tokens ni claves en el código fuente,
  application.properties, pruebas o documentación.
- El archivo .env será local y estará excluido de Git.
- El archivo .env.example documentará las variables necesarias sin secretos.
- La conexión con MySQL y su mecanismo de carga de configuración se
  implementarán en la Issue 02.

Esta primera etapa no requiere credenciales.
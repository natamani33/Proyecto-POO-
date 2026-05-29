# Sistema de Talento Humano - Gobernacion de Boyaca

Aplicacion de escritorio desarrollada en JavaFX para gestionar informacion de talento humano en una base de datos relacional unificada.

Este README esta enfocado en una sola cosa: ejecutarlo bien desde IntelliJ IDEA.

## Tecnologias

- Java 21
- JavaFX 21
- Gradle
- Hibernate ORM + JPA
- PostgreSQL 16 en Docker

## Modulos del sistema

- Servidores publicos
- Dependencias / secretarias
- Cargos
- Vinculacion laboral
- Situaciones administrativas
- Vacaciones
- Permisos
- Licencias

## Requisitos (IntelliJ)

- IntelliJ IDEA Community o Ultimate
- JDK 21 configurado en IntelliJ
- Docker Desktop encendido

## Inicio rapido en IntelliJ (recomendado)

### 1. Abrir proyecto

1. Open en IntelliJ y selecciona la carpeta raiz del proyecto.
2. Espera la importacion de Gradle.
3. Verifica Project SDK = 21.

### 2. Ejecutar en un solo paso

1. Abre el panel Gradle (derecha).
2. Ve a Tasks > application.
3. Ejecuta devRun.

La tarea devRun:

1. Levanta PostgreSQL con Docker Compose.
2. Ejecuta la app JavaFX.

## Flujo alterno en IntelliJ (paso a paso)

Si prefieres controlar cada etapa manualmente, usa la Terminal de IntelliJ:

1. Iniciar DB:

```powershell
docker compose up -d
```

2. Ejecutar app:

```powershell
.\gradlew.bat run --no-daemon
```

3. Detener DB al finalizar:

```powershell
docker compose down
```

## Configuracion de base de datos

La aplicacion usa una unica base de datos PostgreSQL en Docker:

- Host: localhost
- Puerto: 55439
- Base de datos: talento_humano
- Usuario: postgres
- Contrasena: talento_seguro_2026

JDBC actual:

```text
jdbc:postgresql://localhost:55439/talento_humano
```

Hibernate mantiene el esquema con `hibernate.hbm2ddl.auto=update`.

## Tareas Gradle utiles (panel Gradle de IntelliJ)

- devRun: inicia DB y ejecuta la app.
- upDb: inicia solo PostgreSQL.
- downDb: detiene PostgreSQL.
- compileJava: compila el proyecto.

## Estructura del proyecto

```text
Proyecto/
├─ build.gradle
├─ settings.gradle
├─ docker-compose.yml
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  ├─ controller/
│  │  │  ├─ dao/
│  │  │  ├─ model/
│  │  │  ├─ org/example/      # Main
│  │  │  └─ util/
│  │  └─ resources/
│  │     ├─ META-INF/
│  │     │  └─ persistence.xml
│  │     └─ view/             # FXML + CSS
│  └─ test/
└─ gradle/
```

## Modelo de datos principal

Tablas:

- publicserver
- dependency
- position
- employmentlink
- administrativesituation
- vacation
- permission
- license

Relaciones clave:

- position.dependency_id -> dependency.id
- employmentlink.server_id -> publicserver.id
- employmentlink.position_id -> position.id
- administrativesituation.server_id -> publicserver.id
- vacation.server_id -> publicserver.id
- permission.server_id -> publicserver.id
- license.server_id -> publicserver.id

## Solucion de problemas en IntelliJ

### Docker no esta arriba

Sintoma: falla devRun por conexion a DB.

Accion:

1. Abre Docker Desktop.
2. Ejecuta de nuevo devRun.

### IntelliJ no refresca Gradle

Accion:

1. En panel Gradle, click en Reload All Gradle Projects.

### Puerto ocupado

Accion:

1. Cambia el puerto en docker-compose.yml.
2. Actualiza JDBC en src/main/resources/META-INF/persistence.xml.

### Lock de build en Windows/OneDrive

Accion:

1. Ejecuta `.\gradlew.bat --stop`.
2. Vuelve a correr `compileJava` o `devRun`.

## Clase principal

- org.example.Main

## Verificacion rapida

Compilacion validada con:

```powershell
.\gradlew.bat compileJava --no-daemon
```
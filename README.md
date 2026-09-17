# Administrador de Loteos

Aplicación web para administrar loteos, etapas y parcelas, con acceso diferenciado
para usuarios administradores y clientes. Incluye visualización de información
geográfica mediante mapas y archivos GeoJSON.

## Requisitos

- Java 17 o superior.
- Maven 3.9 o superior.
- PostgreSQL 14 o superior.
- Una base de datos PostgreSQL disponible para la aplicación.

## Configuración local

La configuración de desarrollo se encuentra en:

```text
Loteos/src/main/resources/application.properties
```

El archivo admite variables de entorno y utiliza estos valores por defecto:

```text
DATABASE_URL=jdbc:postgresql://localhost:5432/agrimensura
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=...
```

Definí la contraseña mediante una variable de entorno o mediante tu archivo
local ignorado por Git. No subas credenciales al repositorio.

## Ejecutar la aplicación

Desde la raíz del repositorio:

```bash
cd Loteos
mvn spring-boot:run
```

La aplicación queda disponible en:

```text
http://localhost:8080/login
```

También se puede generar el artefacto ejecutable:

```bash
cd Loteos
mvn clean package
java -jar target/App-0.0.1-SNAPSHOT.jar
```

Los scripts `iniciar.sh` y `reiniciar.sh` compilan y ejecutan la aplicación
usando PostgreSQL sin borrar ni modificar los datos existentes. Ambos pueden
ejecutarse desde cualquier directorio:

```bash
./Loteos/iniciar.sh
```

`reiniciar.sh` vuelve a empaquetar la aplicación antes de iniciarla; no
reinicia ni elimina la base de datos.

## Tests

Para ejecutar la suite:

```bash
cd Loteos
mvn test
```

Los tests actuales cubren:

- autorización de clientes por loteo asignado;
- rechazo de acceso a loteos ajenos;
- validaciones básicas de loteos y lotes.

## Estructura principal

```text
Loteos/
├── src/main/java/com/example/app/
│   ├── config/          # Configuración de Spring Security
│   ├── controllers/     # Endpoints web y API
│   ├── models/          # Entidades JPA
│   ├── repositories/    # Acceso a PostgreSQL
│   └── services/        # Reglas de negocio
├── src/main/resources/
│   ├── static/          # Imágenes, mapas y archivos geográficos
│   ├── templates/       # Vistas Mustache
│   └── application.properties
└── src/test/            # Tests unitarios
```

## Roles

- `ADMIN`: administra loteos, etapas y parcelas.
- `CLIENTE`: consulta y actualiza los datos permitidos del loteo que tiene
  asignado.

El acceso de los clientes se valida tanto por rol como por loteo asignado.

## Persistencia

La aplicación utiliza Spring Data JPA con PostgreSQL. Actualmente Hibernate
usa `ddl-auto=update`, por lo que crea o actualiza las tablas automáticamente
durante el desarrollo. Para producción se recomienda migrar a Flyway o
Liquibase antes de desplegar.

Los archivos de mapas y datos geográficos se sirven desde
`src/main/resources/static/`. Si el volumen de datos aumenta, conviene
almacenarlos fuera del artefacto de la aplicación.

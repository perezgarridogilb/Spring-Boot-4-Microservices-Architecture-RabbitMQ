# Spring-Boot-4-Microservices-Architecture-RabbitMQ


<img width="495" height="432" alt="Captura de pantalla 2026-07-29 a la(s) 5 45 25 p m" src="https://github.com/user-attachments/assets/b2d6023a-ac9f-40d3-ba0e-96be18239e17" />

## Config Server + Inventory Service

La siguiente imagen muestra la respuesta del **config-server** (`http://localhost:8888/inventory-service/default`), que es el JSON que devuelve la configuración centralizada del microservicio de inventario:

<img width="1532" height="911" alt="Captura de pantalla 2026-08-17 a la(s) 12 23 21 p m" src="https://github.com/user-attachments/assets/9dc30113-8298-41f7-8e13-6b87ad4f5214" />

### Qué muestra la respuesta

```json
{
  "name": "inventory-service",
  "profiles": ["default"],
  "label": null,
  "version": "7dec9c0495e2ecba9c867b1f187578cd5a7e308b",
  "propertySources": [
    {
      "name": "file:///.../microservices-ecommerce/config-data/inventory-service.yaml",
      "source": {
        "server.port": 8082,
        "spring.application.name": "inventory-service",
        "spring.threads.virtual.enabled": true,
        "spring.datasource.url": "jdbc:mysql://localhost:3307/inventory-db",
        "spring.datasource.username": "root",
        "spring.datasource.password": "root",
        "spring.datasource.driver-class-name": "com.mysql.cj.jdbc.Driver",
        "spring.jpa.hibernate.ddl-auto": "update",
        "spring.jpa.show-sql": true,
        "spring.jpa.properties.hibernate.dialect": "org.hibernate.dialect.MySQLDialect"
      }
    }
  ]
}
```

Cada capa de este JSON corresponde a:

- **`name`** → Nombre de la aplicación (`spring.application.name`). Coincide con el nombre del archivo `inventory-service.yaml` que el config-server busca en su repositorio de configuración.
- **`profiles`** → Perfiles activos; aquí `default` porque no se activó otro perfil (ej. `dev`, `prod`).
- **`label`** → Rama del repositorio git (usada en modo git). Queda `null` al servir desde el filesystem local.
- **`version`** → Hash del commit del repositorio de configuraciones que contiene este archivo. Si cambias el yaml y haces commit, este valor cambia y el cliente puede "refresh" la config.
- **`propertySources`** → Arreglo con los orígenes de propiedades que el config-server le entrega al cliente:
  - **`name`** → Ruta física del archivo leído (`config-data/inventory-service.yaml`).
  - **`source`** → Mapa de propiedades clave/valor ya "aplanadas" que el microservicio aplicará al arrancar:

    | Propiedad | Valor | Qué controla |
    |---|---|---|
    | `server.port` | `8082` | Puerto HTTP del inventory-service |
    | `spring.application.name` | `inventory-service` | Nombre del microservicio (usado en discovery/registry) |
    | `spring.threads.virtual.enabled` | `true` | Hilos virtuales de Java 21 |
    | `spring.datasource.url` | `jdbc:mysql://localhost:3307/inventory-db` | Conexión a MySQL (contenedor `inventory-db`, puerto 3307) |
    | `spring.datasource.username` / `password` | `root` / `root` | Credenciales de la base de datos |
    | `spring.jpa.hibernate.ddl-auto` | `update` | Hibernate actualiza/crea el esquema automáticamente |
    | `spring.jpa.show-sql` | `true` | Imprime las consultas SQL en consola |
    | `spring.jpa.properties.hibernate.dialect` | `MySQLDialect` | Dialecto SQL específico de MySQL |

### Cómo funciona el flujo

1. El **config-server** (puerto 8888) lee los archivos `.yaml` del directorio `config-data/`.
2. El **inventory-service** incluye la dependencia `spring-cloud-starter-config` y en su `application.yaml` local declara:
   ```yaml
   spring:
     config:
       import: optional:configserver:http://localhost:8888
   ```
3. Al arrancar, el inventory-service llama al config-server con `GET /inventory-service/default`, recibe el JSON anterior y usa esas propiedades con **mayor prioridad** que las locales.
4. `optional:` permite que el servicio arranque incluso si el config-server está caído (usa su config local como respaldo).

### Nota importante

El `spring.config.import: configserver:...` va en el `application.yaml` **local del cliente** (en el module `inventory-service/src/main/resources/`), **no** en `config-data/inventory-service.yaml`. Si se pone dentro del archivo servido, el config-server reprocesa el import y se llama a sí mismo, causando un deadlock.

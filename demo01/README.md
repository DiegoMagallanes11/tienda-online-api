# tienda-online-api

API REST para gestión de productos y categorías desarrollada con Spring Boot.

## Tecnologías

- Java 17
- Spring Boot 3.5.6
- Spring Data JPA
- MySQL
- Maven

## Endpoints

### Categorías
```
GET    /api/categorias
POST   /api/categorias
GET    /api/categorias/{id}
PUT    /api/categorias/{id}
DELETE /api/categorias/{id}
```

### Productos
```
GET    /api/productos
POST   /api/productos
GET    /api/productos/{id}
PUT    /api/productos/{id}
DELETE /api/productos/{id}
```

## Configuración

Cambiar la configuración en `application.yml` para MySQL:

```yaml
spring.datasource.url=jdbc:mysql://localhost:3306/tienda_db
spring.datasource.username=root
spring.datasource.password=tu_password
spring.jpa.hibernate.ddl-auto=update
```

## Compilar y ejecutar

```
mvn clean install
mvn spring-boot:run
```

## Tests

```
mvn test
```

33 tests cubriendo service y controller layers.

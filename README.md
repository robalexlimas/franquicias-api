# Franquicias API – Implementación Clean Architecture con WebFlux

## Descripción General

Este proyecto implementa una API reactiva para la gestión de:
- Franquicias
- Sucursales
- Productos

Permite:
- Crear franquicias
- Agregar sucursales
- Agregar productos
- Actualizar nombres
- Actualizar stock
- Eliminar productos
- Consultar el producto con mayor stock por sucursal

La solución fue desarrollada siguiendo:
- Spring Boot con WebFlux
- Arquitectura Hexagonal (Clean Architecture)
- Persistencia reactiva con MongoDB
- Programación reactiva usando Project Reactor
- Pruebas unitarias con cobertura > 80%

---

## Arquitectura

Se implementa Clean Architecture (Arquitectura Hexagonal):

Separando claramente:
- Dominio
- Casos de uso
- Adaptadores
- Punto de arranque

---

## Estructura del Proyecto

Domain

Es el módulo más interno.

Contiene:
- Entidades de dominio (Franchise, Branch, Product)
- DTOs de dominio
- Puertos (FranchiseGateway)

Aquí no existe ninguna dependencia hacia frameworks.

---

## Usecases

Implementa la lógica de negocio.

Cada caso de uso encapsula una operación:
- CreateFranchiseUseCase
- AddBranchUseCase
- AddProductUseCase
- UpdateProductStockUseCase
- UpdateProductNameUseCase
- UpdateBranchNameUseCase
- UpdateFranchiseNameUseCase
- DeleteProductUseCase
- TopProductByBranchUseCase

Se mantiene:
- Independencia de infraestructura
- Orquestación reactiva de flujos
- Separación clara de responsabilidades

---

## Infrastructure

### Helpers

Contiene clases reutilizables para adaptadores.

Se usa patrón tipo Repository + Unit of Work genérico para desacoplar lógica común.

### Driven Adapters

Se implementa:

MongoFranchiseAdapter

Este adaptador implementa el puerto FranchiseGateway y conecta el dominio con MongoDB Reactive.

Incluye:
- Validaciones de integridad
- Manejo de duplicados
- Encadenamiento reactivo con flatMap, switchIfEmpty, map

### Entry Points

Implementado con:
- Spring WebFlux
- RouterFunction
- Handler reactivo

Se utiliza programación funcional en lugar de controllers tradicionales.

Los errores se manejan centralmente en el Handler usando:

```java
.onErrorResume(this::mapError)
```

### Application

Este módulo:
- Ensambla los módulos
- Realiza el component scan
- Contiene el main()
- Inicializa la aplicación

Los casos de uso se registran automáticamente como Beans.

### Programación Reactiva

Se utilizan operadores:
- map
- flatMap
- switchIfEmpty
- flatMapMany
- then
- onErrorResume

Se manejan correctamente las señales:
- onNext
- onError
- onComplete

Ejemplo:

```java
productRepo.findById(productId)
    .switchIfEmpty(Mono.error(new IllegalArgumentException("Product not found")))
    .flatMap(existing -> productRepo.save(...))
    .map(this::toDomain);
```

Toda la aplicación es no bloqueante.

### Persistencia

Se usa:
- MongoDB Reactive
- Spring Data MongoDB Reactive

El sistema puede conectarse fácilmente a:
- MongoDB Atlas (Cloud)
- Instancia local
- Contenedor Docker

No se incluyó despliegue en nube por limitaciones de tiempo del ejercicio, pero la arquitectura está preparada para ello mediante variables de entorno.

## API RESTful

Base path:

```bash
/api
```

Endpoints

<table>
  <thead>
    <tr>
      <th>Método</th>
      <th>Endpoint</th>
      <th>Descripción</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>POST</td>
      <td><code>/api/franchises</code></td>
      <td>Crear franquicia</td>
    </tr>
    <tr>
      <td>PATCH</td>
      <td><code>/api/franchises/{id}/name</code></td>
      <td>Actualizar nombre franquicia</td>
    </tr>
    <tr>
      <td>POST</td>
      <td><code>/api/franchises/{id}/branches</code></td>
      <td>Agregar sucursal</td>
    </tr>
    <tr>
      <td>PATCH</td>
      <td><code>/api/branches/{id}/name</code></td>
      <td>Actualizar nombre sucursal</td>
    </tr>
    <tr>
      <td>POST</td>
      <td><code>/api/branches/{id}/products</code></td>
      <td>Agregar producto</td>
    </tr>
    <tr>
      <td>PATCH</td>
      <td><code>/api/products/{id}/stock</code></td>
      <td>Actualizar stock</td>
    </tr>
    <tr>
      <td>PATCH</td>
      <td><code>/api/products/{id}/name</code></td>
      <td>Actualizar nombre producto</td>
    </tr>
    <tr>
      <td>DELETE</td>
      <td><code>/api/products/{id}</code></td>
      <td>Eliminar producto</td>
    </tr>
    <tr>
      <td>GET</td>
      <td><code>/api/franchises/{id}/top-products</code></td>
      <td>Producto con mayor stock por sucursal</td>
    </tr>
  </tbody>
</table>

Cumple principios REST:
- Recursos bien definidos
- Uso correcto de HTTP verbs
- Códigos de estado adecuados
- JSON como formato estándar

> ℹ️ **Nota:**  
> La documentación interactiva de la API está disponible mediante **Swagger UI** en la ruta `/swagger-ui/index.html` una vez la aplicación se encuentra en ejecución.

---

## Pruebas Unitarias

Incluye pruebas para:
- Casos de uso
- Adaptadores Mongo
- Router
- Handler
- Configuraciones

Cobertura Global

Cobertura actual:
- 81% instrucciones
- 74% branches

Ejecutar pruebas

```bash
./gradlew clean test
```

Generar reporte global

```bash
./gradlew jacocoMergedReport
```

Reporte en:


```
build/reports/jacocoMergedReport/html/index.html
```

## Logging

Se utiliza:
- SLF4J (Spring Boot default logging)

Se registran:
- Errores de negocio
- Validaciones
- Eventos relevantes

## Docker (Plus)

El proyecto incluye un `Dockerfile` dentro de `deployment/` para empaquetar y ejecutar la aplicación en un contenedor.

1. Compilar y generar el JAR

> Nota: el `Dockerfile` asume que existe un JAR en `applications/app-service/build/libs/`.

Desde la raíz del proyecto:

```bash
./gradlew clean build
```

Valida que el JAR exista:

```bash
ls -la applications/app-service/build/libs/
```

Deberías ver un archivo tipo:

```bash
app-service-<version>.jar (o similar)
```

2. Construir la imagen Docker

Desde la raíz del proyecto:

```bash
docker build -f deployment/Dockerfile -t franquicias-api:latest .
```

Verifica que la imagen quedó creada:

```bash
docker images | grep franquicias-api
```

3. Ejecutar el contenedor

La aplicación requiere la variable de entorno ```SPRING_DATA_MONGODB_URI``` para conectarse a MongoDB (por ejemplo Atlas).

Ejemplo:

```bash
docker run --rm --name franquicias-api \
  -p 8080:8080 \
  -e SPRING_DATA_MONGODB_URI="mongodb+srv://<user>:<pass>@<cluster>/<db>?retryWrites=true&w=majority" \
  franquicias-api:latest
```

4. Validación rápida
- Swagger UI:
- http://localhost:8080/swagger-ui.html

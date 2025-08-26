# Order Services - Hexagonal Architecture

Este proyecto implementa el servicio de órdenes usando **Arquitectura Hexagonal (Ports & Adapters)** y **Domain-Driven Design (DDD)**.

## 🏗️ Estructura del Proyecto

```
src/main/java/com/food/ordering/system/order/
├── adapter/                          # Adaptadores (Puertos)
│   ├── inbound/                      # Puertos de entrada
│   │   ├── rest/                     # Adaptador REST
│   │   │   ├── advice/              # Manejo global de excepciones
│   │   │   ├── spec/                # Especificaciones OpenAPI/Swagger
│   │   │   ├── controller/          # Controladores REST
│   │   │   ├── dto/                 # DTOs de entrada/salida REST
│   │   │   └── mapper/              # Mapeadores REST ↔ Application
│   │   └── messaging/               # Adaptadores de mensajería
│   │       ├── kafka/               # Consumidores Kafka
│   │       └── mq/                  # Consumidores Message Queue
│   └── outbound/                    # Puertos de salida
│       ├── persistence/             # Adaptador de persistencia (H2)
│       │   ├── entity/              # Entidades JPA
│       │   ├── mapper/              # Mapeadores Domain ↔ Entity
│       │   └── repository/          # Implementaciones de Application Ports Out
│       ├── memory/                  # Adaptador en memoria (testing)
│       └── messaging/               # Productores de mensajería
│           ├── kafka/               # Productores Kafka
│           └── mq/                  # Productores Message Queue
├── application/                      # Capa de Aplicación
│   ├── command/                     # DTOs de comandos (intención de cambio)
│   ├── dto/                         # DTOs de transferencia de datos
│   ├── query/                       # Records para consultas parametrizadas
│   ├── port/                        # Interfaces de puertos
│   │   ├── in/                      # Puertos de entrada
│   │   └── out/                     # Puertos de salida
│   └── services/                    # Implementaciones de casos de uso
├── domain/                          # Capa de Dominio
│   ├── event/                       # Eventos del dominio
│   ├── exception/                   # Excepciones del dominio
│   ├── model/                       # Modelo del dominio
│   │   ├── entities/                # Entidades del dominio
│   │   ├── valueobjects/            # Objetos de valor
│   │   └── aggregates/              # Agregados
│   └── service/                     # Servicios del dominio
└── infrastructure/                  # Configuración
    └── config/                      # Configuraciones (Swagger, WebFlux, etc.)
```

## 🎯 Principios de Diseño

### Arquitectura Hexagonal
- **Puertos de Entrada**: Interfaces que definen cómo la aplicación recibe datos
- **Puertos de Salida**: Interfaces que definen cómo la aplicación envía datos
- **Adaptadores**: Implementaciones concretas de los puertos

### Domain-Driven Design
- **Entidades**: Objetos con identidad única
- **Objetos de Valor**: Objetos inmutables sin identidad
- **Agregados**: Conjuntos de entidades relacionadas
- **Servicios de Dominio**: Lógica de negocio que no pertenece a una entidad específica

## 🚀 Tecnologías

- **Spring Boot 3.2.0**
- **Java 17**
- **Spring Data JPA**
- **H2 Database**
- **Flyway Migration**
- **Spring Kafka**
- **OpenAPI/Swagger**
- **MapStruct**
- **Lombok**

## 📋 Funcionalidades

- ✅ Crear órdenes
- ✅ Consultar órdenes por ID
- ✅ Listar todas las órdenes
- ✅ Consultar órdenes por cliente
- ✅ Gestión de estados de órdenes
- ✅ Integración con Kafka
- ✅ API REST documentada

## 🔧 Configuración

### Base de Datos
- **H2 in-memory**: `jdbc:h2:mem:orderdb`
- **Console**: http://localhost:8081/api/v1/h2-console

### API REST
- **Base URL**: http://localhost:8081/api/v1
- **Swagger UI**: http://localhost:8081/api/v1/swagger-ui.html

### Kafka
- **Bootstrap Servers**: localhost:9092
- **Consumer Group**: order-service-group

## 🏃‍♂️ Ejecución

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar la aplicación
mvn spring-boot:run

# Ejecutar tests
mvn test
```

## 📚 Endpoints

### Orders
- `POST /orders` - Crear nueva orden
- `GET /orders/{orderId}` - Obtener orden por ID
- `GET /orders` - Listar todas las órdenes
- `GET /orders/customer/{customerId}` - Órdenes por cliente

## 🔄 Flujo de Datos

1. **REST Controller** → **Command DTO** → **Use Case Service** → **Domain Entity** → **Repository Interface** → **Repository Implementation** → **Database**

2. **Kafka Consumer** → **Command DTO** → **Use Case Service** → **Domain Event** → **Kafka Producer** → **Message Queue**

## 🧪 Testing

El proyecto incluye:
- Tests unitarios para el dominio
- Tests de integración para adaptadores
- Tests de casos de uso
- Tests de API REST

## 📝 Notas

- Los errores de linter sobre los paquetes son normales en esta fase de desarrollo
- La estructura está preparada para escalar con nuevos adaptadores
- El dominio está completamente aislado de frameworks externos

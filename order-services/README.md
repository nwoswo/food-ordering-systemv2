# Order Services - Arquitectura Hexagonal

Este proyecto implementa el servicio de órdenes usando arquitectura hexagonal (puertos y adaptadores) con Spring Boot y Gradle.

## Estructura del Proyecto

```
src/main/java/com/food/ordering/system/order/service/
├── adapter/
│   ├── inbound/
│   │   └── rest/
│   │       ├── advice/          # Manejadores globales de excepciones
│   │       ├── controller/      # Controladores REST
│   │       ├── dto/            # DTOs para requests/responses
│   │       └── mapper/         # Mappers para conversión de datos
│   └── outbound/
│       └── persistence/        # Repositorios de persistencia
│           ├── entity/         # Entidades JPA
│           ├── mapper/         # Mappers de persistencia
│           └── repository/     # Implementaciones de repositorios
├── application/
│   ├── dto/                   # DTOs para transferencia de datos
│   ├── port/
│   │   ├── in/                # Puertos de entrada (interfaces)
│   │   └── out/               # Puertos de salida (interfaces)
│   └── services/              # Servicios de aplicación
├── domain/
│   ├── event/                 # Eventos del dominio
│   ├── exception/             # Excepciones del dominio
│   ├── model/
│   │   ├── entities/          # Entidades del dominio
│   │   ├── valueobjects/      # Objetos de valor
│   │   └── aggregates/        # Agregados
│   └── service/               # Servicios del dominio
└── infrastructure/
    └── config/                # Configuraciones (Spring Boot, etc.)
```

## Funcionalidades

### Gestión de Órdenes
- Creación de órdenes
- Seguimiento de órdenes
- Gestión de estados de órdenes
- Validación de órdenes

### Integración con Microservicios
- Comunicación con Payment Service
- Comunicación con Restaurant Service
- Comunicación con Customer Service

### Mensajería
- Productores Kafka para eventos de dominio
- Consumidores Kafka para respuestas de otros servicios
- Manejo de eventos de saga

### Persistencia
- Entidades JPA para órdenes, items y direcciones
- Repositorios con implementaciones personalizadas
- Mappers para conversión entre dominio y persistencia

## Endpoints

### Órdenes
- `POST /orders` - Crear una nueva orden
- `GET /orders/{trackingId}` - Obtener orden por tracking ID

### Health Check
- `GET /orders/health` - Estado del servicio

## Configuración

### Base de Datos
- H2 para desarrollo local
- PostgreSQL para producción
- Flyway para migraciones

### Kafka
- Configuración de productores y consumidores
- Topics para eventos de dominio
- Manejo de mensajes de respuesta

### Circuit Breaker
- Configuración con Resilience4j
- Fallbacks para servicios externos
- Timeouts y thresholds configurados

## Tecnologías

- Spring Boot 3.2.0
- Spring Data JPA
- Spring Kafka
- Spring Cloud Circuit Breaker (Resilience4j)
- Flyway
- H2 / PostgreSQL
- Lombok
- MapStruct
- Gradle

## Arquitectura

### Dominio
- **Entidades**: Order, OrderItem, Product, Customer, Restaurant
- **Value Objects**: Money, OrderItemId, TrackingId, StreetAddress
- **Agregados**: Order (agregado raíz)
- **Eventos**: OrderCreatedEvent, OrderPaidEvent, OrderCancelledEvent
- **Servicios**: OrderDomainService

### Aplicación
- **Puertos de Entrada**: OrderApplicationService
- **Puertos de Salida**: OrderRepository, CustomerRepository, RestaurantRepository
- **Servicios**: OrderApplicationServiceImpl, OrderCreateCommandHandler, OrderTrackCommandHandler

### Adaptadores
- **Inbound REST**: OrderController, OrderGlobalExceptionHandler
- **Outbound Persistence**: OrderRepositoryImpl, CustomerRepositoryImpl, RestaurantRepositoryImpl
- **Outbound Messaging**: Kafka productores y consumidores

## Saga Pattern

El servicio implementa el patrón Saga para manejar transacciones distribuidas:

1. **Order Saga**: Maneja la creación y pago de órdenes
2. **Payment Saga**: Maneja las respuestas de pago
3. **Restaurant Saga**: Maneja las respuestas de aprobación del restaurante

## Eventos de Dominio

- `OrderCreatedEvent`: Cuando se crea una nueva orden
- `OrderPaidEvent`: Cuando se confirma el pago
- `OrderCancelledEvent`: Cuando se cancela la orden
- `OrderApprovalSaga`: Manejo de la saga de aprobación

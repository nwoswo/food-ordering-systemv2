# API Gateway - Arquitectura Hexagonal

Este proyecto implementa un API Gateway usando Spring Cloud Gateway con arquitectura hexagonal (puertos y adaptadores).

## Estructura del Proyecto

```
src/main/java/com/food/ordering/system/gateway/
├── adapter/
│   ├── inbound/
│   │   ├── rest/
│   │   │   ├── advice/          # Manejadores globales de excepciones
│   │   │   ├── controller/      # Controladores REST
│   │   │   ├── dto/            # DTOs para requests/responses
│   │   │   ├── mapper/         # Mappers para conversión de datos
│   │   │   └── spec/           # Especificaciones de API
│   │   └── messaging/
│   │       ├── kafka/          # Consumidores Kafka
│   │       └── mq/             # Consumidores de colas de mensajes
│   └── outbound/
│       ├── persistence/        # Repositorios de persistencia
│       ├── memory/             # Repositorios en memoria
│       └── messaging/
│           ├── kafka/          # Productores Kafka
│           └── mq/             # Productores de colas de mensajes
├── application/
│   ├── command/               # DTOs para comandos (crear, actualizar)
│   ├── dto/                   # DTOs para transferencia de datos
│   ├── query/                 # Records para consultas
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
    └── config/                # Configuraciones (Swagger, WebFlux, etc.)
```

## Funcionalidades

### Enrutamiento
- Enrutamiento de requests a microservicios
- Configuración de rutas con predicados y filtros
- Strip prefix para limpiar URLs

### Circuit Breaker
- Implementación con Resilience4j
- Fallbacks para cada servicio
- Configuración de timeouts y thresholds

### Health Checks
- Endpoints de salud para cada servicio
- Monitoreo del estado del gateway

### Manejo de Errores
- Global exception handler
- Respuestas de fallback estructuradas
- Logging centralizado

## Endpoints

### Fallback Endpoints
- `GET /fallback/order-service` - Fallback para Order Service
- `GET /fallback/payment-service` - Fallback para Payment Service
- `GET /fallback/restaurant-service` - Fallback para Restaurant Service
- `GET /fallback/customer-service` - Fallback para Customer Service
- `GET /fallback/health` - Estado del gateway

### Health Check Endpoints
- `GET /api/health/orders` - Health check de Order Service
- `GET /api/health/payments` - Health check de Payment Service
- `GET /api/health/restaurants` - Health check de Restaurant Service
- `GET /api/health/customers` - Health check de Customer Service

## Configuración

### Circuit Breaker
- Sliding window size: 10
- Failure rate threshold: 50%
- Wait duration in open state: 10 seconds
- Timeout: 3 seconds

### Rutas Configuradas
- Order Service: `http://order-service:8181`
- Payment Service: `http://payment-service:8182`
- Restaurant Service: `http://restaurant-service:8183`
- Customer Service: `http://customer-service:8184`

## Tecnologías

- Spring Cloud Gateway
- Spring Cloud Circuit Breaker (Resilience4j)
- Spring Boot Actuator
- Spring Boot DevTools
- WebFlux (Reactivo)
- Lombok

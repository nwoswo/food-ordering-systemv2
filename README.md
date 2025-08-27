# Food Ordering System v2

Sistema de pedidos de comida basado en microservicios con arquitectura hexagonal, patron Saga, y patrón Outbox con Debezium.

## Servicios

- **order-services**: Gestión de órdenes con patrón Saga
- **customer-services**: Gestión de clientes
- **payment-services**: Gestión de pagos
- **restaurant-services**: Gestión de restaurantes
- **api-gateway**: Gateway de API
- **consulta-services**: Servicio de consulta de eventos de órdenes (nuevo)
- **kafka-infrastructure**: Infraestructura de Kafka y Debezium

## Tecnologías

- **Java 17**
- **Spring Boot 3.2.0**
- **Gradle**
- **PostgreSQL**
- **Apache Kafka**
- **Debezium**
- **Docker & Docker Compose**

## Patrones Implementados

- **Arquitectura Hexagonal (Clean Architecture)**
- **Patrón Saga** para transacciones distribuidas
- **Patrón Outbox** con Debezium para mensajería confiable
- **Domain-Driven Design (DDD)**

## Despliegue

### Prerrequisitos
- Docker y Docker Compose
- Java 17
- Gradle

### Compilación y Despliegue

**Opción 1: Despliegue completo con un comando**
```bash
./start-full-stack.sh
```

**Opción 2: Despliegue paso a paso**

1. **Generar imágenes Docker:**
```bash
./build-docker-images.sh
```

2. **Levantar stack completo:**
```bash
./start-full-stack.sh
```

**Opción 3: Despliegue manual**

1. **Compilar el proyecto:**
```bash
./gradlew clean build -x test
```

2. **Generar imágenes Docker:**
```bash
./build-and-docker.sh
```

3. **Levantar infraestructura:**
```bash
cd kafka-infrastructure && docker compose up -d
```

4. **Configurar Debezium:**
```bash
cd kafka-infrastructure && ./setup-debezium-outbox.sh
```

5. **Levantar servicios:**
```bash
cd kafka-infrastructure && docker compose -f docker-compose-services.yml up -d
```

## Consulta Service

El servicio `consulta-services` es un nuevo microservicio que:

- Consume eventos del topic `order-OrderCreatedEvent` de Kafka
- Almacena los eventos en la base de datos
- Proporciona endpoints REST para consultar información de órdenes

### Endpoints Principales

- `GET /api/v1/consulta/order/{orderId}` - Consultar orden por ID
- `GET /api/v1/consulta/orders` - Obtener todas las órdenes
- `GET /api/v1/consulta/health` - Health check

### Pruebas

```bash
./test-consulta-service.sh
```

## Estructura del Proyecto

```
food-ordering-systemv2/
├── order-services/          # Servicio de órdenes
├── customer-services/       # Servicio de clientes
├── payment-services/        # Servicio de pagos
├── restaurant-services/     # Servicio de restaurantes
├── api-gateway/            # Gateway de API
├── consulta-services/      # Servicio de consulta (nuevo)
├── common-libraries/       # Bibliotecas comunes
├── kafka-infrastructure/   # Infraestructura de Kafka
├── build-and-docker.sh     # Script de build y Docker
├── build-docker-images.sh  # Script de generación de imágenes
└── start-full-stack.sh     # Script de despliegue completo
```

## Configuración de Debezium

El servicio consume eventos del topic `order-OrderCreatedEvent` que es generado por Debezium desde la tabla `orden.outbox_events` del servicio de órdenes.

La configuración del conector Debezium se encuentra en:
- `kafka-infrastructure/debezium-order-outbox-connector.json`
- `kafka-infrastructure/setup-debezium-outbox.sh`

El conector Debezium usa el transform `EventRouter` para enrutar eventos por tipo, generando topics como `order-OrderCreatedEvent`.

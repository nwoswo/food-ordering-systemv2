# Consulta Service

Este microservicio es responsable de consumir eventos del topic `order-OrderCreatedEvent` de Kafka (generado por Debezium desde la tabla `orden.outbox_events`) y proporcionar endpoints REST para consultar la información de las órdenes.

## Funcionalidades

- **Consumo de eventos**: Escucha el topic `order-OrderCreatedEvent` de Kafka
- **Almacenamiento**: Guarda los eventos en la tabla `consulta.order_outbox`
- **Consultas REST**: Proporciona endpoints para consultar información de órdenes

## Endpoints Disponibles

### GET `/api/v1/consulta/order/{orderId}`
Consulta una orden específica por su ID.

**Ejemplo:**
```bash
curl http://localhost:8185/api/v1/consulta/order/123e4567-e89b-12d3-a456-426614174000
```

### GET `/api/v1/consulta/orders`
Obtiene todas las órdenes almacenadas.

**Ejemplo:**
```bash
curl http://localhost:8185/api/v1/consulta/orders
```

### GET `/api/v1/consulta/orders/aggregate/{aggregateId}`
Consulta órdenes por ID de agregado.

**Ejemplo:**
```bash
curl http://localhost:8185/api/v1/consulta/orders/aggregate/123e4567-e89b-12d3-a456-426614174000
```

### GET `/api/v1/consulta/orders/event-type/{eventType}`
Consulta órdenes por tipo de evento.

**Ejemplo:**
```bash
curl http://localhost:8185/api/v1/consulta/orders/event-type/OrderCreatedEvent
```

### GET `/api/v1/consulta/orders/processed/{processed}`
Consulta órdenes por estado de procesamiento (true/false).

**Ejemplo:**
```bash
curl http://localhost:8185/api/v1/consulta/orders/processed/false
```

### GET `/api/v1/consulta/health`
Endpoint de salud del servicio.

**Ejemplo:**
```bash
curl http://localhost:8185/api/v1/consulta/health
```

## Configuración

### Base de Datos
- **Esquema**: `consulta`
- **Tabla**: `order_outbox`
- **Puerto**: 8185

### Kafka
- **Topic**: `order-OrderCreatedEvent`
- **Group ID**: `consulta-service-group`

## Estructura del Proyecto

```
consulta-services/
├── src/main/java/com/food/ordering/system/consulta/
│   ├── ConsultaServiceApplication.java
│   ├── dataaccess/
│   │   ├── entity/
│   │   │   └── OrderOutboxEntity.java
│   │   └── repository/
│   │       └── OrderOutboxRepository.java
│   └── infrastructure/
│       ├── kafka/
│       │   └── OrderOutboxKafkaConsumer.java
│       └── rest/
│           └── ConsultaController.java
└── src/main/resources/
    ├── application.yml
    └── bd/
        └── V1__init_consulta_schema.sql
```

## Despliegue

### Compilación
```bash
./gradlew :consulta-services:build
```

### Docker
```bash
docker build -t consulta-service:latest consulta-services/ --build-arg JAR_FILE=consulta-service-1.0.0.jar
```

### Docker Compose
El servicio está incluido en `kafka-infrastructure/docker-compose-services.yml`:

```yaml
consulta-service:
  image: consulta-service:latest
  container_name: consulta-service
  ports:
    - "8185:8185"
  environment:
    - POSTGRES_HOST=postgres-db
    - KAFKA_BOOTSTRAP_SERVERS=kafka-broker-1:9092,kafka-broker-2:9092,kafka-broker-3:9092
```

## Configuración de Debezium

El servicio consume eventos del topic `order-OrderCreatedEvent` que es generado por Debezium desde la tabla `orden.outbox_events` del servicio de órdenes.

La configuración del conector Debezium se encuentra en:
- `kafka-infrastructure/debezium-config/order-outbox-connector.json`
- `kafka-infrastructure/setup-debezium.sh`

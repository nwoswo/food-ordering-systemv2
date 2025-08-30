# Food Ordering System - Kubernetes Deployment

Este directorio contiene todos los archivos necesarios para desplegar el **Food Ordering System** en Kubernetes usando Kind.

## 📁 Estructura de Archivos

### Archivos Principales
- **`deploy.sh`** - Script principal de despliegue completo
- **`cleanup.sh`** - Script de limpieza de recursos
- **`kind-config.yaml`** - Configuración del cluster Kind

### Archivos de Infraestructura Consolidados
- **`configmaps.yaml`** - Todos los ConfigMaps (PostgreSQL, Debezium, Kafka Connect)
- **`infrastructure.yaml`** - Infraestructura base (PostgreSQL, Kafka brokers, Kafka UI)
- **`kafka-connect.yaml`** - Kafka Connect deployment y service
- **`jobs.yaml`** - Jobs de inicialización (tópicos Kafka, conector Debezium)
- **`microservices-complete.yaml`** - Todos los microservicios

## 🚀 Despliegue Rápido

### Prerequisitos
- Docker
- Kind
- kubectl
- Gradle (para construir microservicios)

### Despliegue Completo
```bash
# Dar permisos de ejecución
chmod +x deploy.sh cleanup.sh

# Desplegar todo el sistema
./deploy.sh
```

### Limpieza
```bash
# Limpiar recursos de Kubernetes y Kind
./cleanup.sh

# Limpiar también imágenes Docker
./cleanup.sh --clean-images
```

## 📋 Componentes Desplegados

### Infraestructura Base
- **PostgreSQL 13** - Base de datos principal
- **Kafka 3-node KRaft Cluster** - Sin Zookeeper
- **Kafka UI** - Interfaz web para administrar Kafka
- **MetalLB** - Load balancer para Kind

### Kafka Connect & Debezium
- **Kafka Connect** - Plataforma de conectores
- **Debezium PostgreSQL Connector** - CDC para outbox pattern
- **Tópicos automáticos** - Configurados con `cleanup.policy=compact`

### Microservicios
- **Order Service** - Gestión de órdenes
- **Payment Service** - Procesamiento de pagos
- **Restaurant Service** - Gestión de restaurantes
- **Customer Service** - Gestión de clientes
- **Consulta Service** - Consultas agregadas
- **API Gateway** - Gateway principal

## 🔗 Acceso a Servicios

### Kafka UI
```bash
kubectl port-forward -n kafka-infrastructure service/kafka-ui 8090:8080
# http://localhost:8090
```

### Kafka Connect
```bash
kubectl port-forward -n kafka-infrastructure service/kafka-connect 8083:8083
# http://localhost:8083
```

### API Gateway
```bash
kubectl get svc -n kafka-infrastructure api-gateway
# El servicio es LoadBalancer, obtendrá IP de MetalLB
```

## 📊 Monitoreo

### Verificar Estado
```bash
# Ver todos los pods
kubectl get pods -n kafka-infrastructure

# Ver todos los servicios
kubectl get svc -n kafka-infrastructure

# Ver logs de Kafka Connect
kubectl logs -n kafka-infrastructure -l app=kafka-connect

# Ver tópicos de Kafka
kubectl exec -n kafka-infrastructure kafka-broker-1-xxx -- kafka-topics --list --bootstrap-server kafka-broker-1:9092
```

### Verificar Conector Debezium
```bash
# Verificar estado del conector
kubectl exec -n kafka-infrastructure kafka-connect-xxx -- curl -s http://localhost:8083/connectors/order-outbox-connector/status

# Ver logs del job de registro
kubectl logs -n kafka-infrastructure job/debezium-connector-setup
```

## 🔧 Configuración

### Variables de Entorno Importantes
- **KAFKA_BOOTSTRAP_SERVERS**: `kafka-broker-1:9092,kafka-broker-2:9092,kafka-broker-3:9092`
- **POSTGRES_HOST**: `postgres`
- **POSTGRES_PORT**: `5432`
- **POSTGRES_DB**: `postgres`
- **POSTGRES_USER**: `postgres`
- **POSTGRES_PASSWORD**: `admin`

### Recursos Asignados
- **Kafka Brokers**: 1Gi-2Gi RAM, 500m-1000m CPU
- **Kafka Connect**: 2Gi-4Gi RAM, 500m-1000m CPU
- **PostgreSQL**: 512Mi-1Gi RAM, 250m-500m CPU
- **Microservicios**: 512Mi-1Gi RAM, 250m-500m CPU

## 🐛 Troubleshooting

### Problemas Comunes

#### Kafka Connect no se conecta a brokers
```bash
# Verificar conectividad desde Kafka Connect
kubectl exec -n kafka-infrastructure kafka-connect-xxx -- curl -s http://localhost:8083/connectors

# Verificar logs de Kafka Connect
kubectl logs -n kafka-infrastructure -l app=kafka-connect --tail=50
```

#### Microservicios no inician
```bash
# Verificar logs del microservicio
kubectl logs -n kafka-infrastructure -l app=order-service

# Verificar conectividad a PostgreSQL
kubectl exec -n kafka-infrastructure order-service-xxx -- nc -zv postgres 5432
```

#### Conector Debezium no se registra
```bash
# Verificar que Kafka Connect esté listo
kubectl get pods -n kafka-infrastructure -l app=kafka-connect

# Re-ejecutar job de registro
kubectl delete job debezium-connector-setup -n kafka-infrastructure
kubectl apply -f jobs.yaml
```

## 📝 Notas Importantes

1. **Persistencia**: Los datos se almacenan en `emptyDir` (se pierden al reiniciar pods)
2. **Red**: Todos los servicios usan `ClusterIP` excepto API Gateway que usa `LoadBalancer`
3. **Seguridad**: Configuración básica sin TLS/SASL para desarrollo
4. **Escalabilidad**: Configurado para desarrollo local, no producción

## 🔄 Actualizaciones

Para actualizar un componente específico:

```bash
# Actualizar solo microservicios
kubectl apply -f microservices-complete.yaml

# Actualizar solo Kafka Connect
kubectl apply -f kafka-connect.yaml

# Actualizar solo infraestructura
kubectl apply -f infrastructure.yaml
```

## 📚 Referencias

- [Kind Documentation](https://kind.sigs.k8s.io/)
- [Kafka KRaft Mode](https://kafka.apache.org/documentation/#kraft)
- [Debezium Documentation](https://debezium.io/documentation/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)

## 🔄 Orden de Despliegue

El script de despliegue ejecuta los componentes en el siguiente orden:

1. **Cluster Kind** - Crear cluster local
2. **MetalLB** - Instalar load balancer
3. **Imágenes** - Construir y cargar microservicios
4. **Infraestructura** - PostgreSQL y Kafka brokers
5. **Kafka Connect** - Plataforma de conectores
6. **Base de Datos** - Crear esquema y tabla `orden.outbox_events`
7. **Tópicos Kafka** - Inicializar tópicos de aplicación
8. **Conector Debezium** - Registrar conector para CDC
9. **Microservicios** - Desplegar todos los servicios

### ⚠️ Importante: Inicialización de Base de Datos

El conector Debezium **requiere** que la tabla `orden.outbox_events` exista antes de registrarse. El job `db-init` crea:

```sql
-- Habilitar extensión para UUIDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA public;

-- Esquema
CREATE SCHEMA IF NOT EXISTS orden;

-- Tabla outbox_events
CREATE TABLE IF NOT EXISTS orden.outbox_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_id VARCHAR(255) NOT NULL,
    aggregate_type VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    event_data JSONB NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP WITH TIME ZONE NULL
);

-- Índices para rendimiento
CREATE INDEX IF NOT EXISTS idx_outbox_events_aggregate_id ON orden.outbox_events(aggregate_id);
CREATE INDEX IF NOT EXISTS idx_outbox_events_created_at ON orden.outbox_events(created_at);
CREATE INDEX IF NOT EXISTS idx_outbox_events_processed_at ON orden.outbox_events(processed_at);
```

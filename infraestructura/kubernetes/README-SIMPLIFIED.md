# Kafka Infrastructure - Kubernetes Deployment (Simplified)

Esta es la versión simplificada del despliegue de la infraestructura Kafka en Kubernetes, con archivos consolidados para facilitar el manejo.

## 📁 Estructura Final Limpia

```
kubernetes/
├── postgres-complete.yaml              # ✅ PostgreSQL completo (todo en uno)
├── kafka-complete.yaml                 # ✅ Kafka completo (todo en uno)
├── kafka-init-job.yaml                 # Job para inicializar topics
├── kafka-connect-deployment.yaml       # Kafka Connect
├── kafka-connect-service.yaml          # Servicio Kafka Connect
├── kafka-ui-complete.yaml              # ✅ Kafka UI completo (todo en uno)
├── debezium-connector-configmap.yaml   # Configuración del conector
├── debezium-connector-job.yaml         # Job para registrar el conector
├── deploy-simple.sh                    # ✅ Script simplificado
├── cleanup.sh                          # Script de limpieza
├── README.md                           # Documentación original (para referencia)
└── README-SIMPLIFIED.md                # Este archivo
```

## 🚀 Despliegue Rápido

### Opción 1: Despliegue Simplificado (Recomendado)
```bash
cd infraestructura/kubernetes
./deploy-simple.sh
```

### Opción 2: Despliegue Manual
```bash
# 1. PostgreSQL (todo en uno)
kubectl apply -f postgres-complete.yaml

# 2. Kafka (todo en uno)
kubectl apply -f kafka-complete.yaml

# 3. Inicialización y servicios adicionales
kubectl apply -f kafka-init-job.yaml
kubectl apply -f kafka-connect-deployment.yaml
kubectl apply -f kafka-connect-service.yaml
kubectl apply -f debezium-connector-configmap.yaml
kubectl apply -f debezium-connector-job.yaml
kubectl apply -f kafka-ui-complete.yaml
```

## 🔧 Ventajas de la Versión Simplificada

### ✅ **Archivos Consolidados**
- **`postgres-complete.yaml`**: Namespace + ConfigMap + Secret + PVC + Deployment + Service
- **`kafka-complete.yaml`**: ConfigMap + PVCs + 3 Brokers + Services
- **`kafka-ui-complete.yaml`**: Deployment + Service
- **Solo 13 archivos** en lugar de 25+

### ✅ **Script Simplificado**
- **`deploy-simple.sh`**: Despliegue más directo
- Menos pasos manuales
- Mejor manejo de errores

### ✅ **Manejo Mejorado del Conector Debezium**
- Verificación de existencia del archivo JSON
- Mejor logging y debugging
- Manejo robusto de errores

## 📊 Verificación del Despliegue

### Estado General
```bash
kubectl get all -n kafka-infrastructure
```

### Logs del Conector Debezium
```bash
kubectl logs -n kafka-infrastructure job/debezium-connector-setup
```

### Estado del Conector
```bash
# Port-forward para acceder a Kafka Connect
kubectl port-forward -n kafka-infrastructure service/kafka-connect 8083:8083

# Verificar estado del conector
curl http://localhost:8083/connectors/order-outbox-connector/status
```

## 🔍 Troubleshooting

### Problema: El job de Debezium falla
```bash
# Verificar que el ConfigMap existe
kubectl get configmap debezium-connector-config -n kafka-infrastructure

# Verificar el contenido del ConfigMap
kubectl get configmap debezium-connector-config -n kafka-infrastructure -o yaml

# Ver logs del job
kubectl logs -n kafka-infrastructure job/debezium-connector-setup
```

### Problema: Kafka Connect no está listo
```bash
# Verificar estado del deployment
kubectl get deployment kafka-connect -n kafka-infrastructure

# Ver logs
kubectl logs -n kafka-infrastructure deployment/kafka-connect

# Verificar que el servicio responde
kubectl port-forward -n kafka-infrastructure service/kafka-connect 8083:8083
curl http://localhost:8083/
```

## 🌐 Acceso a Servicios

### Kafka UI
```bash
kubectl port-forward -n kafka-infrastructure service/kafka-ui 8090:8090
# Acceder a: http://localhost:8090
```

### Kafka Connect REST API
```bash
kubectl port-forward -n kafka-infrastructure service/kafka-connect 8083:8083
# Acceder a: http://localhost:8083
```

## 🧹 Limpieza

```bash
./cleanup.sh
```

## 📝 Notas Importantes

1. **ConfigMap del Conector**: El archivo JSON se monta correctamente desde el ConfigMap
2. **Verificación de Archivos**: El job verifica que el archivo JSON existe antes de usarlo
3. **Logging Mejorado**: Mejor visibilidad de lo que está pasando durante el despliegue
4. **Manejo de Errores**: El job maneja mejor los casos de error y reintentos
5. **Estructura Limpia**: Solo los archivos necesarios, sin duplicados

## 🔄 Migración desde la Versión Original

Si ya tienes la versión original desplegada:

```bash
# 1. Limpiar la versión anterior
./cleanup.sh

# 2. Desplegar la versión simplificada
./deploy-simple.sh
```

La versión simplificada es compatible con la original y usa los mismos recursos de Kubernetes.

## 📋 Archivos Eliminados

Los siguientes archivos fueron eliminados porque están consolidados:

### PostgreSQL (consolidados en `postgres-complete.yaml`)
- ❌ `namespace.yaml`
- ❌ `postgres-configmap.yaml`
- ❌ `postgres-secret.yaml`
- ❌ `postgres-pvc.yaml`
- ❌ `postgres-deployment.yaml`
- ❌ `postgres-service.yaml`

### Kafka (consolidados en `kafka-complete.yaml`)
- ❌ `kafka-configmap.yaml`
- ❌ `kafka-pvcs.yaml`
- ❌ `kafka-broker-1-deployment.yaml`
- ❌ `kafka-broker-2-deployment.yaml`
- ❌ `kafka-broker-3-deployment.yaml`
- ❌ `kafka-services.yaml`

### Kafka UI (consolidados en `kafka-ui-complete.yaml`)
- ❌ `kafka-ui-deployment.yaml`
- ❌ `kafka-ui-service.yaml`

### Scripts (obsoletos)
- ❌ `deploy.sh` (archivos referenciados no existen)

**Total eliminados: 15 archivos** 🎉

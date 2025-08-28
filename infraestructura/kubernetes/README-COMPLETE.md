# Kafka Infrastructure - Complete Deployment with Kind

Esta es la versión completa del despliegue que incluye la creación del cluster Kind y toda la infraestructura Kafka.

## 🏗️ **Arquitectura Completa**

### **Cluster Kind**
- **3 nodos**: 1 control-plane + 2 workers
- **Port mappings**: Acceso directo a servicios
- **Optimizado** para Kafka y PostgreSQL

### **Infraestructura**
- **PostgreSQL**: Base de datos con replicación lógica
- **Kafka Cluster**: 3 brokers con KRaft
- **Kafka Connect**: Para streaming de datos
- **Debezium Connector**: CDC para outbox pattern
- **Kafka UI**: Interfaz web de monitoreo

## 📁 **Estructura de Archivos**

```
kubernetes/
├── kind-config.yaml                    # ✅ Configuración del cluster Kind
├── postgres-complete.yaml              # ✅ PostgreSQL completo
├── kafka-complete.yaml                 # ✅ Kafka completo  
├── kafka-ui-complete.yaml              # ✅ Kafka UI completo
├── kafka-init-job.yaml                 # Job para inicializar topics
├── kafka-connect-deployment.yaml       # Kafka Connect
├── kafka-connect-service.yaml          # Servicio Kafka Connect
├── debezium-connector-configmap.yaml   # Configuración del conector
├── debezium-connector-job.yaml         # Job para registrar el conector
├── deploy-with-kind.sh                 # ✅ Script completo con Kind
├── cleanup-with-kind.sh                # ✅ Limpieza completa
├── deploy-simple.sh                    # Script solo despliegue
├── cleanup.sh                          # Limpieza solo infraestructura
├── deploy.sh                           # Script original (referencia)
├── README.md                           # Documentación original
├── README-SIMPLIFIED.md                # Documentación simplificada
└── README-COMPLETE.md                  # Este archivo
```

## 🚀 **Despliegue Completo**

### **Prerrequisitos**
```bash
# Instalar Kind
curl -Lo ./kind https://kind.sigs.k8s.io/dl/v0.20.0/kind-linux-amd64
chmod +x ./kind
sudo mv ./kind /usr/local/bin/kind

# Instalar kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
chmod +x kubectl
sudo mv kubectl /usr/local/bin/

# Verificar Docker está corriendo
docker info
```

### **Despliegue Completo**
```bash
cd infraestructura/kubernetes
./deploy-with-kind.sh
```

## 🌐 **Acceso a Servicios**

### **Acceso Directo (via Kind port mappings)**
- **Kafka UI**: http://localhost:8090
- **Kafka Connect REST API**: http://localhost:8083
- **PostgreSQL**: localhost:5432 (si se necesita externamente)

### **Acceso via kubectl**
```bash
# Kafka UI
kubectl port-forward -n kafka-infrastructure service/kafka-ui 8090:8090

# Kafka Connect
kubectl port-forward -n kafka-infrastructure service/kafka-connect 8083:8083
```

## 🔧 **Configuración del Cluster Kind**

### **Características**
- **3 nodos**: Mejor distribución de recursos
- **Port mappings**: Acceso directo sin port-forward
- **Feature gates**: Habilitadas para mejor compatibilidad
- **Admission controllers**: Configurados para seguridad

### **Port Mappings**
```yaml
extraPortMappings:
- containerPort: 8090  # Kafka UI
  hostPort: 8090
- containerPort: 8083  # Kafka Connect
  hostPort: 8083
- containerPort: 5432  # PostgreSQL
  hostPort: 5432
```

## 📊 **Verificación del Despliegue**

### **Estado del Cluster**
```bash
# Información del cluster
kubectl cluster-info --context kind-kafka-infrastructure

# Nodos
kubectl get nodes

# Estado general
kubectl get all -n kafka-infrastructure
```

### **Logs de Servicios**
```bash
# PostgreSQL
kubectl logs -n kafka-infrastructure deployment/postgres

# Kafka brokers
kubectl logs -n kafka-infrastructure deployment/kafka-broker-1
kubectl logs -n kafka-infrastructure deployment/kafka-broker-2
kubectl logs -n kafka-infrastructure deployment/kafka-broker-3

# Kafka Connect
kubectl logs -n kafka-infrastructure deployment/kafka-connect

# Kafka UI
kubectl logs -n kafka-infrastructure deployment/kafka-ui

# Debezium connector
kubectl logs -n kafka-infrastructure job/debezium-connector-setup
```

### **Estado del Conector Debezium**
```bash
curl http://localhost:8083/connectors/order-outbox-connector/status
```

## 🧹 **Limpieza Completa**

### **Limpieza de Todo**
```bash
./cleanup-with-kind.sh
```

### **Limpieza Solo Infraestructura**
```bash
./cleanup.sh
```

## 🔍 **Troubleshooting**

### **Problema: Kind no está instalado**
```bash
# Instalar Kind
curl -Lo ./kind https://kind.sigs.k8s.io/dl/v0.20.0/kind-linux-amd64
chmod +x ./kind
sudo mv ./kind /usr/local/bin/kind
```

### **Problema: Docker no está corriendo**
```bash
# Iniciar Docker
sudo systemctl start docker
sudo systemctl enable docker
```

### **Problema: Cluster ya existe**
El script te preguntará si quieres eliminar el cluster existente y crear uno nuevo.

### **Problema: Puertos ocupados**
Si los puertos 8090, 8083, o 5432 están ocupados, modifica `kind-config.yaml`:
```yaml
extraPortMappings:
- containerPort: 8090
  hostPort: 8091  # Cambiar a puerto disponible
```

## 📋 **Comandos Útiles**

### **Gestión del Cluster**
```bash
# Listar clusters
kind get clusters

# Información del cluster
kind get nodes --name kafka-infrastructure

# Eliminar cluster
kind delete cluster --name kafka-infrastructure
```

### **Gestión de Contextos**
```bash
# Ver contextos
kubectl config get-contexts

# Cambiar contexto
kubectl config use-context kind-kafka-infrastructure
```

### **Monitoreo**
```bash
# Ver recursos
kubectl get all -n kafka-infrastructure

# Ver eventos
kubectl get events -n kafka-infrastructure

# Ver logs en tiempo real
kubectl logs -f -n kafka-infrastructure deployment/kafka-broker-1
```

## 🎯 **Ventajas del Despliegue Completo**

### ✅ **Todo en Uno**
- Cluster + Infraestructura en un solo comando
- No necesitas configurar nada manualmente

### ✅ **Port Mappings**
- Acceso directo a servicios sin port-forward
- URLs fijas y predecibles

### ✅ **Configuración Optimizada**
- Cluster configurado específicamente para Kafka
- Recursos distribuidos en múltiples nodos

### ✅ **Limpieza Completa**
- Elimina todo: infraestructura + cluster
- No deja residuos

## 🔄 **Flujos de Trabajo**

### **Desarrollo Local**
```bash
# Desplegar todo
./deploy-with-kind.sh

# Trabajar con la infraestructura
# ... desarrollo ...

# Limpiar todo
./cleanup-with-kind.sh
```

### **Testing**
```bash
# Desplegar
./deploy-with-kind.sh

# Ejecutar tests
# ... tests ...

# Limpiar
./cleanup-with-kind.sh
```

### **Demo**
```bash
# Desplegar
./deploy-with-kind.sh

# Mostrar servicios
echo "Kafka UI: http://localhost:8090"
echo "Kafka Connect: http://localhost:8083"

# Limpiar después
./cleanup-with-kind.sh
```

## 📝 **Notas Importantes**

1. **Recursos**: El cluster requiere al menos 4GB RAM y 2 CPU cores
2. **Docker**: Debe estar corriendo y tener permisos
3. **Puertos**: Los puertos 8090, 8083, 5432 deben estar disponibles
4. **Persistencia**: Los datos se pierden al eliminar el cluster
5. **Red**: El cluster usa la red de Docker

## 🆚 **Comparación de Scripts**

| Script | Cluster | Infraestructura | Port Mappings | Limpieza |
|--------|---------|-----------------|---------------|----------|
| `deploy-with-kind.sh` | ✅ Crea | ✅ Despliega | ✅ Configura | ✅ Completa |
| `deploy-simple.sh` | ❌ Usa existente | ✅ Despliega | ❌ Manual | ❌ Parcial |
| `deploy.sh` | ❌ Usa existente | ✅ Despliega | ❌ Manual | ❌ Parcial |

**Recomendación**: Usa `deploy-with-kind.sh` para desarrollo y testing.

#!/bin/bash

# Food Ordering System - Kubernetes Deployment Script (Improved)
# Este script automatiza el despliegue completo del sistema en Kind con Kafka

set -e  # Exit on any error

echo "🚀 Food Ordering System - Kubernetes Deployment (Improved)"
echo "=========================================================="

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para imprimir mensajes con colores
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Función para verificar si un comando existe
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Verificar prerrequisitos
print_status "Verificando prerrequisitos..."

# Verificar Docker
if ! command_exists docker; then
    print_error "Docker no está instalado"
    exit 1
fi

# Verificar Kind
if ! command_exists kind; then
    print_error "Kind no está instalado"
    exit 1
fi

# Verificar kubectl
if ! command_exists kubectl; then
    print_error "kubectl no está instalado"
    exit 1
fi

# Verificar Maven
if ! command_exists mvn; then
    print_error "Maven no está instalado"
    exit 1
fi

print_success "Prerrequisitos verificados"

# Obtener el directorio raíz del proyecto
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
print_status "Directorio raíz del proyecto: $PROJECT_ROOT"

# Cambiar al directorio raíz del proyecto
cd "$PROJECT_ROOT"

# Crear cluster de Kind si no existe
print_status "Verificando cluster de Kind..."
if ! kind get clusters | grep -q "local-cluster"; then
    print_status "Creando cluster de Kind con configuración MetalLB..."
    kind create cluster --name local-cluster --config kubernetes-scripts/kind-config-with-metallb.yaml
    print_success "Cluster de Kind creado con configuración MetalLB"
else
    print_success "Cluster de Kind ya existe"
fi

# Instalar MetalLB
print_status "Instalando MetalLB..."
if [ -f "./kubernetes-scripts/install-metallb.sh" ]; then
    ./kubernetes-scripts/install-metallb.sh
    print_success "MetalLB instalado"
else
    print_warning "Script install-metallb.sh no encontrado, instalando manualmente..."
    kubectl apply -f https://raw.githubusercontent.com/metallb/metallb/v0.13.12/config/manifests/metallb-native.yaml
    kubectl wait --namespace metallb-system --for=condition=ready pod --selector=app=metallb --timeout=300s
    
    # Configurar pool de IPs
    cat <<EOF | kubectl apply -f -
apiVersion: metallb.io/v1beta1
kind: IPAddressPool
metadata:
  name: first-pool
  namespace: metallb-system
spec:
  addresses:
  - 172.18.255.200-172.18.255.250
---
apiVersion: metallb.io/v1beta1
kind: L2Advertisement
metadata:
  name: example
  namespace: metallb-system
spec:
  ipAddressPools:
  - first-pool
EOF
    print_success "MetalLB configurado manualmente"
fi

# Construir imágenes Docker
print_status "Construyendo imágenes Docker..."

# Construir el proyecto con Maven
mvn clean install -DskipTests

# Verificar si las imágenes se construyeron automáticamente
if ! docker images | grep -q "order-service:latest"; then
    print_warning "Las imágenes no se construyeron automáticamente, construyendo manualmente..."
    
    # Construir imágenes manualmente
    cd order-service/order-container && docker build -t order-service:latest . && cd ../..
    cd payment-service/payment-container && docker build -t payment-service:latest . && cd ../..
    cd restaurant-service/restaurant-container && docker build -t restaurant-service:latest . && cd ../..
    cd customer-service && docker build -t customer-service:latest . && cd ..
    cd api-gateway && docker build -t api-gateway:latest . && cd ..
    
    print_success "Imágenes construidas manualmente"
else
    print_success "Imágenes construidas automáticamente"
fi

# Cargar imágenes en Kind
print_status "Cargando imágenes en Kind..."
kind load docker-image order-service:latest --name local-cluster
kind load docker-image payment-service:latest --name local-cluster
kind load docker-image restaurant-service:latest --name local-cluster
kind load docker-image customer-service:latest --name local-cluster
kind load docker-image api-gateway:latest --name local-cluster
print_success "Imágenes cargadas en Kind"

# Aplicar ConfigMaps
print_status "Aplicando ConfigMaps..."
kubectl apply -f infrastructure/kubernetes/configmaps.yml
print_success "ConfigMaps aplicados"

# Desplegar infraestructura
print_status "Desplegando PostgreSQL..."
kubectl apply -f infrastructure/kubernetes/postgres-deployment.yml

# Esperar a que PostgreSQL esté listo
print_status "Esperando a que PostgreSQL esté listo..."
kubectl wait --for=condition=ready pod -l app=postgres-deployment --timeout=300s
print_success "PostgreSQL está listo"

# Desplegar Kafka
print_status "Desplegando Kafka..."
kubectl apply -f infrastructure/kubernetes/kafka-deployment.yml

# Esperar a que Kafka esté listo
print_status "Esperando a que Kafka esté listo..."
kubectl wait --for=condition=ready pod -l app=kafka-broker-1 --timeout=300s
kubectl wait --for=condition=ready pod -l app=kafka-broker-2 --timeout=300s
kubectl wait --for=condition=ready pod -l app=kafka-broker-3 --timeout=300s
print_success "Kafka está listo"

# Desplegar aplicación
print_status "Desplegando microservicios..."
kubectl apply -f infrastructure/kubernetes/application-deployment-local.yml

# Esperar a que los pods estén listos
print_status "Esperando a que los pods estén listos..."
kubectl wait --for=condition=ready pod -l app=order-deployment --timeout=300s
kubectl wait --for=condition=ready pod -l app=payment-deployment --timeout=300s
kubectl wait --for=condition=ready pod -l app=restaurant-deployment --timeout=300s
kubectl wait --for=condition=ready pod -l app=customer-deployment --timeout=300s
kubectl wait --for=condition=ready pod -l app=api-gateway-deployment --timeout=300s
print_success "Todos los pods están listos"

# Verificar que todos los servicios estén funcionando
print_status "Verificando salud de los servicios..."
sleep 10  # Dar tiempo a que los servicios se inicialicen completamente

# Función para verificar servicio
check_service_health() {
    local service_name=$1
    local service_ip=$2
    local port=$3
    
    if [ "$service_ip" != "N/A" ] && [ -n "$service_ip" ]; then
        if curl -s -f "http://$service_ip:$port/actuator/health" > /dev/null 2>&1; then
            echo -e "  ✅ $service_name: ${GREEN}OK${NC}"
        else
            echo -e "  ⚠️  $service_name: ${YELLOW}Iniciando...${NC}"
        fi
    else
        echo -e "  ❌ $service_name: ${RED}No disponible${NC}"
    fi
}

# Obtener IPs de los servicios
GATEWAY_IP=$(kubectl get service api-gateway -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || echo "N/A")
ORDER_IP=$(kubectl get service order-service -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || echo "N/A")
PAYMENT_IP=$(kubectl get service payment-service -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || echo "N/A")
RESTAURANT_IP=$(kubectl get service restaurant-service -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || echo "N/A")
CUSTOMER_IP=$(kubectl get service customer-service -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || echo "N/A")

check_service_health "API Gateway" "$GATEWAY_IP" "8080"
check_service_health "Order Service" "$ORDER_IP" "8181"
check_service_health "Payment Service" "$PAYMENT_IP" "8182"
check_service_health "Restaurant Service" "$RESTAURANT_IP" "8183"
check_service_health "Customer Service" "$CUSTOMER_IP" "8184"

# Mostrar estado final
print_status "Estado del despliegue:"
echo ""
kubectl get pods
echo ""
kubectl get services
echo ""

print_success "¡Despliegue completado exitosamente!"
echo ""
echo "🌐 URLs de acceso (LoadBalancer):"
echo "  - API Gateway: http://172.18.255.201:8080"
echo "  - Order Service: http://172.18.255.202:8181"
echo "  - Payment Service: http://172.18.255.203:8182"
echo "  - Restaurant Service: http://172.18.255.204:8183"
echo "  - Customer Service: http://172.18.255.205:8184"
echo "  - PostgreSQL: 172.18.255.200:5432"
echo ""
echo "📊 Comandos útiles:"
echo "  - Ver pods: kubectl get pods"
echo "  - Ver logs: kubectl logs deployment/<service-name>"
echo "  - Ver servicios: kubectl get services"
echo "  - Acceder a pod: kubectl exec -it <pod-name> -- /bin/bash"
echo "  - Port forwarding: kubectl port-forward service/order-service 8181:8181"
echo ""
print_status "¡El sistema está listo para usar!"


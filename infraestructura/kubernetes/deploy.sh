#!/bin/bash

# Script de Despliegue Completo para Food Ordering System
# Este script crea el cluster Kind, despliega toda la infraestructura y microservicios

set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Variables
CLUSTER_NAME="kafka-infrastructure"
NAMESPACE="kafka-infrastructure"
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Funciones de logging
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Función para verificar si un comando existe
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Función para verificar prerequisitos
check_prerequisites() {
    log_info "Verificando prerequisitos..."
    
    if ! command_exists kind; then
        log_error "Kind no está instalado. Por favor instala Kind primero."
        exit 1
    fi
    
    if ! command_exists kubectl; then
        log_error "kubectl no está instalado. Por favor instala kubectl primero."
        exit 1
    fi
    
    if ! command_exists docker; then
        log_error "Docker no está instalado. Por favor instala Docker primero."
        exit 1
    fi
    
    log_success "Todos los prerequisitos están instalados"
}

# Función para crear cluster Kind
create_kind_cluster() {
    log_info "Creando cluster Kind..."
    
    if kind get clusters | grep -q "$CLUSTER_NAME"; then
        log_warning "El cluster $CLUSTER_NAME ya existe. Eliminando..."
        kind delete cluster --name "$CLUSTER_NAME"
    fi
    
    kind create cluster --name "$CLUSTER_NAME" --config "$SCRIPT_DIR/kind-config-with-metallb.yaml"
    kind export kubeconfig --name "$CLUSTER_NAME"
    
    log_success "Cluster Kind creado exitosamente"
}

# Función para instalar MetalLB
install_metallb() {
    log_info "Instalando MetalLB..."
    
    kubectl apply -f https://raw.githubusercontent.com/metallb/metallb/v0.13.12/config/manifests/metallb-native.yaml
    
    # Esperar a que MetalLB se inicialice completamente
    log_info "Esperando a que MetalLB se inicialice completamente..."
    sleep 60
    
    # Verificar que los pods de MetalLB estén listos
    log_info "Verificando pods de MetalLB..."
    kubectl wait --namespace metallb-system \
        --for=condition=ready pod \
        --selector=app=controller \
        --timeout=300s || log_warning "MetalLB controller no está listo, continuando..."
    
    # Configurar IP pool
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
    
    log_success "MetalLB instalado y configurado"
}

# Función para construir y cargar imágenes de microservicios
build_and_load_images() {
    log_info "Construyendo y cargando imágenes de microservicios..."
    
    local services=("customer-services" "payment-services" "restaurant-services" "order-services" "consulta-services" "api-gateway")
    
    for service in "${services[@]}"; do
        log_info "Procesando $service..."
        
        if [ -d "$PROJECT_ROOT/$service" ]; then
            cd "$PROJECT_ROOT/$service"
            
            # Construir con Gradle usando el gradlew del proyecto raíz
            log_info "Construyendo $service con Gradle..."
            "$PROJECT_ROOT/gradlew" build -x test
            
            # Construir imagen Docker
            local image_name="${service//-services/}-service"
            log_info "Construyendo imagen Docker: $image_name:latest"
            docker build -t "$image_name:latest" .
            
            # Cargar imagen en Kind
            log_info "Cargando imagen en Kind cluster..."
            kind load docker-image "$image_name:latest" --name "$CLUSTER_NAME"
            
            log_success "$service procesado exitosamente"
        else
            log_warning "Directorio $service no encontrado, saltando..."
        fi
    done
    
    cd "$PROJECT_ROOT/infraestructura/kubernetes"
}

# Función para desplegar infraestructura
deploy_infrastructure() {
    log_info "Desplegando infraestructura base..."
    
    # Aplicar ConfigMaps
    kubectl apply -f "$SCRIPT_DIR/configmaps.yaml"
    
    # Aplicar infraestructura base
    kubectl apply -f "$SCRIPT_DIR/infrastructure.yaml"
    
    # Esperar a que PostgreSQL esté listo
    log_info "Esperando a que PostgreSQL esté listo..."
    kubectl wait --for=condition=ready pod -l app=postgres -n "$NAMESPACE" --timeout=300s
    
    # Esperar a que los brokers de Kafka estén listos
    log_info "Esperando a que los brokers de Kafka estén listos..."
    kubectl wait --for=condition=ready pod -l app=kafka-broker-1 -n "$NAMESPACE" --timeout=300s
    kubectl wait --for=condition=ready pod -l app=kafka-broker-2 -n "$NAMESPACE" --timeout=300s
    kubectl wait --for=condition=ready pod -l app=kafka-broker-3 -n "$NAMESPACE" --timeout=300s
    
    log_success "Infraestructura base desplegada"
}

# Función para desplegar Kafka Connect
deploy_kafka_connect() {
    log_info "Desplegando Kafka Connect..."
    
    kubectl apply -f "$SCRIPT_DIR/kafka-connect.yaml"
    
    # Esperar a que Kafka Connect esté listo
    log_info "Esperando a que Kafka Connect esté listo..."
    kubectl wait --for=condition=ready pod -l app=kafka-connect -n "$NAMESPACE" --timeout=300s
    
    log_success "Kafka Connect desplegado"
}

# Función para inicializar base de datos
initialize_database() {
    log_info "Inicializando base de datos..."
    
    kubectl apply -f "$SCRIPT_DIR/jobs.yaml"
    
    # Esperar a que el job de inicialización de BD termine
    log_info "Esperando a que se inicialice la base de datos..."
    kubectl wait --for=condition=complete job/db-init -n "$NAMESPACE" --timeout=300s
    
    log_success "Base de datos inicializada"
}

# Función para inicializar Kafka
initialize_kafka() {
    log_info "Inicializando tópicos de Kafka..."
    
    # Esperar a que el job de inicialización de Kafka termine
    log_info "Esperando a que se inicialicen los tópicos..."
    kubectl wait --for=condition=complete job/kafka-init -n "$NAMESPACE" --timeout=300s
    
    log_success "Tópicos de Kafka inicializados"
}

# Función para registrar conector Debezium
register_debezium_connector() {
    log_info "Registrando conector Debezium..."
    kubectl apply -f "$SCRIPT_DIR/job_connect.yaml"
    # Esperar a que el job de Debezium termine
    log_info "Esperando a que se registre el conector Debezium..."
    kubectl wait --for=condition=complete job/debezium-connector-setup -n "$NAMESPACE" --timeout=300s
    
    log_success "Conector Debezium registrado"
}

# Función para desplegar microservicios
deploy_microservices() {
    log_info "Desplegando microservicios..."
    
    kubectl apply -f "$SCRIPT_DIR/microservices-complete.yaml"
    
    # Esperar a que todos los microservicios estén listos
    log_info "Esperando a que los microservicios estén listos..."
    # kubectl wait --for=condition=ready pod -l app=order-service -n "$NAMESPACE" --timeout=300s
    # kubectl wait --for=condition=ready pod -l app=payment-service -n "$NAMESPACE" --timeout=300s
    # kubectl wait --for=condition=ready pod -l app=restaurant-service -n "$NAMESPACE" --timeout=300s
    # kubectl wait --for=condition=ready pod -l app=customer-service -n "$NAMESPACE" --timeout=300s
    # kubectl wait --for=condition=ready pod -l app=consulta-service -n "$NAMESPACE" --timeout=300s
    # kubectl wait --for=condition=ready pod -l app=api-gateway -n "$NAMESPACE" --timeout=300s
    
    log_success "Microservicios desplegados"
}

# Función para mostrar información final
show_final_info() {
    log_success "¡Despliegue completado exitosamente!"
    echo
    echo "=== INFORMACIÓN DE ACCESO ==="
    echo
    echo "📊 Kubernetes Dashboard:"
    echo "   kubectl proxy"
    echo "   http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/"
    echo
    echo "🔍 Kafka UI:"
    echo "   kubectl port-forward -n $NAMESPACE service/kafka-ui 8090:8080"
    echo "   http://localhost:8090"
    echo
    echo "🔌 Kafka Connect:"
    echo "   kubectl port-forward -n $NAMESPACE service/kafka-connect 8083:8083"
    echo "   http://localhost:8083"
    echo
    echo "🌐 API Gateway:"
    echo "   kubectl get svc -n $NAMESPACE api-gateway"
    echo
    echo "📝 Comandos útiles:"
    echo "   kubectl get pods -n $NAMESPACE"
    echo "   kubectl get svc -n $NAMESPACE"
    echo "   kubectl logs -n $NAMESPACE -l app=kafka-connect"
    echo
    echo "🧹 Para limpiar todo:"
    echo "   kind delete cluster --name $CLUSTER_NAME"
}

# Función principal
main() {
    log_info "Iniciando despliegue completo del Food Ordering System..."
    
    check_prerequisites
    create_kind_cluster
    install_metallb
    deploy_infrastructure
    initialize_database
    initialize_kafka
    build_and_load_images
    deploy_kafka_connect
    deploy_microservices
    
    register_debezium_connector
    show_final_info
}

# Ejecutar función principal
main "$@"

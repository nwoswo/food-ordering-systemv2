#!/bin/bash

# Script de Limpieza para Food Ordering System
# Este script elimina todos los recursos del cluster

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

# Función para limpiar recursos de Kubernetes
cleanup_kubernetes_resources() {
    log_info "Limpiando recursos de Kubernetes..."
    
    # Eliminar namespace completo
    if kubectl get namespace "$NAMESPACE" >/dev/null 2>&1; then
        log_info "Eliminando namespace $NAMESPACE..."
        kubectl delete namespace "$NAMESPACE" --timeout=300s
        log_success "Namespace $NAMESPACE eliminado"
    else
        log_warning "Namespace $NAMESPACE no existe"
    fi
    
    # Eliminar MetalLB
    if kubectl get namespace metallb-system >/dev/null 2>&1; then
        log_info "Eliminando MetalLB..."
        kubectl delete namespace metallb-system --timeout=300s
        log_success "MetalLB eliminado"
    else
        log_warning "MetalLB no está instalado"
    fi
}

# Función para eliminar cluster Kind
delete_kind_cluster() {
    log_info "Eliminando cluster Kind..."
    
    if kind get clusters | grep -q "$CLUSTER_NAME"; then
        kind delete cluster --name "$CLUSTER_NAME"
        log_success "Cluster Kind $CLUSTER_NAME eliminado"
    else
        log_warning "Cluster Kind $CLUSTER_NAME no existe"
    fi
}

# Función para limpiar imágenes Docker (opcional)
cleanup_docker_images() {
    if [ "$1" = "--clean-images" ]; then
        log_info "Limpiando imágenes Docker de microservicios..."
        
        local images=("customer-service" "payment-service" "restaurant-service" "order-service" "consulta-service" "api-gateway")
        
        for image in "${images[@]}"; do
            if docker images | grep -q "$image"; then
                log_info "Eliminando imagen $image..."
                docker rmi "$image:latest" 2>/dev/null || true
            fi
        done
        
        log_success "Imágenes Docker limpiadas"
    fi
}

# Función principal
main() {
    log_info "Iniciando limpieza del Food Ordering System..."
    
    cleanup_kubernetes_resources
    delete_kind_cluster
    cleanup_docker_images "$1"
    
    log_success "¡Limpieza completada exitosamente!"
    echo
    echo "Todos los recursos han sido eliminados."
    echo "Para un nuevo despliegue, ejecuta: ./deploy.sh"
}

# Mostrar ayuda
show_help() {
    echo "Uso: $0 [OPCIONES]"
    echo
    echo "Opciones:"
    echo "  --clean-images    También elimina las imágenes Docker de microservicios"
    echo "  --help           Muestra esta ayuda"
    echo
    echo "Ejemplos:"
    echo "  $0               Limpia solo recursos de Kubernetes y Kind"
    echo "  $0 --clean-images Limpia todo incluyendo imágenes Docker"
}

# Procesar argumentos
case "$1" in
    --help)
        show_help
        exit 0
        ;;
    --clean-images)
        main "$1"
        ;;
    "")
        main
        ;;
    *)
        log_error "Opción desconocida: $1"
        show_help
        exit 1
        ;;
esac

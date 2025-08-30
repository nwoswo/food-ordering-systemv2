#!/bin/bash

# Build and Load Docker Images Script
# This script builds all microservice images and loads them into Kind

set -e

echo "🐳 Building and loading Docker images..."

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

# Function to build and load image
build_and_load_image() {
    local service_name=$1
    local service_path=$2
    
    print_status "Building $service_name..."
    cd "$service_path"
    docker build -t "$service_name:latest" .
    kind load docker-image "$service_name:latest" --name kafka-infrastructure
    cd - > /dev/null
    print_success "$service_name built and loaded successfully!"
}

# Build all microservices
build_and_load_image "customer-service" "customer-services"
build_and_load_image "payment-service" "payment-services"
build_and_load_image "restaurant-service" "restaurant-services"
build_and_load_image "order-service" "order-services"
build_and_load_image "consulta-service" "consulta-services"
build_and_load_image "api-gateway" "api-gateway"

print_success "🎉 All images built and loaded successfully!"
echo ""
print_status "📋 Available images in Kind cluster:"
docker images | grep -E "(customer-service|payment-service|restaurant-service|order-service|consulta-service|api-gateway)"

#!/bin/bash

echo "🐳 Generando imágenes Docker de todos los servicios..."
echo ""

# Primero compilar todos los módulos
echo "📦 Compilando todos los módulos antes de generar imágenes Docker..."
./gradlew clean build -x test

echo ""
echo "🔨 Generando imágenes Docker..."

# API Gateway
echo "📦 Generando imagen para api-gateway..."
cd api-gateway
cp build/libs/api-gateway-1.0.0.jar .
docker build -t api-gateway:latest .
cd ..

# Customer Services
echo "📦 Generando imagen para customer-services..."
cd customer-services
cp build/libs/customer-service-1.0.0.jar .
docker build -t customer-service:latest .
cd ..

# Order Services
echo "📦 Generando imagen para order-services..."
cd order-services
cp build/libs/order-service-1.0.0.jar .
docker build -t order-service:latest .
cd ..

# Payment Services
echo "📦 Generando imagen para payment-services..."
cd payment-services
cp build/libs/payment-service-1.0.0.jar .
docker build -t payment-service:latest .
cd ..

# Restaurant Services
echo "📦 Generando imagen para restaurant-services..."
cd restaurant-services
cp build/libs/restaurant-service-1.0.0.jar .
docker build -t restaurant-service:latest .
cd ..

echo ""
echo "✅ Imágenes Docker generadas exitosamente!"
echo ""
echo "📋 Imágenes disponibles:"
docker images | grep -E "(api-gateway|customer-service|order-service|payment-service|restaurant-service)"

echo ""
echo "🚀 Para levantar toda la infraestructura, ejecuta:"
echo "   cd kafka-infrastructure && docker compose up -d"
echo ""
echo "🔗 URLs de acceso:"
echo "   - Kafka UI: http://localhost:8090"
echo "   - PostgreSQL: localhost:5432"
echo "   - API Gateway: http://localhost:8080"
echo "   - Order Service: http://localhost:8181"
echo "   - Payment Service: http://localhost:8182"
echo "   - Restaurant Service: http://localhost:8183"
echo "   - Customer Service: http://localhost:8184"

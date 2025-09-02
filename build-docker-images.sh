#!/bin/bash

echo "🔨 Generando imágenes Docker para Food Ordering System..."

# Clean and build all projects
echo "📦 Compilando proyectos con Gradle..."
./gradlew clean build -x test

if [ $? -eq 0 ]; then
    echo "✅ Compilación exitosa! Generando imágenes Docker..."
    
    # Build order-service
    echo "🐳 Generando imagen order-service..."
    docker build -t order-service:latest order-services/ --build-arg JAR_FILE=order-service-1.0.0.jar
    
    # Build customer-service
    echo "🐳 Generando imagen customer-service..."
    docker build -t customer-service:latest customer-services/ --build-arg JAR_FILE=customer-service-1.0.0.jar
    
    # Build payment-service
    echo "🐳 Generando imagen payment-service..."
    docker build -t payment-service:latest payment-services/ --build-arg JAR_FILE=payment-service-1.0.0.jar
    
    # Build restaurant-service
    echo "🐳 Generando imagen restaurant-service..."
    docker build -t restaurant-service:latest restaurant-services/ --build-arg JAR_FILE=restaurant-service-1.0.0.jar
    
    # Build api-gateway
    echo "🐳 Generando imagen api-gateway..."
    docker build -t api-gateway-service:latest api-gateway-services/ --build-arg JAR_FILE=api-gateway-service-1.0.0.jar
    
    # Build consulta-service
    echo "🐳 Generando imagen consulta-service..."
    docker build -t consulta-service:latest consulta-services/ --build-arg JAR_FILE=consulta-service-1.0.0.jar
    
    echo "✅ Todas las imágenes Docker generadas exitosamente!"
    echo ""
    echo "🚀 Ahora puedes ejecutar: ./start-full-stack.sh"
else
    echo "❌ Error en la compilación! Revisa los errores arriba."
    exit 1
fi

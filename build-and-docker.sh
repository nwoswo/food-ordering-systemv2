#!/bin/bash

echo "Building Food Ordering System with Gradle..."

# Clean and build all projects
./gradlew clean build -x test

if [ $? -eq 0 ]; then
    echo "Build successful! Creating Docker images..."
    
    # Build order-service
    echo "Building order-service Docker image..."
    docker build -t order-service:latest order-services/ --build-arg JAR_FILE=order-service-1.0.0.jar
    
    # Build customer-service
    echo "Building customer-service Docker image..."
    docker build -t customer-service:latest customer-services/ --build-arg JAR_FILE=customer-service-1.0.0.jar
    
    # Build payment-service
    echo "Building payment-service Docker image..."
    docker build -t payment-service:latest payment-services/ --build-arg JAR_FILE=payment-service-1.0.0.jar
    
    # Build restaurant-service
    echo "Building restaurant-service Docker image..."
    docker build -t restaurant-service:latest restaurant-services/ --build-arg JAR_FILE=restaurant-service-1.0.0.jar
    
    # Build api-gateway
    echo "Building api-gateway Docker image..."
    docker build -t api-gateway:latest api-gateway/ --build-arg JAR_FILE=api-gateway-1.0.0.jar
    
    # Build consulta-service
    echo "Building consulta-service Docker image..."
    docker build -t consulta-service:latest consulta-services/ --build-arg JAR_FILE=consulta-service-1.0.0.jar
    
    echo "All Docker images created successfully!"
    echo "You can now run: ./start-full-stack.sh to start the complete stack"
else
    echo "Build failed! Please check the errors above."
    exit 1
fi

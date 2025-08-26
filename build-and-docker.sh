#!/bin/bash

echo "Building Food Ordering System with Gradle..."

# Clean and build all projects
./gradlew clean build -x test

if [ $? -eq 0 ]; then
    echo "Build successful! Creating Docker images..."
    
    # Build Docker images for each service
    echo "Building order-service Docker image..."
    docker build --build-arg JAR_FILE=order-service-1.0.0.jar -t order-service:latest order-services/
    
    echo "Building customer-service Docker image..."
    docker build --build-arg JAR_FILE=customer-service-1.0.0.jar -t customer-service:latest customer-services/
    
    echo "Building payment-service Docker image..."
    docker build --build-arg JAR_FILE=payment-service-1.0.0.jar -t payment-service:latest payment-services/
    
    echo "Building restaurant-service Docker image..."
    docker build --build-arg JAR_FILE=restaurant-service-1.0.0.jar -t restaurant-service:latest restaurant-services/
    
    echo "Building api-gateway Docker image..."
    docker build --build-arg JAR_FILE=api-gateway-1.0.0.jar -t api-gateway:latest api-gateway/
    
    echo "Docker images created successfully!"
    echo "Available images:"
    docker images | grep -E "(order|payment|restaurant|customer|api-gateway)"
else
    echo "Build failed! Please check the errors above."
    exit 1
fi


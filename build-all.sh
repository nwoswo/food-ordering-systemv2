#!/bin/bash

echo "🚀 Compilando TODO el proyecto food-ordering-systemv2..."
echo ""

# Compilar todos los módulos
./gradlew clean build --continue

echo ""
echo "📋 Resumen de compilación:"
echo ""

# Verificar qué módulos se compilaron exitosamente
echo "✅ Módulos que compilaron exitosamente:"
if [ -f "./common-libraries/build/libs/common-libraries-1.0-SNAPSHOT.jar" ]; then
    echo "   - common-libraries"
fi
if [ -f "./kafka-infrastructure/build/libs/kafka-infrastructure-1.0-SNAPSHOT.jar" ]; then
    echo "   - kafka-infrastructure"
fi
if [ -f "./api-gateway/build/libs/api-gateway-1.0.0.jar" ]; then
    echo "   - api-gateway"
fi
if [ -f "./customer-services/build/libs/customer-service-1.0.0.jar" ]; then
    echo "   - customer-services"
fi
if [ -f "./order-services/build/libs/order-service-1.0.0.jar" ]; then
    echo "   - order-services"
fi
if [ -f "./payment-services/build/libs/payment-service-1.0.0.jar" ]; then
    echo "   - payment-services"
fi
if [ -f "./restaurant-services/build/libs/restaurant-service-1.0.0.jar" ]; then
    echo "   - restaurant-services"
fi

echo ""
echo "📁 Archivos JAR generados:"
find . -name "*.jar" -type f -not -path "./gradle/*" | sed 's/^/   /'

echo ""
echo "💡 Para generar imágenes Docker, ejecuta: ./build-docker-images.sh"

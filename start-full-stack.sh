#!/bin/bash

# Helper function to wait for a service to be available by polling its health endpoint
wait_for_service() {
    local name=$1
    local url=$2
    echo -n "⏳ Esperando a que el servicio '$name' esté disponible en $url..."
    until curl -s -f -o /dev/null "$url"; do
        echo -n "."
        sleep 5
    done
    echo " ✅ ¡Listo!"
}

echo "🚀 Iniciando el stack completo de Food Ordering System..."
echo ""

# Verificar si Docker está ejecutándose
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker no está ejecutándose. Por favor, inicia Docker y vuelve a intentar."
    exit 1
fi

# Verificar si las imágenes Docker existen
echo "🔍 Verificando imágenes Docker..."
if ! docker images | grep -q "api-gateway"; then
    echo "⚠️  Imágenes Docker no encontradas. Generando imágenes Docker..."
    ./build-docker-images.sh
fi

echo ""
echo "📦 Iniciando infraestructura (Kafka + PostgreSQL + Kafka Connect)..."
cd kafka-infrastructure
docker compose up -d

# Esperar a que los componentes clave de la infraestructura estén listos
wait_for_service "Kafka Connect" "http://localhost:8083"

echo ""
echo "🚀 Iniciando servicios de aplicación..."
docker compose -f docker-compose-services.yml up -d

# Esperar a que los servicios de la aplicación estén listos (asumiendo que exponen /actuator/health)
wait_for_service "Order Service" "http://localhost:8181/actuator/health"
wait_for_service "Payment Service" "http://localhost:8182/actuator/health"
wait_for_service "Restaurant Service" "http://localhost:8183/actuator/health"
wait_for_service "Customer Service" "http://localhost:8184/actuator/health"
wait_for_service "Consulta Service" "http://localhost:8185/api/v1/consulta/health"
wait_for_service "API Gateway" "http://localhost:8080/actuator/health"

echo ""
echo "🔧 Configurando Debezium Outbox Pattern..."
./setup-debezium-outbox.sh

echo ""
echo "✅ Stack completo iniciado!"
echo ""
echo "🔗 URLs de acceso:"
echo "   - API Gateway: http://localhost:8080"
echo "   - Order Service: http://localhost:8181"
echo "   - Payment Service: http://localhost:8182"
echo "   - Restaurant Service: http://localhost:8183"
echo "   - Customer Service: http://localhost:8184"
echo "   - Consulta Service: http://localhost:8185"
echo "   - Kafka UI: http://localhost:8090"
echo "   - Kafka Connect: http://localhost:8083"
echo "   - PostgreSQL: localhost:5432"
echo ""
echo "📋 Comandos útiles:"
echo "   - Ver logs infraestructura: cd kafka-infrastructure && docker compose logs -f"
echo "   - Ver logs servicios: cd kafka-infrastructure && docker compose -f docker-compose-services.yml logs -f"
echo "   - Parar infraestructura: cd kafka-infrastructure && docker compose down"
echo "   - Parar servicios: cd kafka-infrastructure && docker compose -f docker-compose-services.yml down"
echo "   - Reiniciar servicios: cd kafka-infrastructure && docker compose -f docker-compose-services.yml restart"
echo ""
echo "🔍 Verificando estado de los servicios..."
docker compose -f docker-compose-services.yml ps

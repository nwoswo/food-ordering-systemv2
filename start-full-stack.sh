#!/bin/bash

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

echo ""
echo "⏳ Esperando a que Kafka, PostgreSQL y Kafka Connect estén listos..."
sleep 45

echo ""
echo "🚀 Iniciando servicios de aplicación..."
docker compose -f docker-compose-services.yml up -d

echo ""
echo "⏳ Esperando a que los servicios estén listos..."
sleep 20

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

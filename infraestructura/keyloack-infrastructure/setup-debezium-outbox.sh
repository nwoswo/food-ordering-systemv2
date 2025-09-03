#!/bin/bash

# Obtener el directorio donde se encuentra el script para usar rutas absolutas
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
CONFIG_FILE="$SCRIPT_DIR/debezium-order-outbox-connector.json"

echo "🔧 Configurando Debezium Outbox Pattern..."
echo ""

# Esperar a que Kafka Connect esté listo
echo "⏳ Esperando a que Kafka Connect esté listo..."
# Verificar si Kafka Connect está disponible
until curl -s http://localhost:8083/connectors > /dev/null; do
    echo -n "."
    sleep 10
done

echo "✅ Kafka Connect está listo"
echo ""

# Verificar que Debezium PostgreSQL Connector esté disponible
echo "🔍 Verificando conectores disponibles..."
curl -s http://localhost:8083/connector-plugins | jq '.[] | select(.class | contains("PostgresConnector"))'

echo ""
CONNECTOR_NAME="order-outbox-connector"
CONNECTOR_URL="http://localhost:8083/connectors/$CONNECTOR_NAME"

echo "🔍 Verificando si el conector '$CONNECTOR_NAME' ya existe..."
STATUS_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$CONNECTOR_URL")

if [ "$STATUS_CODE" -eq 200 ]; then
  echo "✅ El conector '$CONNECTOR_NAME' ya existe. Verificando su estado..."
  STATUS=$(curl -s "$CONNECTOR_URL/status" | jq -r '.connector.state')
  echo "   - Estado actual: $STATUS"

  if [ "$STATUS" == "FAILED" ]; then
    echo "   - ⚠️  El conector está en estado FAILED. Intentando reiniciar..."
    curl -s -X POST "$CONNECTOR_URL/restart"
    sleep 5 # Esperar un momento para que el reinicio tenga efecto
  fi
else
  echo "📦 El conector no existe. Creándolo ahora..."
  curl -s -X POST http://localhost:8083/connectors \
    -H "Content-Type: application/json" \
    -d @"$CONFIG_FILE"
  sleep 5 # Dar tiempo para que se inicialice
fi

echo ""
echo "🔍 Verificando estado final del conector..."
curl -s "$CONNECTOR_URL/status" | jq .

echo ""
echo "📋 Listando todos los conectores..."
curl -s http://localhost:8083/connectors | jq .

echo ""
echo "✅ Configuración de Debezium Outbox completada!"
echo ""
echo "🔗 URLs útiles:"
echo "   - Kafka Connect REST API: http://localhost:8083"
echo "   - Conectores: http://localhost:8083/connectors"
echo "   - Estado del conector: http://localhost:8083/connectors/$CONNECTOR_NAME/status"
echo "   - Plugins disponibles: http://localhost:8083/connector-plugins"
echo ""
echo "📊 Para ver los eventos en Kafka:"
echo "   docker exec -it kafka-broker-1 kafka-console-consumer --bootstrap-server localhost:9092 --topic order-OrderCreatedEvent --from-beginning"

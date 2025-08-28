#!/bin/bash

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

echo "🔍 Verificando si el conector '$CONNECTOR_NAME' ya existe..."
STATUS_CODE=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:8083/connectors/$CONNECTOR_NAME")

if [ "$STATUS_CODE" -eq 200 ]; then
  echo "✅ El conector '$CONNECTOR_NAME' ya existe. No se tomarán acciones."
else
  echo "📦 Creando conector Debezium para '$CONNECTOR_NAME'..."
  curl -s -X POST http://localhost:8083/connectors \
    -H "Content-Type: application/json" \
    -d @debezium-order-outbox-connector.json
fi

echo ""
echo "🔍 Verificando conector creado..."
curl -s "http://localhost:8083/connectors/$CONNECTOR_NAME/status" | jq .

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

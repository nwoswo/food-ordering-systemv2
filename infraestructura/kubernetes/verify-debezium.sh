#!/bin/bash

echo "🔍 Verificando estado de Debezium en Kubernetes..."
echo ""

# Verificar namespace
echo "📋 Verificando namespace kafka-infrastructure..."
kubectl get namespace kafka-infrastructure
echo ""

# Verificar pods
echo "🐳 Verificando pods..."
kubectl get pods -n kafka-infrastructure
echo ""

# Verificar servicios
echo "🔌 Verificando servicios..."
kubectl get services -n kafka-infrastructure
echo ""

# Verificar logs de PostgreSQL
echo "📊 Verificando logs de PostgreSQL..."
kubectl logs -n kafka-infrastructure deployment/postgres --tail=20
echo ""

# Verificar logs de Kafka Connect
echo "🔗 Verificando logs de Kafka Connect..."
kubectl logs -n kafka-infrastructure deployment/kafka-connect --tail=20
echo ""

# Verificar estado del conector Debezium
echo "⚙️ Verificando estado del conector Debezium..."
KAFKA_CONNECT_POD=$(kubectl get pods -n kafka-infrastructure -l app=kafka-connect -o jsonpath='{.items[0].metadata.name}')
if [ -n "$KAFKA_CONNECT_POD" ]; then
    echo "Kafka Connect Pod: $KAFKA_CONNECT_POD"
    kubectl exec -n kafka-infrastructure $KAFKA_CONNECT_POD -- curl -s http://localhost:8083/connectors | jq .
    echo ""
    kubectl exec -n kafka-infrastructure $KAFKA_CONNECT_POD -- curl -s http://localhost:8083/connectors/order-outbox-connector/status | jq .
else
    echo "❌ No se encontró el pod de Kafka Connect"
fi
echo ""

# Verificar topics de Kafka
echo "📝 Verificando topics de Kafka..."
KAFKA_POD=$(kubectl get pods -n kafka-infrastructure -l app=kafka-broker-1 -o jsonpath='{.items[0].metadata.name}')
if [ -n "$KAFKA_POD" ]; then
    echo "Kafka Broker Pod: $KAFKA_POD"
    kubectl exec -n kafka-infrastructure $KAFKA_POD -- kafka-topics --list --bootstrap-server localhost:9092
else
    echo "❌ No se encontró el pod de Kafka Broker"
fi
echo ""

# Verificar tabla outbox en PostgreSQL
echo "🗄️ Verificando tabla outbox en PostgreSQL..."
POSTGRES_POD=$(kubectl get pods -n kafka-infrastructure -l app=postgres -o jsonpath='{.items[0].metadata.name}')
if [ -n "$POSTGRES_POD" ]; then
    echo "PostgreSQL Pod: $POSTGRES_POD"
    kubectl exec -n kafka-infrastructure $POSTGRES_POD -- psql -U postgres -d postgres -c "SELECT COUNT(*) FROM orden.outbox_events;"
    kubectl exec -n kafka-infrastructure $POSTGRES_POD -- psql -U postgres -d postgres -c "SELECT id, aggregate_id, event_type, processed, created_at FROM orden.outbox_events ORDER BY created_at DESC LIMIT 3;"
else
    echo "❌ No se encontró el pod de PostgreSQL"
fi
echo ""

echo "✅ Verificación completada!"

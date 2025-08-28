#!/bin/bash

# Simplified Kafka Infrastructure Kubernetes Deployment Script
# This script deploys the complete Kafka infrastructure using consolidated files

set -e

NAMESPACE="kafka-infrastructure"

echo "🚀 Starting Simplified Kafka Infrastructure Deployment..."

# Deploy PostgreSQL (all in one file)
echo "🐘 Deploying PostgreSQL..."
kubectl apply -f postgres-complete.yaml

# Wait for PostgreSQL to be ready
echo "⏳ Waiting for PostgreSQL to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/postgres -n $NAMESPACE

# Deploy Kafka (all in one file)
echo "📡 Deploying Kafka cluster..."
kubectl apply -f kafka-complete.yaml

# Wait for Kafka brokers to be ready
echo "⏳ Waiting for Kafka brokers to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/kafka-broker-1 -n $NAMESPACE
kubectl wait --for=condition=available --timeout=300s deployment/kafka-broker-2 -n $NAMESPACE
kubectl wait --for=condition=available --timeout=300s deployment/kafka-broker-3 -n $NAMESPACE

# Initialize Kafka topics
echo "🔧 Initializing Kafka topics..."
kubectl apply -f kafka-init-job.yaml

# Wait for Kafka initialization to complete
echo "⏳ Waiting for Kafka initialization to complete..."
kubectl wait --for=condition=complete --timeout=300s job/kafka-init -n $NAMESPACE

# Deploy Kafka Connect
echo "🔌 Deploying Kafka Connect..."
kubectl apply -f kafka-connect-deployment.yaml
kubectl apply -f kafka-connect-service.yaml

# Wait for Kafka Connect to be ready
echo "⏳ Waiting for Kafka Connect to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/kafka-connect -n $NAMESPACE

# Deploy Debezium connector
echo "🔄 Deploying Debezium connector..."
kubectl apply -f debezium-connector-configmap.yaml
kubectl apply -f debezium-connector-job.yaml

# Deploy Kafka UI
echo "🖥️ Deploying Kafka UI..."
kubectl apply -f kafka-ui-complete.yaml

# Wait for Kafka UI to be ready
echo "⏳ Waiting for Kafka UI to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/kafka-ui -n $NAMESPACE

echo "✅ Simplified Kafka Infrastructure deployment completed!"
echo ""
echo "📊 Deployment Status:"
kubectl get all -n $NAMESPACE

echo ""
echo "🌐 Access Points:"
echo "Kafka UI: http://localhost:8090 (if using port-forward)"
echo "Kafka Connect REST API: http://localhost:8083 (if using port-forward)"
echo ""
echo "🔧 To access services from outside the cluster:"
echo "kubectl port-forward -n $NAMESPACE service/kafka-ui 8090:8090"
echo "kubectl port-forward -n $NAMESPACE service/kafka-connect 8083:8083"
echo ""
echo "📋 To check logs:"
echo "kubectl logs -n $NAMESPACE deployment/kafka-broker-1"
echo "kubectl logs -n $NAMESPACE deployment/kafka-connect"
echo "kubectl logs -n $NAMESPACE deployment/kafka-ui"
echo ""
echo "🔍 To check Debezium connector status:"
echo "kubectl logs -n $NAMESPACE job/debezium-connector-setup"

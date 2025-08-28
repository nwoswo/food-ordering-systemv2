#!/bin/bash

# Complete Kafka Infrastructure Deployment with Kind
# This script creates a Kind cluster and deploys the complete Kafka infrastructure

set -e

CLUSTER_NAME="kafka-infrastructure"
NAMESPACE="kafka-infrastructure"

echo "🚀 Starting Complete Kafka Infrastructure Deployment with Kind..."

# Check if Kind is installed
if ! command -v kind &> /dev/null; then
    echo "❌ Kind is not installed. Please install Kind first:"
    echo "   curl -Lo ./kind https://kind.sigs.k8s.io/dl/v0.20.0/kind-linux-amd64"
    echo "   chmod +x ./kind"
    echo "   sudo mv ./kind /usr/local/bin/kind"
    exit 1
fi

# Check if kubectl is installed
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl is not installed. Please install kubectl first."
    exit 1
fi

# Check if Docker is running
if ! docker info &> /dev/null; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Check if cluster already exists
if kind get clusters | grep -q "$CLUSTER_NAME"; then
    echo "⚠️  Cluster '$CLUSTER_NAME' already exists."
    read -p "Do you want to delete it and create a new one? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "🗑️  Deleting existing cluster..."
        kind delete cluster --name "$CLUSTER_NAME"
    else
        echo "🔄 Using existing cluster..."
    fi
fi

# Create Kind cluster
if ! kind get clusters | grep -q "$CLUSTER_NAME"; then
    echo "🏗️  Creating Kind cluster '$CLUSTER_NAME'..."
    kind create cluster --name "$CLUSTER_NAME" 
    
    echo "⏳ Waiting for cluster to be ready..."
    kubectl wait --for=condition=Ready nodes --all --timeout=300s
    
    echo "✅ Kind cluster created successfully!"
else
    echo "✅ Using existing Kind cluster '$CLUSTER_NAME'"
fi

# Set context to the new cluster
echo "⚙️  Setting kubectl context..."
kubectl cluster-info --context "kind-$CLUSTER_NAME"

# Wait for cluster to be fully ready
echo "⏳ Waiting for cluster components to be ready..."
kubectl wait --for=condition=Ready nodes --all --timeout=300s

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
echo "🖥️  Deploying Kafka UI..."
kubectl apply -f kafka-ui-complete.yaml

# Wait for Kafka UI to be ready
echo "⏳ Waiting for Kafka UI to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/kafka-ui -n $NAMESPACE

echo "✅ Complete Kafka Infrastructure deployment completed!"
echo ""
echo "📊 Deployment Status:"
kubectl get all -n $NAMESPACE

echo ""
echo "🌐 Access Points (direct access via Kind port mappings):"
echo "Kafka UI: http://localhost:8090"
echo "Kafka Connect REST API: http://localhost:8083"
echo "PostgreSQL: localhost:5432 (if needed externally)"
echo ""
echo "🔧 Cluster Information:"
echo "Cluster Name: $CLUSTER_NAME"
echo "Context: kind-$CLUSTER_NAME"
echo ""
echo "📋 Useful Commands:"
echo "kubectl cluster-info --context kind-$CLUSTER_NAME"
echo "kubectl get nodes"
echo "kubectl get all -n $NAMESPACE"
echo ""
echo "📋 To check logs:"
echo "kubectl logs -n $NAMESPACE deployment/kafka-broker-1"
echo "kubectl logs -n $NAMESPACE deployment/kafka-connect"
echo "kubectl logs -n $NAMESPACE deployment/kafka-ui"
echo ""
echo "🔍 To check Debezium connector status:"
echo "kubectl logs -n $NAMESPACE job/debezium-connector-setup"
echo ""
echo "🧹 To clean up everything:"
echo "kind delete cluster --name $CLUSTER_NAME"

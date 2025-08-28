#!/bin/bash

# Complete Cleanup Script for Kafka Infrastructure with Kind
# This script removes the infrastructure and the Kind cluster

set -e

CLUSTER_NAME="kafka-infrastructure"
NAMESPACE="kafka-infrastructure"

echo "🧹 Starting Complete Cleanup for Kafka Infrastructure with Kind..."

# Check if Kind is installed
if ! command -v kind &> /dev/null; then
    echo "❌ Kind is not installed. Skipping cluster cleanup."
    exit 1
fi

# Check if cluster exists
if ! kind get clusters | grep -q "$CLUSTER_NAME"; then
    echo "⚠️  Cluster '$CLUSTER_NAME' does not exist. Nothing to clean up."
    exit 0
fi

echo "🗑️  Cleaning up Kafka Infrastructure..."

# Delete all resources in the namespace (if namespace exists)
if kubectl get namespace "$NAMESPACE" &> /dev/null; then
    echo "📦 Deleting all resources in namespace $NAMESPACE..."
    kubectl delete namespace "$NAMESPACE" --ignore-not-found=true
    
    # Wait for namespace deletion to complete
    echo "⏳ Waiting for namespace deletion to complete..."
    kubectl wait --for=delete namespace/"$NAMESPACE" --timeout=300s || true
    echo "✅ Namespace deleted successfully!"
else
    echo "ℹ️  Namespace $NAMESPACE does not exist."
fi

echo "🗑️  Deleting Kind cluster '$CLUSTER_NAME'..."
kind delete cluster --name "$CLUSTER_NAME"

echo "✅ Complete cleanup completed!"
echo ""
echo "📋 Note: Persistent volumes may need to be manually deleted if they were created with 'Retain' policy"
echo "To check for remaining PVs: kubectl get pv"
echo "To delete a PV: kubectl delete pv <pv-name>"
echo ""
echo "🔧 To verify cleanup:"
echo "kind get clusters"
echo "kubectl get namespaces"

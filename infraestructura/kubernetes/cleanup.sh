#!/bin/bash

# Kafka Infrastructure Kubernetes Cleanup Script
# This script removes the complete Kafka infrastructure from Kubernetes

set -e

NAMESPACE="kafka-infrastructure"

echo "🧹 Starting Kafka Infrastructure Cleanup..."

# Delete all resources in the namespace
echo "🗑️ Deleting all resources in namespace $NAMESPACE..."
kubectl delete namespace $NAMESPACE --ignore-not-found=true

# Wait for namespace deletion to complete
echo "⏳ Waiting for namespace deletion to complete..."
kubectl wait --for=delete namespace/$NAMESPACE --timeout=300s || true

echo "✅ Kafka Infrastructure cleanup completed!"
echo ""
echo "📋 Note: Persistent volumes may need to be manually deleted if they were created with 'Retain' policy"
echo "To check for remaining PVs: kubectl get pv"
echo "To delete a PV: kubectl delete pv <pv-name>"

#!/bin/bash

echo "Testing Consulta Service..."

# Wait for service to be ready
echo "Waiting for service to be ready..."
until curl -f http://localhost:8185/api/v1/consulta/health; do
  echo "Service is not ready yet. Waiting..."
  sleep 5
done

echo "Service is ready!"

# Test health endpoint
echo "Testing health endpoint..."
curl -s http://localhost:8185/api/v1/consulta/health
echo -e "\n"

# Test get all orders
echo "Testing get all orders..."
curl -s http://localhost:8185/api/v1/consulta/orders | jq .
echo -e "\n"

# Test get orders by processed status
echo "Testing get orders by processed status (false)..."
curl -s http://localhost:8185/api/v1/consulta/orders/processed/false | jq .
echo -e "\n"

echo "Tests completed!"

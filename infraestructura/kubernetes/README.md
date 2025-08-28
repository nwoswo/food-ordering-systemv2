# Kafka Infrastructure - Kubernetes Deployment

This directory contains Kubernetes YAML manifests for deploying the complete Kafka infrastructure used in the Food Ordering System.

## 🏗️ Architecture

The infrastructure consists of:

- **PostgreSQL Database**: Primary database with logical replication enabled for Debezium
- **Kafka Cluster**: 3-node Kafka cluster using KRaft (no Zookeeper required)
- **Kafka Connect**: For data streaming and CDC (Change Data Capture)
- **Debezium Connector**: PostgreSQL connector for outbox pattern implementation
- **Kafka UI**: Web interface for monitoring and managing Kafka

## 📁 File Structure

```
kubernetes/
├── namespace.yaml                           # Namespace definition
├── postgres-configmap.yaml                  # PostgreSQL configuration
├── postgres-secret.yaml                     # PostgreSQL credentials
├── postgres-pvc.yaml                        # PostgreSQL persistent volume claim
├── postgres-deployment.yaml                 # PostgreSQL deployment
├── postgres-service.yaml                    # PostgreSQL service
├── kafka-configmap.yaml                     # Kafka configuration
├── kafka-pvcs.yaml                          # Kafka persistent volume claims
├── kafka-broker-1-deployment.yaml           # Kafka broker 1 deployment
├── kafka-broker-2-deployment.yaml           # Kafka broker 2 deployment
├── kafka-broker-3-deployment.yaml           # Kafka broker 3 deployment
├── kafka-services.yaml                      # Kafka broker services
├── kafka-init-job.yaml                      # Kafka topics initialization job
├── kafka-connect-deployment.yaml            # Kafka Connect deployment
├── kafka-connect-service.yaml               # Kafka Connect service
├── kafka-ui-deployment.yaml                 # Kafka UI deployment
├── kafka-ui-service.yaml                    # Kafka UI service
├── debezium-connector-configmap.yaml        # Debezium connector configuration
├── debezium-connector-job.yaml              # Debezium connector setup job
├── deploy.sh                                # Deployment script
├── cleanup.sh                               # Cleanup script
└── README.md                                # This file
```

## 🚀 Quick Start

### Prerequisites

- Kubernetes cluster (local or cloud)
- `kubectl` configured to access your cluster
- Storage class that supports `ReadWriteOnce` access mode

### Deployment

1. **Make scripts executable:**
   ```bash
   chmod +x deploy.sh cleanup.sh
   ```

2. **Deploy the infrastructure:**
   ```bash
   ./deploy.sh
   ```

3. **Access the services:**
   ```bash
   # Kafka UI
   kubectl port-forward -n kafka-infrastructure service/kafka-ui 8090:8090
   
   # Kafka Connect REST API
   kubectl port-forward -n kafka-infrastructure service/kafka-connect 8083:8083
   ```

### Cleanup

To remove the entire infrastructure:

```bash
./cleanup.sh
```

## 🔧 Configuration

### Storage

The deployment uses persistent volumes for:
- PostgreSQL data (10Gi)
- Kafka broker data (10Gi each)

Make sure your cluster has a storage class named `standard` or update the PVC configurations.

### Resource Limits

Default resource allocations:
- **PostgreSQL**: 256Mi-512Mi RAM, 250m-500m CPU
- **Kafka Brokers**: 512Mi-1Gi RAM, 500m-1000m CPU each
- **Kafka Connect**: 512Mi-1Gi RAM, 500m-1000m CPU
- **Kafka UI**: 256Mi-512Mi RAM, 250m-500m CPU

### Network Configuration

- **Namespace**: `kafka-infrastructure`
- **Kafka Cluster**: 3 brokers with replication factor 3
- **Services**: Internal communication via ClusterIP, UI exposed via LoadBalancer

## 📊 Monitoring

### Check Deployment Status

```bash
kubectl get all -n kafka-infrastructure
```

### View Logs

```bash
# PostgreSQL
kubectl logs -n kafka-infrastructure deployment/postgres

# Kafka brokers
kubectl logs -n kafka-infrastructure deployment/kafka-broker-1
kubectl logs -n kafka-infrastructure deployment/kafka-broker-2
kubectl logs -n kafka-infrastructure deployment/kafka-broker-3

# Kafka Connect
kubectl logs -n kafka-infrastructure deployment/kafka-connect

# Kafka UI
kubectl logs -n kafka-infrastructure deployment/kafka-ui
```

### Access Kafka UI

Once deployed, access the Kafka UI at `http://localhost:8090` (after port-forwarding) to:
- Monitor topics and partitions
- View consumer groups
- Browse messages
- Manage connectors

## 🔌 Kafka Connect

### Connector Status

Check Debezium connector status:

```bash
curl http://localhost:8083/connectors/order-outbox-connector/status
```

### Available Endpoints

- **List connectors**: `GET /connectors`
- **Get connector status**: `GET /connectors/{name}/status`
- **Get connector config**: `GET /connectors/{name}/config`
- **Create connector**: `POST /connectors`
- **Delete connector**: `DELETE /connectors/{name}`

## 🗄️ Database Configuration

PostgreSQL is configured with:
- Logical replication enabled (`wal_level = logical`)
- Outbox pattern support
- Optimized for CDC operations

### Connection Details

- **Host**: `postgres.kafka-infrastructure.svc.cluster.local`
- **Port**: `5432`
- **Database**: `postgres`
- **Username**: `postgres`
- **Password**: `admin`

## 🔄 Topics Created

The initialization job creates the following topics:
- `connect-configs` (1 partition, RF=3)
- `connect-offsets` (25 partitions, RF=3)
- `connect-status` (1 partition, RF=3)
- `payment-request` (3 partitions, RF=3)
- `payment-response` (3 partitions, RF=3)
- `restaurant-approval-request` (3 partitions, RF=3)
- `restaurant-approval-response` (3 partitions, RF=3)

## 🛠️ Troubleshooting

### Common Issues

1. **Storage Issues**
   ```bash
   kubectl get pvc -n kafka-infrastructure
   kubectl describe pvc <pvc-name> -n kafka-infrastructure
   ```

2. **Pod Startup Issues**
   ```bash
   kubectl describe pod <pod-name> -n kafka-infrastructure
   kubectl logs <pod-name> -n kafka-infrastructure
   ```

3. **Kafka Cluster Issues**
   ```bash
   # Check broker status
   kubectl exec -n kafka-infrastructure deployment/kafka-broker-1 -- kafka-broker-api-versions --bootstrap-server localhost:9092
   ```

4. **Connector Issues**
   ```bash
   # Check connector logs
   kubectl logs -n kafka-infrastructure deployment/kafka-connect
   
   # Check connector status
   curl http://localhost:8083/connectors/order-outbox-connector/status
   ```

### Scaling

To scale Kafka brokers:
```bash
kubectl scale deployment kafka-broker-1 --replicas=2 -n kafka-infrastructure
```

**Note**: Scaling Kafka requires careful consideration of partition assignments and replication factors.

## 🔐 Security Notes

- Default credentials are used for demonstration
- In production, use proper secrets management
- Consider enabling TLS for Kafka
- Implement proper RBAC policies

## 📝 Customization

To customize the deployment:

1. **Modify resource limits** in deployment files
2. **Update storage sizes** in PVC files
3. **Change Kafka configuration** in ConfigMaps
4. **Adjust replica counts** for high availability

## 🤝 Integration

This infrastructure is designed to work with the Food Ordering System microservices:
- Order Service
- Payment Service
- Restaurant Service
- Customer Service
- API Gateway

The services should be configured to connect to:
- **Kafka**: `kafka-broker-1:9092,kafka-broker-2:9092,kafka-broker-3:9092`
- **PostgreSQL**: `postgres:5432`

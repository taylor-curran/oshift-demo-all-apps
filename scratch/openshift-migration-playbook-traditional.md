## Playbook 1: Traditional Cloud Foundry Apps (High Complexity)
*Applies to: account-service, credit-scoring-engine, transaction-processor*

### Pre-Migration Assessment
- [ ] Inventory all CF service bindings and map to OpenShift operators
- [ ] Identify external dependencies and network requirements
- [ ] Assess resource requirements and cluster capacity
- [ ] Review Spring Cloud dependencies for replacement strategy

### Phase 1: Environment Preparation
1. **Install Required Operators**
   ```bash
   # Install PostgreSQL Operator
   oc apply -f https://operatorhub.io/install/postgresql.yaml
   
   # Install Redis Operator  
   oc apply -f https://operatorhub.io/install/redis-operator.yaml
   
   # Install Strimzi Kafka Operator (for transaction-processor)
   oc apply -f https://operatorhub.io/install/strimzi-kafka-operator.yaml
   ```

2. **Create Project/Namespace**
   ```bash
   oc new-project banking-services
   oc label namespace banking-services name=banking-services
   ```

### Phase 2: Application-Specific Migration

#### Account Service Migration
1. **Convert CF Manifest to OpenShift Deployment**
   ```yaml
   # account-service-deployment.yaml
   apiVersion: apps/v1
   kind: Deployment
   metadata:
     name: account-service
     labels:
       app: account-service
   spec:
     replicas: 3  # From CF manifest instances: 3
     selector:
       matchLabels:
         app: account-service
     template:
       metadata:
         labels:
           app: account-service
       spec:
         containers:
         - name: account-service
           image: account-service:1.2.0
           ports:
           - containerPort: 8080
           - containerPort: 8081  # Management port
           resources:
             requests:
               memory: "1Gi"      # From CF memory: 1024M
               cpu: "500m"
             limits:
               memory: "1Gi"
               cpu: "1000m"
           env:
           - name: SPRING_PROFILES_ACTIVE
             value: "production"
           - name: DB_POOL_SIZE
             value: "20"
           livenessProbe:
             httpGet:
               path: /actuator/health
               port: 8081
             initialDelaySeconds: 60
           readinessProbe:
             httpGet:
               path: /actuator/health
               port: 8081
             initialDelaySeconds: 30
   ```

2. **Replace Eureka with Service Mesh**
   - Remove `spring-cloud-starter-netflix-eureka-client` dependency
   - Configure Istio service mesh for service discovery
   - Update application.properties to disable Eureka

3. **Migrate Service Bindings**
   ```yaml
   # postgresql-instance.yaml
   apiVersion: postgresql.cnpg.io/v1
   kind: Cluster
   metadata:
     name: account-postgres
   spec:
     instances: 2
     postgresql:
       parameters:
         max_connections: "200"
         shared_buffers: "256MB"
   ```

#### Credit Scoring Engine Migration (Complex Multi-Buildpack)
1. **Create Custom Container Image**
   ```dockerfile
   # Dockerfile
   FROM openjdk:17-jdk-slim as java-base
   FROM python:3.9-slim as python-base
   
   FROM java-base
   COPY --from=python-base /usr/local /usr/local
   COPY target/credit-scoring-engine-3.1.0.jar app.jar
   COPY models/ /models/
   
   EXPOSE 8080
   ENTRYPOINT ["java", "-Xmx2560m", "-XX:+UseG1GC", "-jar", "/app.jar"]
   ```

2. **Handle High Memory Requirements**
   ```yaml
   resources:
     requests:
       memory: "3Gi"      # From CF memory: 3072M
       cpu: "1000m"
     limits:
       memory: "3Gi"
       cpu: "2000m"
   ```

3. **Migrate ML Model Storage**
   ```yaml
   # model-storage-pvc.yaml
   apiVersion: v1
   kind: PersistentVolumeClaim
   metadata:
     name: ml-models-storage
   spec:
     accessModes:
       - ReadWriteOnce
     resources:
       requests:
         storage: 10Gi
   ```

#### Transaction Processor Migration (High-Throughput)
1. **Configure HorizontalPodAutoscaler**
   ```yaml
   # transaction-processor-hpa.yaml
   apiVersion: autoscaling/v2
   kind: HorizontalPodAutoscaler
   metadata:
     name: transaction-processor-hpa
   spec:
     scaleTargetRef:
       apiVersion: apps/v1
       kind: Deployment
       name: transaction-processor
     minReplicas: 5    # From CF instances: 5
     maxReplicas: 10
     metrics:
     - type: Resource
       resource:
         name: cpu
         target:
           type: Utilization
           averageUtilization: 70
   ```

2. **Migrate Database Sharding**
   ```yaml
   # Create 4 separate PostgreSQL instances for sharding
   # postgresql-shard-1.yaml through postgresql-shard-4.yaml
   apiVersion: postgresql.cnpg.io/v1
   kind: Cluster
   metadata:
     name: transaction-db-shard-1
   spec:
     instances: 2
     postgresql:
       parameters:
         max_connections: "500"
   ```

3. **Configure Kafka with Strimzi**
   ```yaml
   # kafka-cluster.yaml
   apiVersion: kafka.strimzi.io/v1beta2
   kind: Kafka
   metadata:
     name: transaction-kafka
   spec:
     kafka:
       replicas: 3
       config:
         num.partitions: 12
         default.replication.factor: 3
   ```

### Phase 3: Service Configuration
1. **Create Services and Routes**
   ```yaml
   # account-service-service.yaml
   apiVersion: v1
   kind: Service
   metadata:
     name: account-service
   spec:
     selector:
       app: account-service
     ports:
     - name: http
       port: 8080
       targetPort: 8080
   ---
   apiVersion: route.openshift.io/v1
   kind: Route
   metadata:
     name: account-api
   spec:
     host: account-api.apps.banking.com
     to:
       kind: Service
       name: account-service
   ```

### Phase 4: Migration Gotchas & Validation
- [ ] **Service Discovery**: Verify all inter-service communication works without Eureka
- [ ] **Health Checks**: Confirm Kubernetes probes replace CF health checks
- [ ] **Resource Limits**: Monitor memory usage, especially for credit-scoring-engine
- [ ] **Database Connections**: Validate connection pooling and sharding logic
- [ ] **External APIs**: Test credit bureau and payment processor integrations

---


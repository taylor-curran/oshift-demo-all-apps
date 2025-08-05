# Infrastructure Artifact Files with Full Content

## 1. Account Service (Java Spring Boot)

### Cloud Foundry Artifacts

#### `manifest.yml` - CF deployment manifest
```yaml
---
applications:
- name: account-service
  instances: 3
  memory: 1024M
  disk_quota: 2G
  stack: cflinuxfs4
  buildpacks:
    - java_buildpack
  path: ./target/account-service-1.2.0.jar
  
  env:
    SPRING_PROFILES_ACTIVE: production
    JBP_CONFIG_OPEN_JDK_JRE: '[jre: {version: 17.+}]'
    SERVER_PORT: 8080
    MANAGEMENT_SERVER_PORT: 8081
    
    # Database Configuration
    DB_POOL_SIZE: 20
    DB_CONNECTION_TIMEOUT: 30000
    DB_IDLE_TIMEOUT: 600000
    
    # Service Discovery
    EUREKA_CLIENT_ENABLED: true
    EUREKA_INSTANCE_HOSTNAME: account-service
    
    # Monitoring
    NEWRELIC_ENABLED: true
    DATADOG_TRACE_ENABLED: true
    
  services:
    - account-postgres-db
    - account-redis-cache
    - config-server
    - service-registry
    - circuit-breaker-dashboard
    
  routes:
    - route: account-api.apps.banking.com
    - route: account-internal.apps.banking.com
      
  health-check-type: http
  health-check-http-endpoint: /actuator/health
  health-check-invocation-timeout: 10
  
  command: java -jar account-service-1.2.0.jar --server.port=$PORT
```

### Kubernetes/OpenShift Artifacts

#### `Dockerfile` - Container image definition
```dockerfile
FROM registry.access.redhat.com/ubi8/openjdk-17:latest

# Set working directory
WORKDIR /deployments

# Copy the jar file
COPY target/account-service-*.jar app.jar

# Expose ports
EXPOSE 8080 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### `k8s/deployment.yaml` - Kubernetes deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: account-service
  namespace: demo-apps
  labels:
    app: account-service
    app.kubernetes.io/name: account-service
    app.kubernetes.io/version: "1.2.0"
    version: v1
spec:
  replicas: 3
  selector:
    matchLabels:
      app: account-service
  template:
    metadata:
      labels:
        app: account-service
        app.kubernetes.io/name: account-service
        version: v1
    spec:
      containers:
      - name: account-service
        image: account-service:latest
        imagePullPolicy: Never
        ports:
        - containerPort: 8080
          name: http
        - containerPort: 8081
          name: management
        envFrom:
        - configMapRef:
            name: account-service-config
        - secretRef:
            name: account-service-secrets
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8081
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8081
          initialDelaySeconds: 5
          periodSeconds: 5
          timeoutSeconds: 3
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
```

#### `k8s/service.yaml` - Kubernetes service
```yaml
apiVersion: v1
kind: Service
metadata:
  name: account-service
  namespace: demo-apps
  labels:
    app: account-service
    app.kubernetes.io/name: account-service
spec:
  selector:
    app: account-service
  ports:
  - name: http
    port: 80
    targetPort: 8080
    protocol: TCP
  - name: management
    port: 8081
    targetPort: 8081
    protocol: TCP
  type: ClusterIP
```

#### `k8s/configmap.yaml` - Configuration map
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: account-service-config
  namespace: demo-apps
  labels:
    app: account-service
data:
  SPRING_PROFILES_ACTIVE: "production"
  SERVER_PORT: "8080"
  MANAGEMENT_SERVER_PORT: "8081"
  DB_POOL_SIZE: "20"
  DB_CONNECTION_TIMEOUT: "30000"
  DB_IDLE_TIMEOUT: "600000"
  EUREKA_CLIENT_ENABLED: "true"
  EUREKA_INSTANCE_HOSTNAME: "account-service"
  NEWRELIC_ENABLED: "true"
  DATADOG_TRACE_ENABLED: "true"
```

---

## 3. Credit Scoring Engine (Java Spring Boot)

### Cloud Foundry Artifacts

#### `manifest.yml` - CF deployment configuration
```yaml
---
applications:
- name: credit-scoring-engine
  instances: 4
  memory: 3072M
  disk_quota: 5G
  stack: cflinuxfs4
  buildpacks:
    - java_buildpack
    - python_buildpack
  path: ./target/credit-scoring-engine-3.1.0.jar
  
  env:
    SPRING_PROFILES_ACTIVE: production,scoring
    JBP_CONFIG_OPEN_JDK_JRE: '[jre: {version: 17.+}]'
    JVM_OPTS: "-Xmx2560m -XX:+UseG1GC -XX:+UseStringDeduplication"
    
    # Credit Bureau APIs
    EXPERIAN_API_URL: "https://api.experian.com/credit"
    EQUIFAX_API_URL: "https://api.equifax.com/ews"
    TRANSUNION_API_URL: "https://api.transunion.com/credit"
    CREDIT_API_TIMEOUT: "15000"
    
    # Scoring Models
    FICO_MODEL_VERSION: "9.0"
    VANTAGE_MODEL_VERSION: "4.0"
    CUSTOM_MODEL_PATH: "/models/proprietary-score-v2.3.pkl"
    
    # Data Sources
    INCOME_VERIFICATION_API: "https://api.theworknumber.com"
    BANK_STATEMENT_ANALYZER: "https://api.plaid.com/v2"
    
    # Risk Thresholds
    MIN_CREDIT_SCORE: "580"
    MAX_DTI_RATIO: "0.43"
    MIN_INCOME_VERIFICATION: "true"
    
    # Compliance & Regulation
    FCRA_COMPLIANCE_MODE: "true"
    ECOA_COMPLIANCE_MODE: "true"
    ADVERSE_ACTION_NOTIFICATIONS: "true"
    
    # Machine Learning
    ML_FEATURE_COUNT: "247"
    MODEL_REFRESH_INTERVAL: "24h"
    A_B_TEST_ENABLED: "true"
    
  services:
    - credit-postgres-primary
    - credit-postgres-replica
    - credit-redis-cluster
    - model-storage-s3
    - credit-bureau-proxy
    - encryption-service
    - audit-trail-kafka
    
  routes:
    - route: credit-scoring.internal.banking.com
    - route: credit-api-v3.banking.com
      
  health-check-type: http
  health-check-http-endpoint: /actuator/health/detailed
  health-check-invocation-timeout: 20
```

### Note
No Dockerfile or Kubernetes manifests - represents traditional CF app requiring containerization during migration.

---

## 4. Customer Portal (Node.js/Express)

### Cloud Foundry Artifacts

#### `manifest.yml` - CF deployment configuration
```yaml
---
applications:
- name: customer-portal
  instances: 2
  memory: 512M
  disk_quota: 1G
  stack: cflinuxfs4
  buildpacks:
    - nodejs_buildpack
  path: ./dist
  
  env:
    NODE_ENV: production
    PORT: 8080
    
    # API Backend Configuration
    API_BASE_URL: "https://account-api.apps.banking.com"
    PAYMENT_API_URL: "https://payment-gateway.apps.banking.com"
    
    # Authentication
    AUTH_PROVIDER: "oauth2"
    OAUTH_CLIENT_ID: "customer-portal-prod"
    OAUTH_REDIRECT_URL: "https://portal.banking.com/auth/callback"
    
    # Content Security Policy
    CSP_POLICY: "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'"
    
    # Feature Flags
    FEATURE_MOBILE_DEPOSIT: "true"
    FEATURE_WIRE_TRANSFERS: "true"
    FEATURE_INVESTMENT_TRACKING: "false"
    
    # Analytics
    GOOGLE_ANALYTICS_ID: "GA-BANKING-PORTAL"
    HOTJAR_ID: "2847593"
    
  services:
    - portal-redis-session
    - content-delivery-network
    
  routes:
    - route: portal.banking.com
    - route: mobile.banking.com
      
  health-check-type: http
  health-check-http-endpoint: /health
  
  command: npm start
```

### Note
No Dockerfile or Kubernetes manifests - represents traditional CF Node.js app.

---

## 5. Fraud Detection Worker (Python)

### Cloud Foundry Artifacts

#### `fraud-worker-app.json` - Korifi app definition
```json
{
  "name": "fraud-detection-worker",
  "lifecycle": {
    "type": "buildpack",
    "data": {
      "stack": "paketo-buildpacks-stacks-jammy",
      "buildpacks": [
        "gcr.io/paketo-buildpacks/python",
        "gcr.io/paketo-buildpacks/pip"
      ]
    }
  },
  "relationships": {
    "space": { "data": { "guid": "ml-services" } }
  },
  "metadata": {
    "labels": {
      "app.kubernetes.io/name": "fraud-detection-worker",
      "app.kubernetes.io/component": "ml-worker",
      "team": "fraud-prevention",
      "ml-model-version": "v2.1.3"
    }
  }
}
```

#### `worker-config.yml` - Environment configuration
```yaml
environment_variables:
  # Python Configuration
  PYTHONPATH: "/workspace/src"
  PYTHON_VERSION: "3.11"
  
  # ML Model Configuration
  MODEL_PATH: "/models/fraud-detection-v2.1.3.pkl"
  MODEL_THRESHOLD: "0.75"
  BATCH_SIZE: "100"
  
  # Queue Configuration
  RABBITMQ_URL: "amqp://fraud-queue:5672"
  QUEUE_NAME: "transaction-analysis"
  PREFETCH_COUNT: "10"
  
  # Database
  POSTGRES_URL: "postgresql://fraud-db:5432/fraud_analytics"
  REDIS_URL: "redis://model-cache:6379"
  
  # Feature Engineering
  FEATURE_STORE_ENDPOINT: "http://feature-store:8080/api/v1"
  TRANSACTION_HISTORY_DAYS: "90"
  
  # Monitoring
  PROMETHEUS_PORT: "9090"
  LOG_LEVEL: "INFO"
  SENTRY_DSN: "((fraud-sentry-dsn))"

services:
  - fraud-postgres
  - fraud-rabbitmq
  - model-cache-redis
```

### Kubernetes/OpenShift Artifacts

#### `Dockerfile` - Python container
```dockerfile
# Minimal Dockerfile for fraud-detection-worker (Python)
FROM python:3.9-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY src/ ./src/

EXPOSE 8080
CMD ["python", "src/fraud_worker.py"]
```

---

## 6. Legacy Mainframe Adapter (Java Spring Boot)

### Cloud Foundry Artifacts

#### `manifest.yml` - CF deployment configuration
```yaml
---
applications:
- name: legacy-mainframe-adapter
  instances: 2
  memory: 4096M
  disk_quota: 8G
  stack: cflinuxfs4
  buildpacks:
    - java_buildpack
    - binary_buildpack
  path: ./target/mainframe-adapter-1.8.2.jar
  
  env:
    SPRING_PROFILES_ACTIVE: production,mainframe,legacy-integration
    JBP_CONFIG_OPEN_JDK_JRE: '[jre: {version: 11.+}]'  # Legacy Java version requirement
    JVM_OPTS: "-Xmx3584m -XX:+UseConcMarkSweepGC -XX:MaxPermSize=512m"
    
    # IBM Mainframe Connection
    CICS_REGION: "BANKPROD"
    CICS_HOST: "mainframe.banking.internal"
    CICS_PORT: "23"
    CICS_USER_ID: "CFADPTER"
    CICS_TRANSACTION_TIMEOUT: "30000"
    
    # IMS Database
    IMS_CONNECT_HOST: "ims.banking.internal"
    IMS_CONNECT_PORT: "9999"
    IMS_DATABASE: "CUSTOMER"
    IMS_PSB: "CUSTPSB1"
    
    # DB2 Mainframe
    DB2_SUBSYSTEM: "DBPROD01"
    DB2_LOCATION: "SYSPLEX.BANKING.COM"
    DB2_COLLECTION_ID: "BANKING"
    DB2_PACKAGE_SET: "BKGPKG01"
    
    # Message Queue (IBM MQ)
    MQ_QUEUE_MANAGER: "BANKQM01"
    MQ_CHANNEL: "SYSTEM.DEF.SVRCONN"
    MQ_HOST: "mqseries.banking.internal"
    MQ_PORT: "1414"
    MQ_REQUEST_QUEUE: "BANK.REQUEST.QUEUE"
    MQ_RESPONSE_QUEUE: "BANK.RESPONSE.QUEUE"
    
    # Legacy Protocol Support
    SNA_LU_NAME: "BANKAPP1"
    TN3270_SCREEN_SIZE: "24x80"
    EBCDIC_CODEPAGE: "CP037"
    
    # Error Handling & Resilience
    MAINFRAME_CIRCUIT_BREAKER_THRESHOLD: "5"
    RETRY_ATTEMPTS: "3"
    FALLBACK_MODE_ENABLED: "true"
    LEGACY_TIMEOUT_MS: "45000"
    
    # Security
    RACF_USER_ID: "CFUSR001"
    SSL_KEYSTORE_PATH: "/security/mainframe-keystore.jks"
    ENCRYPTION_ALGORITHM: "DES3"
    
    # Monitoring & Alerting
    MAINFRAME_HEALTH_CHECK_INTERVAL: "60s"
    LEGACY_SYSTEM_ALERTS_ENABLED: "true"
    PERFORMANCE_MONITORING: "true"
    
    # Data Transformation
    COPYBOOK_PATH: "/copybooks"
    COBOL_RECORD_LAYOUTS: "/layouts/customer-record.cpy"
    CHARACTER_ENCODING: "IBM037"
    
  services:
    - mainframe-connection-pool
    - legacy-security-manager
    - transaction-coordinator
    - error-recovery-service
    - monitoring-collector
    - ssl-certificate-store
    
  routes:
    - route: legacy-adapter.internal.banking.com
      
  health-check-type: http
  health-check-http-endpoint: /actuator/health/mainframe
  health-check-invocation-timeout: 45
  
  # Special configuration for legacy systems
  command: java -Djava.library.path=/opt/ibm/cics -jar mainframe-adapter-1.8.2.jar
```

### Note
No Dockerfile or Kubernetes manifests - represents pre-containerization legacy integration layer.

---

## 2. Audit Logger (Go)

### Cloud Foundry Artifacts

#### `audit-app.json` - Korifi app definition with Go buildpack
```json
{
  "name": "audit-logger",
  "lifecycle": {
    "type": "buildpack",
    "data": {
      "stack": "paketo-buildpacks-stacks-jammy",
      "buildpacks": [
        "gcr.io/paketo-buildpacks/go"
      ]
    }
  },
  "relationships": {
    "space": { "data": { "guid": "compliance" } }
  },
  "metadata": {
    "labels": {
      "app.kubernetes.io/name": "audit-logger",
      "app.kubernetes.io/component": "logging",
      "team": "compliance",
      "compliance-level": "sox-pci"
    }
  }
}
```

#### `service-config.yml` - Environment variables for S3, NATS, compliance modes
```yaml
environment_variables:
  # Go Configuration
  GOMAXPROCS: "2"
  CGO_ENABLED: "0"
  
  # Audit Configuration
  LOG_RETENTION_DAYS: "2555"  # 7 years for compliance
  ENCRYPTION_KEY_ID: "audit-encryption-key-v3"
  AUDIT_BUFFER_SIZE: "1000"
  
  # Storage
  S3_BUCKET: "banking-audit-logs-prod"
  S3_REGION: "us-east-1"
  
  # Message Queue
  NATS_URL: "nats://audit-nats:4222"
  AUDIT_SUBJECT: "banking.audit.events"
  
  # Compliance
  PCI_COMPLIANCE_MODE: "true"
  SOX_COMPLIANCE_MODE: "true"
  GDPR_COMPLIANCE_MODE: "true"
  
  # Monitoring
  PROMETHEUS_PORT: "9090"
  LOG_LEVEL: "INFO"

services:
  - audit-s3-storage
  - audit-nats-queue
```

### Kubernetes/OpenShift Artifacts

#### `Dockerfile` - Multi-stage Go build
```dockerfile
# Minimal Dockerfile for audit-logger (Go)
FROM golang:1.21-alpine AS builder

WORKDIR /app
COPY go.mod ./
RUN go mod download

COPY src/ ./src/
RUN go build -o audit-logger ./src

FROM alpine:latest
RUN apk --no-cache add ca-certificates
WORKDIR /root/

COPY --from=builder /app/audit-logger .

EXPOSE 8080
CMD ["./audit-logger"]
```

### Build Configuration

#### `go.mod` - Go module dependencies
```go
module audit-logger

go 1.21

require (
	github.com/aws/aws-sdk-go v1.44.225
	github.com/nats-io/nats.go v1.25.0
	github.com/prometheus/client_golang v1.14.0
)
```

---

## 7. Notification Service (Node.js)

### Cloud Foundry Artifacts

#### `notification-app.json` - Korifi app definition
```json
{
  "name": "notification-service",
  "lifecycle": {
    "type": "buildpack",
    "data": {
      "stack": "paketo-buildpacks-stacks-jammy",
      "buildpacks": [
        "gcr.io/paketo-buildpacks/nodejs",
        "gcr.io/paketo-buildpacks/npm"
      ]
    }
  },
  "relationships": {
    "space": { "data": { "guid": "communications" } }
  },
  "metadata": {
    "labels": {
      "app.kubernetes.io/name": "notification-service",
      "app.kubernetes.io/component": "communications",
      "team": "customer-experience"
    }
  }
}
```

#### `notification-config.yml` - Environment configuration
```yaml
environment_variables:
  # Node.js Configuration
  NODE_ENV: "production"
  NODE_OPTIONS: "--max-old-space-size=512"
  
  # Multi-channel Configuration
  EMAIL_PROVIDER: "sendgrid"
  SMS_PROVIDER: "twilio"
  PUSH_PROVIDER: "firebase"
  
  # Email Settings
  SENDGRID_API_KEY: "((sendgrid-api-key))"
  EMAIL_FROM_ADDRESS: "noreply@banking.com"
  EMAIL_REPLY_TO: "support@banking.com"
  
  # SMS Settings  
  TWILIO_ACCOUNT_SID: "((twilio-account-sid))"
  TWILIO_AUTH_TOKEN: "((twilio-auth-token))"
  SMS_FROM_NUMBER: "+15551234567"
  
  # Push Notifications
  FCM_SERVER_KEY: "((firebase-server-key))"
  APNS_KEY_ID: "((apns-key-id))"
  
  # Message Queue
  RABBITMQ_URL: "amqp://notification-queue:5672"
  QUEUE_PREFETCH: "50"
  
  # Template Engine
  TEMPLATE_CACHE_TTL: "3600"
  TEMPLATE_ENGINE: "handlebars"
  
  # Rate Limiting
  EMAIL_RATE_LIMIT: "1000/hour"
  SMS_RATE_LIMIT: "500/hour"
  PUSH_RATE_LIMIT: "10000/hour"
  
  # Monitoring
  LOG_LEVEL: "info"
  METRICS_PORT: "9090"

services:
  - notification-rabbitmq
  - template-storage
```

### Kubernetes/OpenShift Artifacts

#### `Dockerfile` - Node.js container
```dockerfile
# Minimal Dockerfile for notification-service (Node.js)
FROM node:18-alpine

WORKDIR /app

COPY package*.json ./
RUN npm ci --only=production

COPY src/ ./src/

EXPOSE 8080
CMD ["node", "src/notification-service.js"]
```

---

## 8. Payment Gateway API (Java Spring Boot)

### Cloud Foundry Artifacts

#### `payment-gateway-app.json` - Korifi app definition
```json
{
  "name": "payment-gateway-api",
  "lifecycle": {
    "type": "buildpack",
    "data": {
      "stack": "paketo-buildpacks-stacks-jammy",
      "buildpacks": [
        "gcr.io/paketo-buildpacks/java",
        "gcr.io/paketo-buildpacks/spring-boot"
      ]
    }
  },
  "relationships": {
    "space": { "data": { "guid": "production" } }
  },
  "metadata": {
    "labels": {
      "app.kubernetes.io/name": "payment-gateway-api",
      "app.kubernetes.io/component": "api-gateway",
      "team": "payments",
      "environment": "production"
    },
    "annotations": {
      "config.alpha.coreos.com/ignore": "true",
      "deployment.kubernetes.io/revision": "3"
    }
  }
}
```

#### `env-vars.yml` - Environment configuration
```yaml
environment_variables:
  # Application Configuration
  SPRING_PROFILES_ACTIVE: "production,payments"
  SERVER_PORT: "8080"
  MANAGEMENT_SERVER_PORT: "8081"
  
  # Payment Processing
  PAYMENT_PROCESSOR_ENDPOINT: "https://api.stripe.com/v1"
  PAYMENT_TIMEOUT_MS: "30000"
  MAX_PAYMENT_AMOUNT: "10000.00"
  CURRENCY_DEFAULT: "USD"
  
  # Redis Cache Configuration
  REDIS_URL: "redis://redis-cache:6379"
  REDIS_TIMEOUT: "5000"
  CACHE_TTL_SECONDS: "3600"
  
  # Security & Authentication
  JWT_SECRET_KEY: "((payment-jwt-secret))"
  API_KEY_HEADER: "X-API-Key"
  RATE_LIMIT_REQUESTS_PER_MINUTE: "1000"
  
  # Monitoring & Observability
  OTEL_EXPORTER_JAEGER_ENDPOINT: "http://jaeger:14268/api/traces"
  METRICS_EXPORT_INTERVAL: "60s"
  LOG_LEVEL: "INFO"
  
  # Feature Flags
  FEATURE_INTERNATIONAL_PAYMENTS: "true"
  FEATURE_RECURRING_PAYMENTS: "true"
  FEATURE_FRAUD_CHECK: "true"
```

### Kubernetes/OpenShift Artifacts

#### `Dockerfile` - Spring Boot container
```dockerfile
# Minimal Dockerfile for payment-gateway-api (Java Spring Boot)
FROM maven:3.8-openjdk-17 AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=builder /app/target/payment-gateway-api-*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

---

## 9. Risk Analytics (C#/.NET)

### Cloud Foundry Artifacts

#### `risk-app.json` - Korifi app definition
```json
{
  "name": "risk-analytics",
  "lifecycle": {
    "type": "buildpack",
    "data": {
      "stack": "paketo-buildpacks-stacks-jammy",
      "buildpacks": [
        "gcr.io/paketo-buildpacks/dotnet-core"
      ]
    }
  },
  "relationships": {
    "space": { "data": { "guid": "risk-management" } }
  },
  "metadata": {
    "labels": {
      "app.kubernetes.io/name": "risk-analytics",
      "app.kubernetes.io/component": "analytics-engine",
      "team": "risk-management",
      "model-type": "real-time"
    }
  }
}
```

#### `analytics-config.yml` - Environment configuration
```yaml
environment_variables:
  # .NET Configuration
  DOTNET_ENVIRONMENT: "Production"
  ASPNETCORE_URLS: "http://+:8080"
  DOTNET_gcServer: "1"
  
  # Real-time Analytics
  STREAM_PROCESSING_MODE: "real-time"
  WINDOW_SIZE_MINUTES: "5"
  AGGREGATION_INTERVAL: "30s"
  
  # Data Sources
  KAFKA_BOOTSTRAP_SERVERS: "kafka-cluster:9092"
  KAFKA_CONSUMER_GROUP: "risk-analytics"
  TRANSACTION_TOPIC: "banking.transactions"
  MARKET_DATA_TOPIC: "market.prices"
  
  # Risk Models
  VAR_MODEL_VERSION: "3.2.1"
  STRESS_TEST_SCENARIOS: "baseline,adverse,severely-adverse"
  RISK_HORIZON_DAYS: "250"
  CONFIDENCE_LEVEL: "0.99"
  
  # Time Series Database
  INFLUXDB_URL: "http://timeseries-db:8086"
  INFLUXDB_BUCKET: "risk-metrics"
  RETENTION_POLICY: "30d"
  
  # Machine Learning
  ML_MODEL_ENDPOINT: "http://ml-models:8080/predict"
  FEATURE_ENGINEERING_PIPELINE: "v2.1"
  MODEL_REFRESH_HOURS: "4"
  
  # Regulatory Reporting
  BASEL_III_COMPLIANCE: "true"
  CCAR_REPORTING: "true"
  LIQUIDITY_RATIOS: "true"
  
  # Alerting Thresholds
  CREDIT_RISK_THRESHOLD: "0.02"
  MARKET_RISK_THRESHOLD: "0.015"
  OPERATIONAL_RISK_THRESHOLD: "0.01"
  
  # Performance
  BATCH_SIZE: "1000"
  PARALLEL_PROCESSING: "true"
  CACHE_SIZE_MB: "512"
  
  # Monitoring
  PROMETHEUS_METRICS: "true"
  LOG_LEVEL: "Information"

services:
  - risk-kafka
  - timeseries-influxdb
  - ml-model-service
  - regulatory-storage
```

### Kubernetes/OpenShift Artifacts

#### `Dockerfile` - .NET container
```dockerfile
# Minimal Dockerfile for risk-analytics (.NET Core)
FROM mcr.microsoft.com/dotnet/sdk:6.0 AS builder

WORKDIR /app
COPY *.csproj ./
RUN dotnet restore

COPY . ./
RUN dotnet publish -c Release -o out

FROM mcr.microsoft.com/dotnet/aspnet:6.0
WORKDIR /app

COPY --from=builder /app/out .

EXPOSE 8080
ENV ASPNETCORE_URLS=http://+:8080
CMD ["dotnet", "RiskAnalytics.dll"]
```

---

## 10. Transaction Processor (Java Spring Boot)

### Cloud Foundry Artifacts

#### `manifest.yml` - CF deployment configuration
```yaml
---
applications:
- name: transaction-processor
  instances: 5
  memory: 2048M
  disk_quota: 4G
  stack: cflinuxfs4
  buildpacks:
    - java_buildpack
  path: ./target/transaction-processor-2.0.1.jar
  
  env:
    SPRING_PROFILES_ACTIVE: production,high-throughput
    JBP_CONFIG_OPEN_JDK_JRE: '[jre: {version: 17.+}]'
    JVM_OPTS: "-Xmx1536m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
    
    # High-throughput processing
    PROCESSOR_THREAD_POOL_SIZE: 50
    BATCH_SIZE: 1000
    PROCESSING_TIMEOUT_MS: 5000
    
    # Kafka Configuration
    KAFKA_BOOTSTRAP_SERVERS: "kafka-cluster:9092"
    KAFKA_CONSUMER_GROUP: "transaction-processors"
    KAFKA_AUTO_OFFSET_RESET: "earliest"
    KAFKA_MAX_POLL_RECORDS: 500
    
    # Database sharding
    DB_SHARD_COUNT: 4
    DB_WRITE_TIMEOUT: 3000
    DB_READ_TIMEOUT: 1000
    
    # Circuit breaker
    HYSTRIX_ENABLED: true
    CIRCUIT_BREAKER_THRESHOLD: 20
    
  services:
    - transaction-kafka
    - transaction-db-shard-1
    - transaction-db-shard-2
    - transaction-db-shard-3
    - transaction-db-shard-4
    - transaction-redis-cluster
    - audit-service
    - metrics-collector
    
  routes:
    - route: transaction-processor.internal.banking.com
      
  health-check-type: http
  health-check-http-endpoint: /actuator/health
  health-check-invocation-timeout: 15
```

### Note
No Dockerfile or Kubernetes manifests - represents traditional high-throughput CF app requiring containerization.

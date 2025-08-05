# Infrastructure Artifacts Inventory - CF to OpenShift Migration Demo

## Overview
This inventory catalogs all infrastructure artifacts for 10 demo applications simulating enterprise banking workloads. Each app demonstrates different migration patterns from Cloud Foundry to Kubernetes/OpenShift.

---

## 1. Account Service (Java Spring Boot)
**Simulates**: Enterprise account management system with database operations, caching, and service discovery  
**Actually does**: Returns static account balances and validates basic transfer logic

### Infrastructure Artifacts:
- **Cloud Foundry**
  - `manifest.yml` - CF deployment manifest with 3 instances, 1GB memory, Java buildpack
  - Configures services: postgres-db, redis-cache, config-server, service-registry, circuit-breaker
  - Routes: account-api.apps.banking.com, account-internal.apps.banking.com
- **Kubernetes/OpenShift** 
  - `Dockerfile` - Container image definition
  - `k8s/deployment.yaml` - K8s deployment with 3 replicas, health probes, resource limits
  - `k8s/service.yaml` - ClusterIP service definition
  - `k8s/configmap.yaml` - Environment configuration
- **Build**: `pom.xml` - Maven build configuration

---

## 2. Audit Logger (Go)
**Simulates**: Compliance-focused audit trail system with encryption and 7-year retention  
**Actually does**: Generates audit event IDs and validates basic compliance rules

### Infrastructure Artifacts:
- **Cloud Foundry**
  - `audit-app.json` - Korifi app definition with Go buildpack
  - `service-config.yml` - Environment variables for S3, NATS, compliance modes
  - Services: audit-s3-storage, audit-nats-queue
- **Kubernetes/OpenShift**
  - `Dockerfile` - Multi-stage Go build
- **Build**: `go.mod` - Go module dependencies

---

## 3. Credit Scoring Engine (Java Spring Boot)
**Simulates**: ML-powered credit scoring system with feature engineering  
**Actually does**: Returns hardcoded credit scores based on simple rules

### Infrastructure Artifacts:
- **Cloud Foundry**
  - `manifest.yml` - CF deployment configuration (traditional, no containerization)
- **Build**: `pom.xml` - Maven build configuration
- **Note**: No Dockerfile or K8s manifests (represents legacy non-containerized app)

---

## 4. Customer Portal (Node.js/Express)
**Simulates**: Customer-facing web portal with React frontend and Redis sessions  
**Actually does**: Simple login/logout with static account summaries

### Infrastructure Artifacts:
- **Cloud Foundry**
  - `manifest.yml` - CF deployment with Node.js buildpack
- **Build**: 
  - `package.json` - NPM dependencies and scripts
  - `package-lock.json` - Dependency lock file
- **Note**: No Dockerfile or K8s manifests (represents traditional CF app)

---

## 5. Fraud Detection Worker (Python)
**Simulates**: ML-based fraud detection with model inference and batch processing  
**Actually does**: Simple threshold-based fraud scoring using basic rules

### Infrastructure Artifacts:
- **Cloud Foundry**
  - `fraud-worker-app.json` - Korifi app definition
  - `worker-config.yml` - ML model paths, RabbitMQ config, feature store settings
  - Services: fraud-postgres, fraud-rabbitmq, model-cache-redis
- **Kubernetes/OpenShift**
  - `Dockerfile` - Python 3.11 container
- **Build**: `requirements.txt` - Python dependencies

---

## 6. Legacy Mainframe Adapter (Java Spring Boot)
**Simulates**: COBOL mainframe integration adapter with protocol translation  
**Actually does**: Returns static responses mimicking mainframe data

### Infrastructure Artifacts:
- **Cloud Foundry**
  - `manifest.yml` - CF deployment configuration (traditional)
- **Build**: `pom.xml` - Maven build configuration
- **Note**: No Dockerfile or K8s manifests (represents pre-containerization legacy system)

---

## 7. Notification Service (Node.js)
**Simulates**: Multi-channel notification system (email, SMS, push) with template engine  
**Actually does**: Returns success responses without actual notification sending

### Infrastructure Artifacts:
- **Cloud Foundry**
  - `notification-app.json` - Korifi app with Node.js buildpack
  - `notification-config.yml` - SendGrid, Twilio, Firebase config, rate limits
  - Services: notification-rabbitmq, template-storage
- **Kubernetes/OpenShift**
  - `Dockerfile` - Node.js container
- **Build**: 
  - `package.json` - NPM dependencies
  - `package-lock.json` - Dependency lock file

---

## 8. Payment Gateway API (Java Spring Boot)
**Simulates**: Payment processing gateway with Stripe integration and fraud checks  
**Actually does**: Validates payment amounts and returns mock transaction IDs

### Infrastructure Artifacts:
- **Cloud Foundry**
  - `payment-gateway-app.json` - Korifi app with Java/Spring buildpacks
  - `env-vars.yml` - JWT config, Redis cache, feature flags
- **Kubernetes/OpenShift**
  - `Dockerfile` - Spring Boot container
- **Build**: `pom.xml` - Maven build configuration

---

## 9. Risk Analytics (C#/.NET)
**Simulates**: Real-time risk analysis with Kafka streaming and ML models  
**Actually does**: Returns static risk scores and compliance metrics

### Infrastructure Artifacts:
- **Cloud Foundry**
  - `risk-app.json` - Korifi app with .NET Core buildpack
  - `analytics-config.yml` - Kafka config, InfluxDB, ML endpoints, Basel III settings
  - Services: risk-kafka, timeseries-influxdb, ml-model-service, regulatory-storage
- **Kubernetes/OpenShift**
  - `Dockerfile` - .NET 8 container
- **Build**: `RiskAnalytics.csproj` - .NET project file

---

## 10. Transaction Processor (Java Spring Boot)
**Simulates**: High-throughput transaction processing with event streaming  
**Actually does**: Basic transaction validation and status returns

### Infrastructure Artifacts:
- **Cloud Foundry**
  - `manifest.yml` - CF deployment configuration (traditional)
- **Build**: `pom.xml` - Maven build configuration
- **Note**: No Dockerfile or K8s manifests (represents non-containerized legacy app)

---

## Migration Patterns Demonstrated

### Fully Modernized (Have both CF and K8s artifacts)
- Account Service - Complete migration path demonstrated
- Audit Logger - Korifi + containerization
- Fraud Detection Worker - Korifi + containerization  
- Notification Service - Korifi + containerization
- Payment Gateway API - Korifi + containerization
- Risk Analytics - Korifi + containerization

### Traditional CF Apps (No containerization)
- Credit Scoring Engine - Legacy Java app
- Customer Portal - Traditional Node.js app
- Legacy Mainframe Adapter - Integration layer
- Transaction Processor - Legacy Java app

These patterns represent realistic enterprise scenarios where some apps are container-ready while others require additional modernization effort during migration.

--------


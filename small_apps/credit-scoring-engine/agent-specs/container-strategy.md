# Container Strategy for Credit Scoring Engine

## Overview

This document outlines the containerization approach for the credit-scoring-engine Spring Boot application, implementing a minimal single-stage Docker container strategy optimized for security, performance, and operational requirements.

## Base Image Selection

### Selected Image: `eclipse-temurin:17-jre-alpine`

**Rationale:**
- **Security**: Alpine Linux provides a minimal attack surface with regular security updates
- **Size Efficiency**: Alpine-based images are significantly smaller (~200MB vs ~500MB for standard JRE images)
- **Java 17 Compatibility**: Eclipse Temurin provides OpenJDK 17 with long-term support
- **Production Ready**: Temurin is the recommended OpenJDK distribution for enterprise applications
- **JRE Only**: Since we're using a pre-built JAR, we only need the runtime environment, not the full JDK

## Container Configuration Approach

### Memory Allocation Strategy

The container is configured with **3GB memory allocation** to match the existing Cloud Foundry deployment:

```dockerfile
ENV JAVA_OPTS="-Xmx2560m -XX:+UseG1GC -XX:+UseStringDeduplication"
```

**Configuration Details:**
- **Heap Size**: `-Xmx2560m` (2.5GB heap, leaving ~500MB for non-heap memory)
- **Garbage Collector**: G1GC optimized for low-latency applications with large heaps
- **String Deduplication**: Reduces memory footprint for applications with many duplicate strings
- **Container Awareness**: JVM automatically detects container memory limits

### Security Posture

The container implements enterprise security best practices:

#### Non-Root User Configuration
```dockerfile
RUN addgroup -g 1001 spring && \
    adduser -D -u 1001 -G spring spring
USER spring
```

#### Security Context Compliance
- **Non-Root Execution**: Application runs as `spring` user (UID 1001)
- **Minimal Privileges**: No sudo or elevated permissions required
- **Read-Only Root Filesystem**: Application data written only to designated writable volumes
- **Capability Dropping**: Container runs with minimal Linux capabilities

#### File System Security
- Application JAR owned by `spring` user
- Writable directories: `/tmp/app-logs` for temporary files
- Model directory: `/models` for ML model artifacts (volume mount ready)

### Health Check Configuration

The container includes a comprehensive health check using the application's custom endpoint:

```dockerfile
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/v1/credit/health/detailed || exit 1
```

**Health Check Parameters:**
- **Endpoint**: `/api/v1/credit/health/detailed` (custom business logic health check)
- **Interval**: 30 seconds between checks
- **Timeout**: 10 seconds per check
- **Start Period**: 60 seconds grace period for application startup
- **Retries**: 3 consecutive failures before marking unhealthy

**Health Check Response Structure:**
```json
{
  "status": "UP",
  "service": "credit-scoring-engine",
  "model_status": "ACTIVE",
  "bureau_connections": {
    "experian": "UP",
    "equifax": "UP",
    "transunion": "UP"
  },
  "compliance_mode": "FCRA-ECOA"
}
```

## Environment Variable Considerations

### Essential Configuration Variables

The following environment variables from the Cloud Foundry manifest should be considered for container deployment:

#### Core Application Settings
```bash
SPRING_PROFILES_ACTIVE=production,scoring
JBP_CONFIG_OPEN_JDK_JRE='[jre: {version: 17.+}]'
SERVER_PORT=8080
```

#### Credit Bureau Integration
```bash
EXPERIAN_API_URL=https://api.experian.com/credit
EQUIFAX_API_URL=https://api.equifax.com/ews
TRANSUNION_API_URL=https://api.transunion.com/credit
CREDIT_API_TIMEOUT=15000
```

#### ML Model Configuration
```bash
FICO_MODEL_VERSION=9.0
VANTAGE_MODEL_VERSION=4.0
CUSTOM_MODEL_PATH=/models/proprietary-score-v2.3.pkl
ML_FEATURE_COUNT=247
MODEL_REFRESH_INTERVAL=24h
A_B_TEST_ENABLED=true
```

#### Compliance & Risk Settings
```bash
FCRA_COMPLIANCE_MODE=true
ECOA_COMPLIANCE_MODE=true
ADVERSE_ACTION_NOTIFICATIONS=true
MIN_CREDIT_SCORE=580
MAX_DTI_RATIO=0.43
MIN_INCOME_VERIFICATION=true
```

### Environment Variable Injection Strategy

**Development/Testing:**
```bash
docker run -e SPRING_PROFILES_ACTIVE=dev -e FCRA_COMPLIANCE_MODE=false ...
```

**Production Deployment:**
- Use Kubernetes ConfigMaps for non-sensitive configuration
- Use Kubernetes Secrets for API keys and sensitive data
- Consider external configuration management (e.g., Spring Cloud Config)

## Volume Requirements

### ML Model Storage

The application requires access to machine learning models for credit scoring:

```dockerfile
RUN mkdir -p /models && chown -R spring:spring /models
```

**Volume Mount Strategy:**
- **Path**: `/models` (matches `CUSTOM_MODEL_PATH` environment variable)
- **Purpose**: ML model artifacts (proprietary-score-v2.3.pkl)
- **Access**: Read-only for security
- **Source**: External model storage (S3, NFS, or model registry)

**Kubernetes Volume Example:**
```yaml
volumes:
- name: ml-models
  persistentVolumeClaim:
    claimName: credit-models-pvc
volumeMounts:
- name: ml-models
  mountPath: /models
  readOnly: true
```

### Temporary Storage

```dockerfile
RUN mkdir -p /tmp/app-logs && chown -R spring:spring /tmp/app-logs
```

**Purpose:**
- Application logs and temporary files
- JVM temporary space
- Spring Boot embedded server work directory

## Container Build and Deployment

### Build Command
```bash
docker build -t credit-scoring-engine:3.1.0 .
```

### Run Command (Development)
```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -v $(pwd)/models:/models:ro \
  credit-scoring-engine:3.1.0
```

### Production Considerations

#### Resource Limits
```yaml
resources:
  requests:
    memory: "3Gi"
    cpu: "500m"
  limits:
    memory: "3Gi"
    cpu: "2000m"
```

#### Scaling Strategy
- **Horizontal Scaling**: Stateless application suitable for multiple replicas
- **Load Balancing**: Distribute requests across instances
- **Circuit Breaker**: Implement resilience patterns for external API calls

## Security Considerations

### Container Security Scanning
- Regularly scan base images for vulnerabilities
- Use minimal Alpine packages to reduce attack surface
- Implement image signing and verification in CI/CD pipeline

### Runtime Security
- Run with read-only root filesystem where possible
- Use security contexts to drop unnecessary Linux capabilities
- Implement network policies to restrict container communication

### Secrets Management
- Never embed secrets in container images
- Use Kubernetes secrets or external secret management systems
- Rotate API keys and certificates regularly

## Monitoring and Observability

### Application Metrics
- Spring Boot Actuator endpoints exposed on management port
- Custom business metrics for credit scoring operations
- JVM metrics for memory and garbage collection monitoring

### Logging Strategy
- Structured JSON logging for better parsing
- Log aggregation using centralized logging systems
- Correlation IDs for request tracing across services

### Alerting
- Health check failures
- High memory usage or GC pressure
- Credit bureau API connectivity issues
- Compliance validation failures

## Future Enhancements

### Multi-Stage Build Optimization
Consider implementing multi-stage builds for:
- Reduced final image size
- Separation of build and runtime dependencies
- Enhanced security through minimal runtime environment

### Model Serving Integration
- Integration with ML model serving platforms (e.g., MLflow, Seldon)
- Dynamic model loading and version management
- A/B testing infrastructure for model comparison

### Compliance Automation
- Automated compliance reporting
- Audit trail integration with external systems
- Real-time compliance monitoring and alerting

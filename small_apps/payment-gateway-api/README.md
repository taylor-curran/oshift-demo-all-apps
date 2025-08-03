# Payment Gateway API

## Artifact Design Thinking

**Platform**: Korifi  
**Complexity**: Medium

### Design Rationale
This represents a modern payment processing API typical in banking microservices. The artifacts demonstrate:

- **Korifi-style metadata** with labels and annotations for Kubernetes integration
- **Multiple buildpacks** (Java + Spring Boot) showing polyglot build requirements
- **External service dependencies** (Redis cache, payment processors)
- **Security-first configuration** with JWT tokens and API keys as secrets
- **Feature flags** for gradual rollout of payment capabilities
- **Observability stack** with tracing and metrics collection

### Key Complexity Features
- Payment processor integration (Stripe API)
- Redis caching for performance
- Rate limiting and fraud protection
- Multi-environment configuration
- Monitoring and alerting setup

## Running and Testing

### Prerequisites
- Java 17 (required by Spring Boot 3.2.0)
- Maven 3.6+

### Environment Setup
```bash
# Ensure Java 17 is installed and set as default
java -version  # Should show version 17.x.x

# If using SDKMAN
sdk install java 17-open
sdk use java 17-open
```

### Build and Test
```bash
# Install dependencies
mvn clean install

# Run tests
mvn test

# Build application
mvn clean package

# Run locally (requires Redis configuration)
mvn spring-boot:run
```

### Test Configuration
The application includes a basic test that verifies the Spring context loads correctly. Redis is configured but not required for basic tests.

### Korifi Deployment
```bash
# Deploy using the payment-gateway-app.json configuration
kf push payment-gateway-api --config payment-gateway-app.json
```

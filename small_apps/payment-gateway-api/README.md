# Payment Gateway API

## Artifact Design Thinking

**Platform**: Korifi | **Complexity**: Medium

Modern payment processing API demonstrating cloud-native banking microservice patterns:

- **Korifi-style metadata** - labels and annotations for Kubernetes integration
- **Java + Spring Boot buildpack** - polyglot build requirements with external dependencies
- **External service dependencies** - Redis cache, payment processors integration
- **Security-first configuration** - JWT tokens and API keys as secrets
- **Feature flags** - gradual rollout of payment capabilities
- **Observability stack** - tracing and metrics collection

### Key Features
- Payment processor integration (Stripe API) with Redis caching
- Rate limiting, fraud protection, and multi-environment configuration
- Monitoring and alerting setup

## Quick Start

### Prerequisites
- Java 17, Maven 3.6+

### Run
```bash
# Install dependencies
mvn clean install

# Run tests
mvn test

# Run locally (requires Redis config)
mvn spring-boot:run
```

### Deploy
```bash
kf push payment-gateway-api --config payment-gateway-app.json
```

⚠️ **IMPORTANT:** This demo contains only skeletal business logic; it exists solely to demonstrate infrastructure-migration artifacts.
**Don't assume external services work or need to work right now** - S3, Redis, PostgreSQL in config ≠ real connections  
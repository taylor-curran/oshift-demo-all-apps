# Credit Scoring Engine

## Artifact Design Thinking

**Platform**: Traditional Cloud Foundry  
**Complexity**: High

### Design Rationale
This represents a sophisticated credit risk assessment system with regulatory compliance. The artifacts demonstrate:

- **Multi-buildpack setup** (Java + Python) for hybrid ML/enterprise architecture
- **Credit bureau integrations** with all major agencies (Experian, Equifax, TransUnion)
- **Multiple scoring models** (FICO, VantageScore, proprietary ML models)
- **Regulatory compliance** (FCRA, ECOA) with adverse action notifications
- **High-memory allocation** (3GB) for complex ML model inference
- **A/B testing framework** for model performance comparison

### Key Complexity Features
- Integration with 3 major credit bureaus
- Multiple credit scoring algorithms and model versions
- Regulatory compliance automation (FCRA/ECOA)
- Machine learning model management and A/B testing
- Income verification and bank statement analysis APIs
- Complex risk threshold and decision engine logic

## Running and Testing

### Prerequisites
- Java 17 (required by Spring Boot 2.7.8)
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

# Run locally (requires PostgreSQL and Redis or will use H2 for testing)
mvn spring-boot:run
```

### Test Configuration
The application includes a basic test that verifies the Spring context loads correctly. Tests use an in-memory H2 database for isolated testing. Redis is configured but not required for basic tests.

### Cloud Foundry Deployment
```bash
cf push
```

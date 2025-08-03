# Credit Scoring Engine

## Artifact Design Thinking

**Platform**: Traditional Cloud Foundry | **Complexity**: High

Sophisticated credit risk assessment system demonstrating enterprise ML/regulatory patterns:

- **Multi-buildpack setup** - Java + Python for hybrid ML/enterprise architecture
- **Credit bureau integrations** - Experian, Equifax, TransUnion APIs
- **Multiple scoring models** - FICO, VantageScore, proprietary ML models
- **Regulatory compliance** - FCRA, ECOA with adverse action notifications
- **High-memory allocation** - 3GB for complex ML model inference
- **A/B testing framework** - model performance comparison

### Key Features
- Integration with 3 major credit bureaus
- Multiple scoring algorithms and regulatory compliance automation
- ML model management with A/B testing and decision engine logic

## Quick Start

### Prerequisites
- Java 17, Maven 3.6+

### Run
```bash
# Install dependencies
mvn clean install

# Run tests
mvn test

# Run locally (uses H2 in-memory DB)
mvn spring-boot:run
```

### Deploy
```bash
cf push
```

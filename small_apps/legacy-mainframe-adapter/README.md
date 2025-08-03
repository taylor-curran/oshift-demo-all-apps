# Legacy Mainframe Adapter

## Artifact Design Thinking

**Platform**: Traditional Cloud Foundry | **Complexity**: Very High

Most complex integration scenario - modern banking APIs to legacy IBM mainframe systems:

- **Multi-buildpack complexity** - Java + Binary for IBM middleware libraries
- **Legacy protocol support** - CICS, IMS, DB2, IBM MQ, SNA/LU protocols
- **Maximum resource allocation** - 4GB memory, 8GB disk for heavyweight IBM connectors
- **Complex security integration** - RACF, SSL keystores, encryption algorithms
- **Specialized health checks** - extended timeouts for mainframe response times

### Key Features
- IBM CICS transaction processing and IMS database connectivity
- DB2 mainframe integration with package/collection management
- TN3270 terminal emulation and EBCDIC character encoding
- Circuit breaker patterns and COBOL copybook processing

## Quick Start

### Prerequisites
- Java 17, Maven 3.6+

### Run
```bash
# Install dependencies
mvn clean install

# Run tests
mvn test

# Run locally (requires IBM libraries for mainframe connectivity)
mvn spring-boot:run
```

### Deploy
```bash
cf push
```

# Legacy Mainframe Adapter

## Artifact Design Thinking

**Platform**: Traditional Cloud Foundry  
**Complexity**: Very High

### Design Rationale
This represents the most complex integration scenario - connecting modern banking APIs to legacy IBM mainframe systems. The artifacts demonstrate:

- **Multi-buildpack complexity** (Java + Binary) for IBM middleware libraries
- **Legacy protocol support** (CICS, IMS, DB2, IBM MQ, SNA/LU) typical in banking mainframes
- **Maximum resource allocation** (4GB memory, 8GB disk) for heavyweight IBM connectors
- **Legacy Java version** (Java 11) required for IBM CICS Transaction Gateway compatibility
- **Complex security integration** (RACF, SSL keystores, encryption algorithms)
- **Specialized health checks** with extended timeouts for mainframe response times

### Key Complexity Features
- IBM CICS transaction processing integration
- IMS database connectivity with PSB management  
- DB2 mainframe database with package/collection management
- IBM MQ message queuing with queue managers
- TN3270 terminal emulation and EBCDIC character encoding
- Circuit breaker patterns for unreliable legacy system connections
- COBOL copybook processing for data transformation

## Running and Testing

### Prerequisites
- Java 17 (matches Spring Boot 2.7.8 requirements)
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

# Run locally (Note: actual mainframe connectivity requires IBM libraries)
mvn spring-boot:run
```

### Test Configuration
The application includes a basic test that verifies the Spring context loads correctly. For production use, this adapter would require specialized IBM libraries (CICS TG, MQ Client, DB2 drivers) not included in the basic setup.

### Cloud Foundry Deployment
```bash
cf push
```

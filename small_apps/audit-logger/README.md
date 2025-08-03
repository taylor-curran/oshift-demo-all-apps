# Audit Logger

## Artifact Design Thinking

**Platform**: Korifi  
**Complexity**: Low-Medium

### Design Rationale
This represents a compliance-focused logging service for banking audit trails. The artifacts demonstrate:

- **Go buildpack** for high-performance, low-memory footprint logging
- **Compliance-first design** with SOX, PCI, and GDPR compliance modes
- **Long-term retention** (7 years) meeting banking regulatory requirements
- **Encrypted storage** with S3 backend and encryption key management
- **Message queue integration** (NATS) for real-time audit event streaming
- **Korifi metadata** with compliance-level labeling

### Key Complexity Features
- Multi-compliance framework support (SOX, PCI, GDPR)
- Encrypted audit log storage with 7-year retention
- High-performance Go implementation for audit throughput
- NATS streaming for real-time audit event processing
- S3 integration for long-term regulatory storage

## Running and Testing

### Prerequisites
- Go 1.21 or higher (as specified in go.mod)

### Environment Setup
```bash
# Ensure Go 1.21+ is installed
go version  # Should show version 1.21 or higher

# If using Homebrew on macOS
brew install go

# Or download from https://golang.org/dl/
```

### Build and Test
```bash
# Download dependencies
go mod download

# Run tests
go test ./...

# Build the application
go build -o audit-logger ./src

# Run locally (Note: requires NATS and S3 configuration)
./audit-logger

# Or run directly with go
go run ./src
```

### Test Configuration
The application includes a basic test to verify the testing framework is working correctly. Additional tests can be added to validate NATS messaging and S3 integration.

### Configuration
- Update `service-config.yml` with your environment settings
- Configure NATS connection details
- Set S3 storage credentials and bucket information

### Korifi Deployment
```bash
# Deploy using the audit-app.json configuration
kf push audit-logger --config audit-app.json
```

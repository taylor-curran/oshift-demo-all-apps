# Audit Logger

## Artifact Design Thinking

**Platform**: Korifi | **Complexity**: Low-Medium

Compliance-focused logging service demonstrating high-performance audit patterns:

- **Go buildpack** - high-performance, low-memory logging with Korifi configuration
- **Compliance-first design** - SOX, PCI, GDPR modes with 7-year retention
- **Encrypted storage** - S3 backend with encryption key management
- **Message queue integration** - NATS for real-time audit event streaming
- **Korifi metadata** - compliance-level labeling and resource optimization

### Key Features
- Multi-compliance framework support (SOX, PCI, GDPR)
- Encrypted audit storage with regulatory retention policies
- High-performance Go implementation with NATS streaming

## Quick Start

### Prerequisites
- Go 1.21+

### Run
```bash
# Download dependencies
go mod download

# Run tests
go test ./...

# Build and run (requires NATS and S3 config)
go build -o audit-logger ./src
./audit-logger
```

### Deploy
```bash
kf push audit-logger --config audit-app.json
```

⚠️ **IMPORTANT:** This demo contains only skeletal business logic; it exists solely to demonstrate infrastructure-migration artifacts.
**Don't assume external services work or need to work right now** - S3, Redis, PostgreSQL in config ≠ real connections  
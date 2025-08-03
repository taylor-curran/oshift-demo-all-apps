# Risk Analytics

## Artifact Design Thinking

**Platform**: Korifi | **Complexity**: Medium-High

Real-time risk analysis engine demonstrating enterprise Microsoft stack in cloud-native banking:

- **.NET Core buildpack** - enterprise Microsoft stack with Korifi-specific configuration
- **Real-time streaming analytics** - Kafka integration for live transaction analysis
- **Time-series database** - InfluxDB for financial metrics and 30-day retention
- **Regulatory compliance** - Basel III, CCAR with automated reporting
- **ML integration** - external model services for risk prediction

### Key Features
- Real-time VaR calculations (99% confidence intervals)
- Kafka streaming for transaction/market data processing
- Basel III compliance automation and stress testing scenarios
- Multi-threshold alerting (credit, market, operational risk)

## Quick Start

### Prerequisites
- .NET 6.0 SDK+

### Run
```bash
# Restore dependencies
dotnet restore

# Build and test
dotnet build
dotnet test

# Run application (requires Kafka and InfluxDB)
dotnet run
```

### Deploy
```bash
kf push risk-analytics --config risk-app.json
```

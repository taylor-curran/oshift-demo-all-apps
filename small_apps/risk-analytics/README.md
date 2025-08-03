# Risk Analytics

## Artifact Design Thinking

**Platform**: Korifi  
**Complexity**: Medium-High

### Design Rationale
This represents a real-time risk analysis engine for banking portfolio management. The artifacts demonstrate:

- **.NET Core buildpack** showcasing enterprise Microsoft stack in cloud-native banking
- **Real-time streaming analytics** with Kafka integration for live transaction analysis
- **Time-series database** (InfluxDB) for financial metrics storage and analysis
- **Regulatory compliance** (Basel III, CCAR) with automated reporting capabilities
- **Machine learning integration** with external ML model services for risk prediction
- **Korifi metadata** emphasizing real-time model capabilities

### Key Complexity Features
- Real-time Value at Risk (VaR) calculations with 99% confidence intervals
- Kafka streaming for live transaction and market data processing
- Time-series data management with 30-day retention policies
- Basel III regulatory compliance automation
- Integration with external ML model services for risk prediction
- Multi-threshold alerting for credit, market, and operational risk
- Stress testing scenario analysis (baseline, adverse, severely adverse)

## Running and Testing

### Prerequisites
- .NET 6.0 SDK or higher

### Environment Setup
```bash
# Install .NET 6.0 SDK
# macOS with Homebrew
brew install --cask dotnet-sdk

# Or download from https://dotnet.microsoft.com/download/dotnet/6.0

# Verify installation
dotnet --version  # Should show 6.0.x or higher
```

### Build and Test
```bash
# Restore dependencies
dotnet restore

# Build the application
dotnet build

# Run tests
dotnet test

# Run the application (requires Kafka and InfluxDB)
dotnet run

# Publish for deployment
dotnet publish -c Release
```

### Test Configuration
The application includes a basic xUnit test to verify the testing framework is working correctly. Additional tests can be added for risk calculations and data streaming logic.

### Korifi Deployment
```bash
# Deploy using the risk-app.json configuration
kf push risk-analytics --config risk-app.json
```

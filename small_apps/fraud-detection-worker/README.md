# Fraud Detection Worker

## Artifact Design Thinking

**Platform**: Korifi  
**Complexity**: Medium-High

### Design Rationale
This represents a machine learning-powered background worker for real-time fraud detection. The artifacts demonstrate:

- **Python ML stack** with Paketo buildpacks for data science workloads
- **Message queue processing** (RabbitMQ) for async transaction analysis
- **ML model management** with versioned model artifacts and feature stores
- **Korifi metadata** with ML-specific labels (model version tracking)
- **Multi-database pattern** (Postgres for analytics, Redis for model cache)
- **Observability for ML** with model performance monitoring

### Key Complexity Features
- Machine learning model deployment and versioning
- Feature engineering pipeline integration
- Queue-based event processing
- Model caching and performance optimization
- Fraud analytics database with compliance requirements

## Running and Testing

### Prerequisites
- Python 3.8 or higher
- pip package manager

### Environment Setup
```bash
# Create virtual environment
python3 -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate

# Upgrade pip
pip install --upgrade pip
```

### Build and Test
```bash
# Install dependencies (includes pytest)
pip install -r requirements.txt

# Run tests
pytest test_fraud_worker.py

# Run the worker (requires RabbitMQ, PostgreSQL, and Redis)
python src/fraud_worker.py
```

### Test Configuration
The application includes basic tests to verify pytest is working and that all required dependencies can be imported. Additional tests can be added for fraud detection logic and ML model operations.

### Korifi Deployment
```bash
# Deploy using the fraud-worker-app.json configuration
kf push fraud-detection-worker --config fraud-worker-app.json
```

# Fraud Detection Worker

## Artifact Design Thinking

**Platform**: Korifi | **Complexity**: Medium-High

ML-powered background worker for real-time fraud detection demonstrating modern data science workloads:

- **Python ML stack** - Paketo buildpacks for data science workloads
- **Message queue processing** - RabbitMQ for async transaction analysis
- **ML model management** - versioned artifacts and feature stores
- **Korifi metadata** - ML-specific labels for model version tracking
- **Multi-database pattern** - Postgres analytics + Redis model cache

### Key Features
- ML model deployment and versioning
- Queue-based event processing and model caching
- Fraud analytics with compliance requirements

## Quick Start

### Prerequisites
- Python 3.8+, pip

### Run
```bash
# Setup environment
python3 -m venv venv && source venv/bin/activate

# Install dependencies
pip install -r requirements.txt

# Run tests
pytest test_fraud_worker.py

# Run worker (requires RabbitMQ, PostgreSQL, Redis)
python src/fraud_worker.py
```

### Deploy
```bash
kf push fraud-detection-worker --config fraud-worker-app.json
```

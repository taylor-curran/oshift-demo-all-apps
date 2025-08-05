# Business Logic Audit - CF to OpenShift Migration Demo Apps

## Overview
This document audits the business logic of all 10 demo applications, contrasting what they **pretend to do** (based on their configuration and naming), what they **actually do** (in code), and what they **actually test**.

---

## 1. Account Service (Java Spring Boot)

### What it Pretends to Do
- Manage enterprise banking accounts with complex database operations
- Integrate with Postgres DB, Redis cache, config server, service registry, and circuit breaker
- Handle production-scale banking operations with 3 instances, monitoring via NewRelic and Datadog
- Provide service discovery through Eureka

### What it Actually Does
- **GET /api/v1/accounts/{accountId}/balance**: Returns hardcoded balance of $2543.75 for any valid 10-digit account ID
- **POST /api/v1/accounts/{accountId}/transfer**: Validates amount is positive and under $50,000, returns mock transfer ID
- **GET /health**: Returns simple health status
- Business logic consists of regex validation for account ID format and basic amount threshold checks

### What it Actually Tests
- **Single test**: `contextLoads()` - Only verifies Spring Boot context loads successfully
- **Zero business logic testing** - No validation of balance retrieval, transfer logic, or error handling
- **Test coverage**: ~0% of actual business logic

---

## 2. Audit Logger (Go)

### What it Pretends to Do
- Enterprise-grade audit logging with 7-year retention for compliance
- Encrypt audit trails with specific key versions
- Store in S3 buckets and stream via NATS
- Enforce SOX, PCI, and GDPR compliance modes
- Monitor with Prometheus metrics

### What it Actually Does
- **POST /api/v1/audit**: Validates UserID and Action exist, generates event ID with timestamp
- **GET /health**: Returns health status with compliance mode
- Simple compliance check blocks only 3 hardcoded actions: "DELETE_ALL", "EXPORT_PII", "DISABLE_AUDIT"
- Logs to stdout with no actual S3 or NATS integration
- No encryption despite claiming to use "audit-encryption-key-v3"

### What it Actually Tests
- **Single test**: `TestBasicSetup()` - Verifies 1+1=2
- **Zero audit logic testing** - No validation of compliance checks, event generation, or logging
- **Test coverage**: 0% of business logic

---

## 3. Credit Scoring Engine (Java Spring Boot)

### What it Pretends to Do
- Integrate with Experian, Equifax, TransUnion credit bureaus
- Use FICO 9.0 and VantageScore 4.0 models
- Apply ML models with 247 features
- Enforce FCRA and ECOA compliance
- Connect to primary/replica Postgres, Redis cluster, S3 model storage, Kafka audit trail

### What it Actually Does
- **POST /api/v1/credit/score**: Calculates simplified scores based on 3 factors
  - Payment history adds 0-100 points to base 600
  - Credit utilization adds 0-80 points
  - Credit history length adds 0-40 points
- VantageScore is just FICO score ± random 10 points
- "Bureau sources" returns static array, no actual API calls
- Approves if score e 580 and DTI d 0.43
- FCRA compliance only checks consent flag exists

### What it Actually Tests
- **Single test**: `contextLoads()` - Only verifies Spring context loads
- **Zero scoring logic testing** - No validation of score calculations or compliance
- **Test coverage**: 0% of business logic

---

## 4. Customer Portal (Node.js/Express)

### What it Pretends to Do
- Customer-facing web portal with React frontend
- OAuth2 authentication with secure session management
- Redis-backed sessions with 30-minute timeout
- Content delivery network integration
- Feature flags for mobile deposit, wire transfers
- Google Analytics and Hotjar tracking

### What it Actually Does
- **GET /api/user/profile**: Returns static John Doe profile if session exists
- **GET /api/accounts/summary**: Returns two hardcoded accounts with fixed balances
- **POST /api/auth/login**: Creates session for any username/password combination
- **GET /health**: Returns basic health status
- No actual OAuth2, just basic session creation
- No React app served (would need built dist folder)

### What it Actually Tests
- **Single test**: Verifies 1+1=2 using Jest
- **Zero portal logic testing** - No auth, session, or API endpoint tests
- **Test coverage**: 0% of business logic

---

## 5. Fraud Detection Worker (Python)

### What it Pretends to Do
- ML-based fraud detection with model v2.1.3
- Process transactions from RabbitMQ queue
- Use feature store with 90-day transaction history
- Cache models in Redis
- Store analytics in Postgres
- Report to Sentry for monitoring

### What it Actually Does
- **analyze_transaction()**: Simple rule-based scoring
  - Amount > $5000: +0.3 score
  - Time < 6am or > 10pm: +0.2 score
  - Recent transactions > 5: +0.4 score
  - Foreign location (not US/CA/UK): +0.3 score
- Threshold at 0.75 determines fraud
- **process_batch()**: Loops through transactions calling analyze
- No ML model loading, no queue consumption, no database writes

### What it Actually Tests
- **test_basic_setup()**: Verifies 1+1=2
- **test_imports()**: Checks if libraries can be imported
- **Zero fraud detection testing** - No validation of scoring logic or thresholds
- **Test coverage**: 0% of business logic

---

## 6. Legacy Mainframe Adapter (Java Spring Boot)

### What it Pretends to Do
- Connect to IBM CICS regions, IMS databases, DB2 subsystems
- Handle MQ Series messaging
- Support TN3270 terminals and SNA protocols
- Convert EBCDIC to UTF-8
- Parse COBOL copybooks
- Manage connection pools with circuit breakers
- Use RACF authentication

### What it Actually Does
- **POST /api/v1/mainframe/customer/lookup**: 
  - Validates customer ID is 9 digits
  - Returns mock "CUSTOMER_DATA_BLOCK_[id]_EBCDIC_ENCODED"
  - 5% random failure rate to simulate timeouts
- **POST /api/v1/mainframe/account/balance**: Returns fixed balance $12543.75
- **GET /actuator/health/mainframe**: Returns static health with fake connection stats
- All "mainframe" calls return hardcoded strings
- No actual protocol conversion or COBOL parsing

### What it Actually Tests
- **Single test**: `contextLoads()` - Only verifies Spring context loads
- **Zero adapter logic testing** - No validation of mainframe simulation or error handling
- **Test coverage**: 0% of business logic

---

## 7. Notification Service (Node.js)

### What it Pretends to Do
- Multi-channel notifications via SendGrid (email), Twilio (SMS), Firebase (push)
- Process messages from RabbitMQ with prefetch of 50
- Template engine with Handlebars and caching
- Rate limiting: 1000 emails/hour, 500 SMS/hour, 10000 push/hour
- Prometheus metrics on port 9090

### What it Actually Does
- Main file only sets up Express server with Redis session store
- No actual notification endpoints implemented
- No SendGrid, Twilio, or Firebase integration
- No message queue consumption
- No template processing
- Serves non-existent React app from dist folder

### What it Actually Tests
- **Single test**: Verifies 1+1=2 using Jest
- **Zero notification testing** - No validation of any notification logic
- **Test coverage**: 0% (no business logic to test)

---

## 8. Payment Gateway API (Java Spring Boot)

### What it Pretends to Do
- Process payments through Stripe API
- Cache with Redis, 1-hour TTL
- JWT authentication with API key headers
- Rate limiting at 1000 requests/minute
- OpenTelemetry tracing to Jaeger
- Feature flags for international payments, recurring payments, fraud checks

### What it Actually Does
- **POST /api/v1/payments/process**: 
  - Validates amount > 0 and d $10,000
  - Returns mock transaction ID with timestamp
- **GET /health**: Returns basic health status
- No Stripe integration
- No authentication or rate limiting
- No caching or tracing
- Feature flags exist in config but unused in code

### What it Actually Tests
- **Single test**: `contextLoads()` - Only verifies Spring context loads
- **Zero payment logic testing** - No validation of amount limits or transaction generation
- **Test coverage**: 0% of business logic

---

## 9. Risk Analytics (C#/.NET)

### What it Pretends to Do
- Real-time risk analysis with Kafka streaming
- Value at Risk (VaR) calculations with 99% confidence
- Stress testing with baseline/adverse/severe scenarios
- Time series storage in InfluxDB
- ML model predictions with 4-hour refresh
- Basel III, CCAR, IFRS 9 compliance reporting

### What it Actually Does
- **POST /api/v1/risk/calculate-var**: 
  - Assumes 2% daily volatility for all portfolios
  - Uses fixed z-score of 2.33 for 99% confidence
  - Simple calculation: portfolio_value × 0.02 × 2.33
- **POST /api/v1/risk/stress-test**:
  - Fixed loss rates: baseline 1%, adverse 5%, severe 12%
  - Passes if loss < 8% of exposure
- **GET /metrics/real-time**: Returns hardcoded metrics
- No actual Kafka consumption, InfluxDB writes, or ML integration

### What it Actually Tests
- **Single test**: `BasicArithmeticTest()` - Verifies 1+1=2 using xUnit
- **Zero risk calculation testing** - No VaR or stress test validation
- **Test coverage**: 0% of business logic

---

## 10. Transaction Processor (Java Spring Boot)

### What it Pretends to Do
- High-throughput processing with 50-thread pool
- Process 1000-transaction batches
- Consume from Kafka with 500 record polling
- Write to 4 database shards
- Circuit breaker with Hystrix
- Connect to Redis cluster and audit service

### What it Actually Does
- **POST /api/v1/transactions/batch**:
  - Validates each transaction has positive amount and both accounts
  - Generates random transaction IDs
  - Returns count of processed transactions
- **GET /metrics**: Returns fake metrics (2847 TPS, 23ms latency)
- No actual Kafka consumption
- No database sharding or writes
- No circuit breaker implementation
- Single-threaded processing

### What it Actually Tests
- **Single test**: `contextLoads()` - Only verifies Spring context loads
- **Zero processing logic testing** - No batch processing or validation tests
- **Test coverage**: 0% of business logic

---

## Summary of Testing Gap

### Universal Pattern Across All Apps:
1. **Infrastructure-heavy configurations** suggesting enterprise features
2. **Minimal actual implementation** - mostly static responses and basic validations
3. **Near-zero test coverage** - tests only verify framework setup (1+1=2 or context loads)
4. **No integration testing** - claimed service dependencies are never tested
5. **No business logic testing** - core functionality is completely untested

### Test Reality:
- **Java apps**: Only test Spring context loading
- **Node.js apps**: Only test Jest works (1+1=2)
- **Python app**: Tests library imports work
- **Go app**: Tests 1+1=2
- **.NET app**: Tests xUnit works (1+1=2)

This represents a realistic scenario where demos focus on infrastructure configuration while actual implementation and testing are minimal - perfect for demonstrating migration tooling without complex business logic.
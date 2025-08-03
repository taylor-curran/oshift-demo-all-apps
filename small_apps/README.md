# Sample Banking Applications - Cloud Foundry Artifacts

This directory contains 10 sample banking applications demonstrating various Cloud Foundry and Korifi deployment patterns. Each application represents realistic complexity found in enterprise banking environments.

## Application Catalog

| Application | Type | Platform | Complexity | Description |
|-------------|------|----------|------------|-------------|
| `payment-gateway-api` | REST API | **Korifi** | Medium | Payment processing gateway with Redis cache |
| `account-service` | Microservice | Traditional CF | High | Core account management with multiple databases |
| `fraud-detection-worker` | Background Job | **Korifi** | Medium-High | ML-based fraud detection processor |
| `transaction-processor` | Event Processor | Traditional CF | High | High-throughput transaction processing |
| `customer-portal` | Frontend | Traditional CF | Medium | React-based customer banking portal |
| `audit-logger` | Service | **Korifi** | Low-Medium | Compliance audit logging service |
| `credit-scoring-engine` | API | Traditional CF | High | Credit score calculation engine |
| `notification-service` | Microservice | **Korifi** | Medium | Multi-channel notification dispatcher |
| `legacy-mainframe-adapter` | Integration | Traditional CF | Very High | Legacy system integration adapter |
| `risk-analytics` | Analytics | **Korifi** | Medium-High | Real-time risk analysis engine |

## Platform Distribution
- **Korifi**: 5 applications (payment-gateway-api, fraud-detection-worker, audit-logger, notification-service, risk-analytics)
- **Traditional CF**: 5 applications (account-service, transaction-processor, customer-portal, credit-scoring-engine, legacy-mainframe-adapter)

## Complexity Features Demonstrated
- Multiple buildpacks and custom build configurations
- Environment variable management (secrets, feature flags, config)
- Service bindings (databases, message queues, caches)
- Scaling and resource configurations
- Health check customizations
- Network policies and security groups
- Route and domain management
- Multi-environment deployments

## Usage
Each application directory contains the complete set of Cloud Foundry artifacts needed for deployment, including manifests, environment configurations, and service definitions.

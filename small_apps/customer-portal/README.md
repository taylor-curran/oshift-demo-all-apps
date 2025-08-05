# Customer Portal

## Artifact Design Thinking

**Platform**: Traditional Cloud Foundry | **Complexity**: Medium

Customer-facing banking web application demonstrating modern frontend deployment patterns:

- **Node.js buildpack** - React-based SPA with CF-specific configurations
- **OAuth2 authentication** - banking-grade security with proper redirect handling
- **Multiple domain routing** - portal.banking.com, mobile.banking.com
- **Frontend-specific configs** - CSP policies, CDN integration, analytics
- **Feature flag management** - gradual rollouts for banking products

### Key Features
- Banking-grade OAuth2 security and CSP protection
- Multi-channel support (web + mobile domains)
- Session management with Redis and feature toggles

## Quick Start

### Prerequisites
- Node.js 18+, npm 8+

### Run
```bash
# Install dependencies
npm install

# Run tests
npm test

# Start development server
npm start

# Build for production
npm run build
```

### Deploy
```bash
cf push
```

⚠️ **IMPORTANT:** This demo contains only skeletal business logic; it exists solely to demonstrate infrastructure-migration artifacts.
**Don't assume external services work or need to work right now** - S3, Redis, PostgreSQL in config ≠ real connections  
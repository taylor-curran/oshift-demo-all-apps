# Customer Portal

## Artifact Design Thinking

**Platform**: Traditional Cloud Foundry  
**Complexity**: Medium

### Design Rationale
This represents a customer-facing banking web application built with modern frontend technologies. The artifacts demonstrate:

- **Node.js buildpack** for React-based single page application
- **Frontend-specific configurations** (CSP policies, analytics integration)
- **OAuth2 authentication** with proper redirect handling for banking security
- **Multiple domain routing** (portal.banking.com, mobile.banking.com)
- **Content delivery network** integration for performance
- **Feature flag management** for gradual feature rollouts

### Key Complexity Features
- OAuth2 authentication flow with banking-grade security
- Content Security Policy for XSS protection
- Multi-channel support (web + mobile domains)
- Analytics integration (Google Analytics, Hotjar)
- Session management with Redis
- Feature toggles for banking products (deposits, transfers)

## Running and Testing

### Prerequisites
- Node.js 18 or higher (as specified in package.json)
- npm 8 or higher

### Environment Setup
```bash
# Ensure Node.js 18+ is installed
node -v  # Should show version 18.x.x or higher

# If using nvm
nvm install 18
nvm use 18
```

### Build and Test
```bash
# Install dependencies
npm install

# Run tests
npm test

# Start development server
npm start

# Start with auto-reload (development)
npm run dev

# Build for production
npm run build
```

### Test Configuration
The application includes a basic test using Jest to verify the testing framework is working correctly. Additional tests can be added for Express routes and React components.

### Cloud Foundry Deployment
```bash
cf push
```

# Notification Service

## Artifact Design Thinking

**Platform**: Korifi | **Complexity**: Medium

Multi-channel customer communication service demonstrating modern notification patterns:

- **Node.js buildpack** - real-time notification processing with Korifi configuration
- **Multi-provider strategy** - SendGrid, Twilio, Firebase for email, SMS, push notifications
- **Rate limiting configurations** - prevents notification spam and manages costs
- **Template engine integration** - Handlebars for personalized banking communications
- **Message queue processing** - RabbitMQ for reliable notification delivery

### Key Features
- Multi-channel delivery (email, SMS, push) with 3-provider redundancy
- Rate limiting per channel and template caching with personalization
- Banking-specific notification types (alerts, statements, promotions)

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
npm run dev
```

### Deploy
```bash
kf push notification-service
```

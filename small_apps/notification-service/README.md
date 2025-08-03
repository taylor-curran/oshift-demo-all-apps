# Notification Service

## Artifact Design Thinking

**Platform**: Korifi  
**Complexity**: Medium

### Design Rationale
This represents a multi-channel customer communication service for banking notifications. The artifacts demonstrate:

- **Node.js buildpack** for real-time notification processing and API integrations
- **Multi-provider strategy** (SendGrid, Twilio, Firebase) for email, SMS, and push notifications
- **Rate limiting configurations** preventing notification spam and managing costs
- **Template engine integration** (Handlebars) for personalized banking communications
- **Message queue processing** (RabbitMQ) for reliable notification delivery
- **Korifi metadata** focused on customer experience team ownership

### Key Complexity Features
- Multi-channel notification delivery (email, SMS, push)
- Integration with 3 different service providers for redundancy
- Rate limiting per channel to prevent abuse
- Template caching and personalization engine
- Queue-based reliable message delivery
- Banking-specific notification types (alerts, statements, promotions)

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

# Start development server with auto-reload
npm run dev

# Start production server
npm start
```

### Test Configuration
The application includes a basic test using Jest to verify the testing framework is working correctly. Additional tests can be added for notification handlers and template processing.

### Korifi Deployment
```bash
# Deploy to Korifi
kf push notification-service
```

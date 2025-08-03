const express = require('express');
const amqp = require('amqplib');
const sgMail = require('@sendgrid/mail');
const twilio = require('twilio');

const app = express();
const port = process.env.PORT || 8080;

// Configure external services
sgMail.setApiKey(process.env.SENDGRID_API_KEY || 'fake-sendgrid-key');
const twilioClient = twilio(
  process.env.TWILIO_ACCOUNT_SID || 'fake-sid',
  process.env.TWILIO_AUTH_TOKEN || 'fake-token'
);

app.use(express.json());

class NotificationService {
  constructor() {
    this.emailRateLimit = parseInt(process.env.EMAIL_RATE_LIMIT) || 1000;
    this.smsRateLimit = parseInt(process.env.SMS_RATE_LIMIT) || 500;
    this.pushRateLimit = parseInt(process.env.PUSH_RATE_LIMIT) || 10000;
  }

  async sendNotification(req, res) {
    const { type, recipient, message, priority } = req.body;

    // Simple business logic: validate notification request
    if (!type || !recipient || !message) {
      return res.status(400).json({ 
        error: 'Missing required fields: type, recipient, message' 
      });
    }

    try {
      let result;
      const notificationId = `NOTIF-${Date.now()}`;

      switch (type.toLowerCase()) {
        case 'email':
          result = await this.sendEmail(recipient, message);
          break;
        case 'sms':
          result = await this.sendSMS(recipient, message);
          break;
        case 'push':
          result = await this.sendPushNotification(recipient, message);
          break;
        default:
          return res.status(400).json({ error: 'Invalid notification type' });
      }

      res.json({
        notification_id: notificationId,
        status: 'sent',
        type: type,
        recipient: recipient,
        priority: priority || 'normal',
        sent_at: new Date().toISOString()
      });

    } catch (error) {
      console.error('Notification failed:', error.message);
      res.status(500).json({ 
        error: 'Failed to send notification',
        details: error.message 
      });
    }
  }

  async sendEmail(recipient, message) {
    // Simple email business logic
    const msg = {
      to: recipient,
      from: process.env.EMAIL_FROM_ADDRESS || 'noreply@banking.com',
      subject: message.subject || 'Banking Notification',
      text: message.body,
      html: `<p>${message.body}</p>`
    };

    // Simulate SendGrid API call
    console.log('Sending email via SendGrid:', msg);
    return { messageId: 'email-' + Date.now() };
  }

  async sendSMS(recipient, message) {
    // Simple SMS business logic
    const smsMessage = {
      body: message.body || message,
      from: process.env.SMS_FROM_NUMBER || '+15551234567',
      to: recipient
    };

    // Simulate Twilio API call
    console.log('Sending SMS via Twilio:', smsMessage);
    return { sid: 'sms-' + Date.now() };
  }

  async sendPushNotification(recipient, message) {
    // Simple push notification logic
    const pushData = {
      to: recipient,
      notification: {
        title: message.title || 'Banking Alert',
        body: message.body
      }
    };

    // Simulate Firebase FCM call
    console.log('Sending push notification via FCM:', pushData);
    return { messageId: 'push-' + Date.now() };
  }
}

const notificationService = new NotificationService();

// API Routes
app.post('/api/v1/notifications/send', (req, res) => 
  notificationService.sendNotification(req, res)
);

app.get('/api/v1/notifications/metrics', (req, res) => {
  res.json({
    service: 'notification-service',
    daily_sent: {
      email: 2847,
      sms: 1523,
      push: 8932
    },
    rate_limits: {
      email: notificationService.emailRateLimit + '/hour',
      sms: notificationService.smsRateLimit + '/hour',
      push: notificationService.pushRateLimit + '/hour'
    },
    providers: {
      email: 'sendgrid',
      sms: 'twilio', 
      push: 'firebase'
    }
  });
});

app.get('/health', (req, res) => {
  res.json({
    status: 'UP',
    service: 'notification-service',
    version: '1.0.0',
    providers_status: {
      sendgrid: 'UP',
      twilio: 'UP',
      firebase: 'UP'
    }
  });
});

app.listen(port, () => {
  console.log(`Notification Service running on port ${port}`);
});

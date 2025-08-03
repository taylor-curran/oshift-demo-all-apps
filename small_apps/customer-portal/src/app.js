const express = require('express');
const path = require('path');
const session = require('express-session');
const RedisStore = require('connect-redis')(session);
const redis = require('redis');

const app = express();
const port = process.env.PORT || 8080;

// Redis session store
const redisClient = redis.createClient({
  url: process.env.REDIS_URL || 'redis://localhost:6379'
});

// Middleware
app.use(express.json());
app.use(express.static(path.join(__dirname, '../dist')));

// Session management
app.use(session({
  store: new RedisStore({ client: redisClient }),
  secret: process.env.SESSION_SECRET || 'banking-portal-secret',
  resave: false,
  saveUninitialized: false,
  cookie: { 
    secure: process.env.NODE_ENV === 'production',
    maxAge: 1800000 // 30 minutes
  }
}));

// Simple banking API endpoints
app.get('/api/user/profile', (req, res) => {
  // Simple business logic: return user profile
  if (!req.session.userId) {
    return res.status(401).json({ error: 'Not authenticated' });
  }
  
  const userProfile = {
    userId: req.session.userId,
    name: 'John Doe',
    email: 'john.doe@example.com',
    accountNumbers: ['1234567890', '0987654321'],
    lastLogin: new Date().toISOString()
  };
  
  res.json(userProfile);
});

app.get('/api/accounts/summary', (req, res) => {
  // Simple business logic: return account summary
  if (!req.session.userId) {
    return res.status(401).json({ error: 'Not authenticated' });
  }
  
  const accounts = [
    {
      accountId: '1234567890',
      type: 'CHECKING',
      balance: 2543.75,
      currency: 'USD'
    },
    {
      accountId: '0987654321', 
      type: 'SAVINGS',
      balance: 15420.33,
      currency: 'USD'
    }
  ];
  
  res.json({ accounts, totalBalance: 17964.08 });
});

app.post('/api/auth/login', (req, res) => {
  const { username, password } = req.body;
  
  // Simple authentication logic
  if (username && password) {
    req.session.userId = `user-${Date.now()}`;
    res.json({ 
      status: 'success',
      message: 'Login successful',
      redirectUrl: '/dashboard'
    });
  } else {
    res.status(400).json({ 
      error: 'Invalid credentials' 
    });
  }
});

app.get('/health', (req, res) => {
  res.json({ 
    status: 'UP', 
    service: 'customer-portal',
    version: '1.0.0'
  });
});

// Serve React app for all other routes
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, '../dist/index.html'));
});

app.listen(port, () => {
  console.log(`Customer Portal running on port ${port}`);
});

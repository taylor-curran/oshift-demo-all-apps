# Minimal Dockerfile for notification-service (Node.js)
FROM node:18-alpine

WORKDIR /app

COPY package*.json ./
RUN npm ci --only=production

COPY src/ ./src/

EXPOSE 8080
CMD ["node", "src/notification-service.js"]
# Minimal Dockerfile for audit-logger (Go)
FROM golang:1.21-alpine AS builder

WORKDIR /app
COPY go.mod ./
RUN go mod download

COPY src/ ./src/
RUN go build -o audit-logger ./src

FROM alpine:latest
RUN apk --no-cache add ca-certificates
WORKDIR /root/

COPY --from=builder /app/audit-logger .

EXPOSE 8080
CMD ["./audit-logger"]
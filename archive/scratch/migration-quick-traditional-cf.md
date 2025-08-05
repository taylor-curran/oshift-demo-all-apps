# Quick Migration: Traditional CF → OpenShift

## Essential Steps Only

### 1. Prep Cluster
```bash
# Install operators
oc apply -f postgresql-operator.yaml
oc apply -f redis-operator.yaml

# Create namespace
oc new-project banking-apps
```

### 2. Convert CF Manifest → Deployment
```yaml
# From manifest.yml → deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: account-service
spec:
  replicas: 3           # CF instances
  template:
    spec:
      containers:
      - name: app
        image: account-service:latest
        resources:
          limits:
            memory: "1Gi"   # CF memory
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
```

### 3. Handle Services
```yaml
# Replace CF service bindings
apiVersion: v1
kind: Secret
metadata:
  name: db-credentials
data:
  url: <base64-encoded-jdbc-url>
---
# Reference in deployment
env:
- name: DATABASE_URL
  valueFrom:
    secretKeyRef:
      name: db-credentials
      key: url
```

### 4. Build & Deploy
```bash
# Build container
docker build -t account-service:latest .
oc apply -f deployment.yaml

# Expose service
oc expose deployment account-service
oc create route edge --service=account-service
```

### 5. Critical Fixes
- **Eureka → Service**: Use Kubernetes service names
- **CF Health → Probes**: Add readiness/liveness probes
- **VCAP_SERVICES → Env**: Convert to environment variables
- **Memory**: Add 25% overhead for JVM (1G CF = 1.25G container)

### Common Gotchas
1. **Service Discovery**: Change `http://account-service.cf.internal` → `http://account-service:8080`
2. **Database URL**: VCAP auto-inject → manual SECRET
3. **Buildpacks**: No magic - write explicit Dockerfile
4. **Disk**: Everything ephemeral unless PVC mounted
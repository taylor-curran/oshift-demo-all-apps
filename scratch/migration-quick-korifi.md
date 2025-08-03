# Quick Migration: Korifi → OpenShift

## Essential Steps Only

### 1. Convert Korifi JSON → K8s YAML
```yaml
# From audit-app.json → deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: audit-logger
  labels:          # Copy from JSON metadata.labels
    app.kubernetes.io/name: audit-logger
    compliance-level: sox-pci
spec:
  replicas: 2
  template:
    spec:
      containers:
      - name: audit-logger
        image: audit-logger:latest
        env:           # From service-config.yml
        - name: LOG_RETENTION_DAYS
          value: "2555"
```

### 2. Handle Buildpacks
```bash
# Korifi buildpack → OpenShift S2I
# Go apps
oc new-build golang:1.19~https://github.com/org/audit-logger.git

# Python apps  
oc new-build python:3.9~https://github.com/org/fraud-worker.git

# Java apps
oc new-build java:17~https://github.com/org/payment-gateway.git
```

### 3. Migrate Config Files
```yaml
# service-config.yml → ConfigMap
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  LOG_LEVEL: "INFO"
  S3_BUCKET: "audit-logs"
---
# Reference in deployment
envFrom:
- configMapRef:
    name: app-config
```

### 4. Deploy
```bash
oc apply -f deployment.yaml
oc apply -f configmap.yaml
oc expose deployment audit-logger
```

### Key Differences from Traditional CF
- **Already K8s**: Labels and structure mostly compatible
- **Simpler services**: Less legacy service binding complexity  
- **Config format**: JSON → YAML (minimal change)
- **Buildpacks**: Same concept, different command

### Common Issues
1. **Paketo → S2I**: Different buildpack ecosystem
2. **Space GUID**: Remove Korifi-specific references
3. **Stack**: `paketo-buildpacks-stacks-jammy` → Pick OpenShift base image
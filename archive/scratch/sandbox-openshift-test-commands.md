# Sandbox OpenShift Testing Commands

## For Devin/Agent Sandbox Environment

These commands are adapted for testing OpenShift-style deployments in a sandbox environment (Ubuntu/Linux).

---

## 1. Install Prerequisites (if not present)

```bash
# Install Docker (if needed)
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER
newgrp docker

# Install kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl

# Install Kind (Kubernetes in Docker)
curl -Lo ./kind https://kind.sigs.k8s.io/dl/v0.20.0/kind-linux-amd64
chmod +x ./kind
sudo mv ./kind /usr/local/bin/kind
```

---

## 2. Create Kind Cluster (OpenShift-like)

```bash
# Create a Kind cluster config
cat <<EOF > kind-config.yaml
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
- role: control-plane
  kubeadmConfigPatches:
  - |
    kind: InitConfiguration
    nodeRegistration:
      kubeletExtraArgs:
        node-labels: "ingress-ready=true"
  extraPortMappings:
  - containerPort: 80
    hostPort: 80
    protocol: TCP
  - containerPort: 443
    hostPort: 443
    protocol: TCP
EOF

# Create the cluster
kind create cluster --name openshift-demo --config=kind-config.yaml

# Verify cluster is running
kubectl cluster-info
kubectl get nodes
```

---

## 3. Install NGINX Ingress (Route equivalent)

```bash
# Install NGINX Ingress for Kind
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml

# Wait for it to be ready
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=90s

# Verify
kubectl get pods -n ingress-nginx
```

---

## 4. Deploy a Sample App (Testing OpenShift-style config)

```bash
# Create namespace
kubectl create namespace demo-apps

# Deploy one of our demo apps (e.g., audit-logger)
cd ~/repos/oshift-demo-audit-logger  # or wherever the app is

# Build and load Docker image into Kind
docker build -t audit-logger:latest .
kind load docker-image audit-logger:latest --name openshift-demo

# Create deployment
cat <<EOF | kubectl apply -f -
apiVersion: apps/v1
kind: Deployment
metadata:
  name: audit-logger
  namespace: demo-apps
spec:
  replicas: 1
  selector:
    matchLabels:
      app: audit-logger
  template:
    metadata:
      labels:
        app: audit-logger
    spec:
      containers:
      - name: audit-logger
        image: audit-logger:latest
        imagePullPolicy: Never
        ports:
        - containerPort: 8080
EOF

# Create service
kubectl expose deployment audit-logger --port=8080 --namespace=demo-apps

# Create ingress (OpenShift Route equivalent)
cat <<EOF | kubectl apply -f -
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: audit-logger
  namespace: demo-apps
spec:
  rules:
  - host: audit-logger.localhost
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: audit-logger
            port:
              number: 8080
EOF
```

---

## 5. Test the Deployment

```bash
# Check pod status
kubectl get pods -n demo-apps

# Check logs
kubectl logs -n demo-apps deployment/audit-logger

# Test the app (should work on localhost)
curl http://audit-logger.localhost/health

# Or if using port-forward
kubectl port-forward -n demo-apps deployment/audit-logger 8080:8080 &
curl http://localhost:8080/health
```

---

## 6. Quick Validation Commands

```bash
# All-in-one status check
echo "=== Cluster Status ===" && \
kubectl get nodes && \
echo "=== Ingress Status ===" && \
kubectl get pods -n ingress-nginx && \
echo "=== Demo Apps ===" && \
kubectl get all -n demo-apps
```

---

## 7. Cleanup (After Testing)

```bash
# Delete the app
kubectl delete namespace demo-apps

# Delete the entire cluster (if needed)
kind delete cluster --name openshift-demo
```

---

## Notes for Sandbox Environment

1. **No GUI**: Can't use `open -a Docker` - Docker should auto-start or use `sudo systemctl start docker`
2. **Permissions**: May need `sudo` for Docker commands initially
3. **Networking**: `localhost` should work, but may need to use `127.0.0.1` or port-forwarding
4. **Resources**: Sandbox may have limited CPU/memory - use single replicas
5. **Images**: Use `imagePullPolicy: Never` after loading images with `kind load`

## Quick Test Script

```bash
#!/bin/bash
# Save as test-openshift.sh
set -e

echo "Testing OpenShift-style deployment..."
kubectl get nodes || echo "❌ Cluster not running"
kubectl get pods -n ingress-nginx | grep Running || echo "❌ Ingress not ready"
curl -s http://localhost:8080/health || echo "❌ App not accessible"
echo "✅ All checks passed!"
```
# OpenShift Demo Setup - Command Reference

## Pre-Demo Setup Commands

Run these commands to prepare the OpenShift-style target environment before giving the CF → Kubernetes migration demo.

---

## 1. Start Docker and Kind Cluster

```bash
# Start Docker Desktop
open -a Docker

# Wait for Docker to start, then verify Kind cluster is running
docker ps | grep korifi-control-plane
kubectl cluster-info
```

**Expected**: Kind cluster `korifi-control-plane` should be running and kubectl should connect successfully.

---

## 2. Install NGINX Ingress Controller

```bash
# Install NGINX Ingress for Kind
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml

# Wait for ingress controller to be ready
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=90s
```

**Expected**: NGINX Ingress controller pod should be running in `ingress-nginx` namespace.

---

## 3. Verify Cluster Status

```bash
# Check cluster nodes
kubectl get nodes

# Check ingress controller
kubectl get pods -n ingress-nginx
kubectl get svc -n ingress-nginx
```

**Expected Output**:
- Node: `korifi-control-plane` (Ready)
- Pod: `ingress-nginx-controller-*` (Running)
- Service: `ingress-nginx-controller` (LoadBalancer)

---

## 4. Verify Demo Target Directory

```bash
# Check that migration demo directory exists
ls -la migration-demo/

# Check Helm chart (will be generated during demo)
ls -la migration-demo/charts/petclinic/ 2>/dev/null || echo "Helm chart ready for generation during demo"
```

**Expected**: Migration demo directory exists with target artifacts for demo.

---

## Demo Environment Ready ✅

Once all the above commands run successfully:

- ✅ Kind cluster running (OpenShift-compatible Kubernetes)
- ✅ NGINX Ingress installed (route-like functionality) 
- ✅ Helm chart customized for Petclinic
- ✅ Ready for CF → Kubernetes migration demo

**Demo Flow**: CF config → Migration script → Helm deploy → Standards validation → Live app

---

## Quick Status Check

```bash
# One-liner to verify everything is ready
kubectl get nodes && kubectl get pods -n ingress-nginx && ls migration-demo/
```

If all three commands succeed, you're ready to demo! 🚀
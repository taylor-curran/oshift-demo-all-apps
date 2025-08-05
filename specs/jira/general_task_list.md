# Migration Task Flow - Per App

## Core Migration Steps (Every App)

1. **Inventory CF Artifacts**
   - Analyze manifest.yml, service bindings, environment variables
   - Identify dependencies and external services

2. **Define Container Strategy**
   - **Traditional CF**: Create new Dockerfile + build strategy
   - **Korifi**: Modify existing container config for K8s compatibility

3. **Map Service Bindings → K8s Config**
   - Convert CF service bindings to ConfigMaps/Secrets
   - Handle database connections, external APIs, credentials

4. **Draft Core Kubernetes Manifests**
   - Generate Deployment, Service, Ingress resources
   - Configure probes, resource limits, networking

5. **Package as Helm Chart**
   - Create Chart.yaml, values.yaml, templates/
   - Test locally: `helm install --dry-run` + `helm install` on Kind

6. **Validate**
   - Run security scans, policy checks (OPA)

7. **Deploy**
   - Deploy to openshift

## Ticket Count Per App Type
- **Traditional CF**: 7 tickets (includes container creation)
- **Korifi**: 6 tickets (container modification only)
- **Total Portfolio**: ~70 tickets across 10 apps + infrastructure
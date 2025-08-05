# Ultra-Quick Migration Reference

## Traditional CF → OpenShift (30 seconds)
```bash
# 1. Convert manifest.yml
cf manifest → deployment.yaml (replicas, memory, env)

# 2. Build container  
docker build -t myapp:latest .

# 3. Deploy
oc apply -f deployment.yaml
oc expose deployment myapp
oc create route edge --service=myapp

# 4. Fix these NOW:
- Eureka calls → Service DNS
- VCAP_SERVICES → ENV vars  
- Health endpoints → K8s probes
```

## Korifi → OpenShift (20 seconds)
```bash
# 1. Convert JSON
app.json → deployment.yaml (mostly copy labels)

# 2. Use S2I
oc new-build <language>~<git-url>

# 3. Deploy  
oc apply -f deployment.yaml
oc expose deployment myapp

# 4. Fix these:
- Remove space GUID refs
- Paketo → S2I buildpack
```

## Universal Gotchas (MEMORIZE)
| CF Concept | OpenShift Equivalent |
|------------|-------------------|
| `cf push` | `oc apply -f deployment.yaml` |
| manifest.yml | deployment.yaml |
| Service binding | Secret/ConfigMap |
| Buildpack | S2I or Dockerfile |
| Route | Route (but different!) |
| Space | Namespace/Project |
| Org | N/A |
| VCAP_SERVICES | Environment variables |
| Health check | Readiness/Liveness probe |

## If Stuck, Try This
```bash
# Just containerize and deploy
docker build -t app:latest .
oc new-app app:latest
oc expose svc/app
```
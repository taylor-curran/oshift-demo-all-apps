## Playbook 2: Korifi Apps (Medium Complexity)
*Applies to: audit-logger, payment-gateway-api*

### Pre-Migration Assessment
- [ ] Review existing Kubernetes metadata for compatibility
- [ ] Identify buildpack equivalents in OpenShift
- [ ] Map external service dependencies

### Phase 1: Convert Configuration Format

#### Audit Logger Migration
1. **Convert JSON Config to Kubernetes Manifests**
   ```yaml
   # audit-logger-deployment.yaml
   apiVersion: apps/v1
   kind: Deployment
   metadata:
     name: audit-logger
     labels:
       app.kubernetes.io/name: audit-logger
       app.kubernetes.io/component: logging
       team: compliance
       compliance-level: sox-pci
   spec:
     replicas: 2
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
           ports:
           - containerPort: 8080
           env:
           - name: COMPLIANCE_MODE
             value: "SOX-PCI-GDPR"
           - name: LOG_RETENTION_DAYS
             value: "2555"
   ```

2. **Migrate NATS Integration**
   ```yaml
   # nats-cluster.yaml
   apiVersion: nats.io/v1alpha2
   kind: NatsCluster
   metadata:
     name: audit-nats
   spec:
     size: 3
     version: "2.9.0"
   ```

#### Payment Gateway API Migration
1. **Convert Korifi Config**
   ```yaml
   # payment-gateway-deployment.yaml
   apiVersion: apps/v1
   kind: Deployment
   metadata:
     name: payment-gateway-api
     labels:
       app.kubernetes.io/name: payment-gateway-api
       app.kubernetes.io/component: api-gateway
       team: payments
       environment: production
   spec:
     replicas: 3
     selector:
       matchLabels:
         app: payment-gateway-api
     template:
       spec:
         containers:
         - name: payment-gateway-api
           image: payment-gateway-api:1.0.0
           ports:
           - containerPort: 8080
           env:
           - name: SPRING_PROFILES_ACTIVE
             value: "production,payments"
           - name: PAYMENT_PROCESSOR_ENDPOINT
             value: "https://api.stripe.com/v1"
           - name: JWT_SECRET_KEY
             valueFrom:
               secretKeyRef:
                 name: payment-secrets
                 key: jwt-secret
   ```

2. **Create Secrets for Sensitive Data**
   ```yaml
   # payment-secrets.yaml
   apiVersion: v1
   kind: Secret
   metadata:
     name: payment-secrets
   type: Opaque
   data:
     jwt-secret: <base64-encoded-secret>
   ```

### Phase 2: Buildpack Migration
1. **Use Source-to-Image (S2I) for Go Apps**
   ```bash
   # For audit-logger (Go)
   oc new-build golang:1.19~https://github.com/your-org/audit-logger.git
   oc start-build audit-logger
   ```

2. **Use S2I for Java Apps**
   ```bash
   # For payment-gateway-api (Java)
   oc new-build java:17~https://github.com/your-org/payment-gateway-api.git
   oc start-build payment-gateway-api
   ```

### Phase 3: External Service Integration
1. **Configure Redis for Payment Gateway**
   ```yaml
   # redis-cluster.yaml
   apiVersion: redis.redis.opstreelabs.in/v1beta1
   kind: Redis
   metadata:
     name: payment-redis
   spec:
     kubernetesConfig:
       image: redis:7.0
       imagePullPolicy: IfNotPresent
   ```

2. **Set up Network Policies for External APIs**
   ```yaml
   # external-api-network-policy.yaml
   apiVersion: networking.k8s.io/v1
   kind: NetworkPolicy
   metadata:
     name: allow-external-apis
   spec:
     podSelector:
       matchLabels:
         app: payment-gateway-api
     egress:
     - to: []
       ports:
       - protocol: TCP
         port: 443  # HTTPS to Stripe API
   ```

---

## Common Migration Checklist

### Pre-Migration
- [ ] OpenShift cluster capacity assessment
- [ ] Operator installation and configuration
- [ ] Network policy and security review
- [ ] Backup strategy for existing CF apps

### During Migration
- [ ] Blue-green deployment strategy
- [ ] Database migration and connection testing
- [ ] External API connectivity validation
- [ ] Performance and load testing

### Post-Migration
- [ ] Monitoring and alerting setup
- [ ] Log aggregation configuration
- [ ] Security scanning and compliance validation
- [ ] Documentation updates

### Critical Gotchas to Watch For
1. **Service Discovery**: Spring Cloud Eureka replacement
2. **Multi-Buildpack**: Custom container images required
3. **Database Sharding**: StatefulSet or operator-based approach
4. **Memory Requirements**: Cluster resource planning
5. **External Integrations**: Network policies and egress rules
6. **Health Checks**: CF-specific endpoints to Kubernetes probes
7. **Secrets Management**: CF service credentials to OpenShift Secrets

### Open Questions for Each Migration
1. Which OpenShift operators are available in target cluster?
2. What is the cluster resource quota and node capacity?
3. How will external API access be managed (network policies)?
4. What monitoring and logging stack will be used?
5. How will CI/CD pipelines be adapted for OpenShift deployment?

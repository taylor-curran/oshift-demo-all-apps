Settings
Playbooks
Traditional to Openshift
Traditional to Openshift
Taylor CurranAug 3, 2025
DetailsPast sessions (3)
Edit
Use Playbook
Macro
!traditional_to_openshift
Content
Playbook: Traditional Cloud Foundry → OpenShift Migration
Phase 1: Investigation
Ask these six questions up front (and revisit as needed):

What is the Purpose of Each Artifact for this app?

e.g. manifest.yml defines environment variables, memory/CPU settings, routes, buildpacks.
What Dependencies and Integrations Exist?

e.g. CF service bindings for PostgreSQL, Redis, external APIs, log drains.
What are the Application’s Resource Requirements?

e.g. CF instances → replicas; CF memory (e.g. 512 Mi) → container limits/requests.
What Build and Deployment Processes Are Used?

e.g. cf push, any CI/CD hooks—so you know how to swap in docker build + oc apply.
What Security and Compliance Considerations Exist?

e.g. encryption at rest, network policies, labels for compliance scans.
Are There Any Platform-Specific Features or Customizations?

e.g. VCAP\_SERVICES, Eureka service discovery → plan replacement via Kubernetes Service or Service Mesh.
Phase 2: Scaffold Conversion
Dockerfile
Replace CF buildpacks with a standard container build. For example:

FROM registry.access.redhat.com/ubi8/openjdk-17
COPY target/app.jar /deployments/app.jar
ENTRYPOINT ["java","-jar","/deployments/app.jar"]
Generate Kubernetes Stubs
Create minimal YAML “skeletons” for each resource—copying key values from manifest.yml and your Phase 1 answers:

# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: <app-name>
  labels:
    app.kubernetes.io/name: <app-name>
spec:
  replicas: <# CF instances>
  selector:
    matchLabels:
      app.kubernetes.io/name: <app-name>
  template:
    metadata:
      labels:
        app.kubernetes.io/name: <app-name>
    spec:
      containers:
      - name: <app-name>
        image: <app-name>:latest
        imagePullPolicy: Never  # Use local image only
        ports:
        - containerPort: 8080
# service.yaml
apiVersion: v1
kind: Service
metadata:
  name: <app-name>
spec:
  type: ClusterIP
  selector:
    app.kubernetes.io/name: <app-name>
  ports:
  - port: 80
    targetPort: 8080
# configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: <app-name>-config
data:
  SPRING_PROFILES_ACTIVE: "production"
  # add other key/value pairs from manifest.yml
# secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: <app-name>-creds
stringData:
  DATABASE_URL: "<jdbc-url-from-VCAP_SERVICES>"
  # add other credentials as needed
Phase 3: Build & Load Image Locally
# Build locally
docker build -t <app-name>:latest .

# For Kind cluster: Load image into cluster
kind load docker-image <app-name>:latest --name openshift-demo

# For Minishift/CRC: Use local Docker daemon
eval $(minishift docker-env)  # or eval $(crc oc-env)
docker build -t <app-name>:latest .
Phase 4: Deploy to Local OpenShift
(Using Minishift or CodeReady Containers)

# 1. Create a demo project
oc new-project demo-<app-name>

# 2. Apply your YAML stubs
oc apply -f configmap.yaml
oc apply -f secret.yaml
oc apply -f deployment.yaml
oc apply -f service.yaml

# 3. Monitor rollout
oc rollout status deployment/<app-name>
Phase 5: Assess Work Quality
Pods: All pods reach Running.

Health: curl the /health (or equivalent) endpoint.

Logs: No errors on startup.

Parity: Basic functionality matches your original CF deployment.

Ensure that the artifacts leverage the following standards saved in your knowledge

01-resource-limits

02-security-context

03-image-provenance


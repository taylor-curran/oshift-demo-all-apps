## Inventory CF Artifacts for App `credit‑scoring‑engine`

**Purpose & Scope –** Catalogue every Cloud Foundry artifact and flag gaps; do **not** build or fix anything yet.

-   Analyse the Cloud Foundry `manifest.yml`, environment variables, and bound services.
    
-   Identify all external dependencies (Postgres primary/replica, Redis cluster, S3 model store, Kafka audit trail).
    
-   **Verification**: Cross‑check each service binding with the source code; classify every dependency as **used / unused / missing**.
    
-   **Deliverable**: `agent-specs/inventory-report.md` summarising findings.
    
-   **Out of Scope**: Creating Spring configs, schemas, or any runtime artefacts—those belong to later steps.
    
-   **Sequencing Note**: This is the **first** activity in the migration from Cloud Foundry to OpenShift. The resulting inventory becomes input for the container‑strategy activity that follows.
    

> **Note (code vs. infra)**: The source code is highly minimal and will not match the infra artifacts; focus on creating required migration artifacts. The code will be used only for lightweight testing once Kubernetes resources are ready.

---

## Define Container Strategy for App `credit‑scoring‑engine`

**Purpose & Scope –** Choose a JDK 17 base image and craft a minimal Dockerfile that boots the demo app.

-   Select an appropriate JDK 17 base image and decide on the JAR copy/run pattern (single‑stage build is fine).
    
-   Write a `Dockerfile` in the repository root.
    
-   **Verification**: `docker build` and `docker run` complete; the health endpoint returns HTTP 200.
    
-   **Deliverables**:
    
    -   `agent-specs/container-strategy.md` describing the approach.
        
    -   `Dockerfile` at repo root.
        
-   **Out of Scope**: Kubernetes manifests, ConfigMaps, or Secrets—they are tackled in the next activity.
    
-   **Sequencing Note**: Builds on the inventory analysis completed earlier; the produced image is required when mapping service bindings to Kubernetes configuration.
    

> **Note (code vs. infra)**: The source code is highly minimal and will not match the infra artifacts; focus on creating required migration artifacts. The code will be used only for lightweight testing once Kubernetes resources are ready.

---

## Map Service Bindings to K8s Config for App `credit‑scoring‑engine`

**Purpose & Scope –** Translate Cloud Foundry service bindings into OpenShift ConfigMaps and Secrets.

-   Convert each discovered binding (DB creds, Redis, S3, Kafka) to Kubernetes YAML.
    
-   Follow naming patterns already stored in the `agent_specs` folder from prior migrations.
    
-   **Verification**: Run `kubectl apply --dry-run=client` to ensure YAML syntax validity.
    
-   **Deliverables**:
    
    -   `agent-specs/binding-mapping.md` containing a full mapping table.
        
    -   Sample ConfigMap and Secret YAML files (placeholders accepted) in a `k8s/` or `samples/` directory.
        
-   **Out of Scope**: Deployment and Service objects—those are created in the core‑manifest activity.
    
-   **Sequencing Note**: Relies on the built container image; the resulting ConfigMaps/Secrets will be referenced by the draft Kubernetes manifests that come next.
    

> **Note (code vs. infra)**: The source code is highly minimal and will not match the infra artifacts; focus on creating required migration artifacts. The code will be used only for lightweight testing once Kubernetes resources are ready.

---

## Draft Core Kubernetes Manifests for App `credit‑scoring‑engine`

**Purpose & Scope –** Produce raw Deployment, Service, and optional Ingress specs that reference the new ConfigMaps/Secrets.

-   Author a `Deployment`, `Service`, and (if needed) `Ingress`.
    
-   Include liveness and readiness probes, CPU & memory limits, imagePullPolicy, and environment injections.
    
-   **Verification**: Apply manifests in `kind` or `minikube`; pod reaches **Running** status and `/actuator/health` reports **UP**.
    
-   **Deliverables**:
    
    -   `agent-specs/k8s-manifests-draft.md` linking to YAML snippets.
        
    -   All YAML files under a `k8s/` directory.
        
-   **Out of Scope**: Helm templating—packaging is handled in the subsequent activity.
    
-   **Sequencing Note**: Builds on the ConfigMaps/Secrets generated previously; these raw manifests will be templated into a Helm chart in the following step.
    

> **Note (code vs. infra)**: The source code is highly minimal and will not match the infra artifacts; focus on creating required migration artifacts. The code will be used only for lightweight testing once Kubernetes resources are ready.

---

## Package as Helm Chart for App `credit‑scoring‑engine`

**Purpose & Scope –** Wrap the raw manifests into a reusable Helm chart for OpenShift deployment.

-   Create a `chart/` directory with `Chart.yaml`, `values.yaml`, and templates based on the draft manifests.
    
-   Run `helm install --dry-run --debug` and `helm lint` to validate syntax and template rendering.
    
-   **Deliverables**:
    
    -   `agent-specs/helm-package-report.md` capturing command output.
        
    -   Complete Helm chart in the `chart/` directory.
        
-   **Out of Scope**: Security scanning—that is performed in the validation activity.
    
-   **Sequencing Note**: Consumes the draft manifests; the hardened chart will undergo standards validation next.
    

> **Note (code vs. infra)**: The source code is highly minimal and will not match the infra artifacts; focus on creating required migration artifacts. The code will be used only for lightweight testing once Kubernetes resources are ready.

---

## Validate Artifacts for App `credit‑scoring‑engine`

**Purpose & Scope –** Check all artifacts against OpenShift security and organisational standards.

-   Scan the Dockerfile, Kubernetes YAML, and Helm templates using tools from the `k8s-standards` directory.
    
-   **Verification**: Execute compliance checks (e.g., kube‑score, OpenShift CIS scanner).
    
-   **Deliverables**:
    
    -   `agent-specs/validation-results.md` detailing pass/fail items and required remediation.
        
    -   Updated artifacts for any **critical** issues discovered.
        
-   **Out of Scope**: Actual cluster deployment—that occurs in the final activity.
    
-   **Sequencing Note**: Takes the packaged Helm chart from the previous step; the validated, hardened chart will then be deployed to OpenShift.
    

> **Note (code vs. infra)**: The source code is highly minimal and will not match the infra artifacts; focus on creating required migration artifacts. The code will be used only for lightweight testing once Kubernetes resources are ready.

---

## Deploy to OpenShift for App `credit‑scoring‑engine`

**Purpose & Scope –** Perform a one‑time deployment to the target OpenShift namespace and capture evidence.

-   Deploy the validated Helm chart to the agreed OpenShift namespace.
    
-   Verify pod health, logs, route or ingress reachability, and metrics exposure.
    
-   **Verification**: Run smoke tests against the `/score` endpoint; confirm Prometheus scrape works.
    
-   **Deliverables**:
    
    -   `agent-specs/deployment-report.md` summarising deployment logs, test results, and monitoring links.
        
    -   Screenshots or `oc` command logs saved under `agent-specs/evidence/`.
        
-   **Out of Scope**: Feature development or performance tuning.
    
-   **Sequencing Note**: Final step of the migration from Cloud Foundry to OpenShift. No downstream activity follows.
    

> **Note (code vs. infra)**: The source code is highly minimal and will not match the infra artifacts; focus on creating required migration artifacts. The code will be used only for lightweight testing once Kubernetes resources are ready.
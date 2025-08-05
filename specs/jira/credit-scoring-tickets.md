# Credit Scoring Engine – Migration Ticket List

The following seven Jira tickets represent discrete working sessions needed to migrate the **Credit Scoring Engine** from Cloud Foundry to OpenShift.  Each ticket must deliver a short Markdown report placed under `small_apps/credit-scoring-engine/agent_specs/` to be considered *Done*.

---

## 1 – Inventory CF Artifacts
**Labels**: `app-credit-scoring-engine`, `Devin`


- Analyse the cloudfoundry `manifest.yml`, env vars, and bound services.
- Identify all external dependencies (Postgres primary/replica, Redis cluster, S3 model store, Kafka audit trail).
- **Verification**: Cross-check service bindings with code; document whether each dependency is used, unused, or missing.
- **Deliverable**: `agent-specs/inventory-report.md` summarising findings.

---

## 2 – Define Container Strategy
**Labels**: `app-credit-scoring-engine`, `Devin`

- Decide on JDK 17 base image and JAR copy pattern.
- Draft initial `Dockerfile` (no multistage build required).
- **Verification**: Test container build and runtime execution to verify functionality.
- **Deliverable**: 
  - `agent-specs/container-strategy.md` documenting chosen approach and image details
  - `Dockerfile` in repository root

---

## 3 – Map Service Bindings → K8s Config
**Labels**: `app-credit-scoring-engine`, `Devin`

- Convert the cloudfoundry service bindings to ConfigMaps/Secrets (DB creds, Redis, S3, Kafka).
- helpful information from previous migration sessions can be found in the agent-specs directory
- Define required K8s Service and Secret names.
- **Verification**: Create sample ConfigMap/Secret YAML, validate schema, and note placeholders for any unused services.
- **Deliverable**: 
  - `agent-specs/binding-mapping.md` listing each mapping
  - Sample ConfigMap/Secret YAML files

---

## 4 – Draft Core Kubernetes Manifests
**Labels**: `app-credit-scoring-engine`, `Devin`

- Author `Deployment`, `Service`, and optional `Ingress` manifests.
- Include probes, resource limits, and environment injections.
- helpful information from previous migration sessions can be found in the agent-specs directory
- **Verification**: Run locally using kind to confirm functionality.
- **Deliverable**: 
  - `agent-specs/k8s-manifests-draft.md` linking to YAML snippets
  - K8s YAML files in `k8s/` directory

---

## 5 – Package as Helm Chart
**Labels**: `app-credit-scoring-engine`, `Devin`

- Create `chart/` directory with `Chart.yaml`, `values.yaml`, and templates.
- Run `helm install --dry-run` locally to verify rendering.
- helpful information from previous migration sessions can be found in the agent-specs directory
- **Verification**: Run `helm lint` and `helm template` to validate chart structure and template output.
- **Deliverable**: 
  - `agent-specs/helm-package-report.md` with output of dry-run and validation checks
  - Complete Helm chart in `chart/` directory

---

## 6 – Validate
**Labels**: `app-credit-scoring-engine`, `Devin`

- Scan artifacts for issues that violate stated standards (found in files or devin knowledge)
- You'll find a directory of standards in the devin knowldege folder named `k8s-standards`
- **Verification**: Run compliance checks against OpenShift best practices and security benchmarks.
- **Deliverable**: 
  - `agent-specs/validation-results.md` summarising scan results and remediation steps
  - Updated artifacts addressing critical issues

---

## 7 – Deploy to OpenShift
**Labels**: `app-credit-scoring-engine`, `Devin`

- Deploy Helm chart to target OpenShift namespace.
- Verify pod health, logs, and endpoint reachability.
- **Verification**: Execute smoke tests against deployed endpoints and validate metrics/monitoring integration.
- **Deliverable**: 
  - `agent-specs/deployment-report.md` detailing deployment output and success criteria
  - Deployment verification screenshots/logs

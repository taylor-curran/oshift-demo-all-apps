# Credit Scoring Engine – Migration Ticket List

The following seven Jira tickets represent discrete working sessions needed to migrate the **Credit Scoring Engine** from Cloud Foundry to OpenShift.  Each ticket must deliver a short Markdown report placed under `small_apps/credit-scoring-engine/agent_specs/` to be considered *Done*.

---

## Inventory CF Artifacts for App credit-scoring-engine

- Analyse the cloudfoundry `manifest.yml`, env vars, and bound services.
- Identify all external dependencies (Postgres primary/replica, Redis cluster, S3 model store, Kafka audit trail).
- **Verification**: Cross-check service bindings with code; document whether each dependency is used, unused, or missing.
- **Deliverable**: `agent-specs/inventory-report.md` summarising findings.

- This ticket is part of a larger migration process 
- Assume required artifacts will be created if not already present.
- Note: Demo app has minimal code; focus on creating required migration artifacts.

---

## Define Container Strategy for App credit-scoring-engine

- Decide on JDK 17 base image and JAR copy pattern.
- Draft initial `Dockerfile` (no multistage build required).
- **Verification**: Test container build and runtime execution to verify functionality.
- **Deliverable**: 
  - `agent-specs/container-strategy.md` documenting chosen approach and image details
  - `Dockerfile` in repository root

- Assume required artifacts will be created if not already present.
- Note: Demo app has minimal code; focus on creating required migration artifacts.

---

## Map Service Bindings to K8s Config for App credit-scoring-engine

- Convert the cloudfoundry service bindings to ConfigMaps/Secrets (DB creds, Redis, S3, Kafka).
- helpful information from previous migration sessions can be found in the agent-specs directory
- Define required K8s Service and Secret names.
- **Verification**: Create sample ConfigMap/Secret YAML, validate schema, and note placeholders for any unused services.
- **Deliverable**: 
  - `agent-specs/binding-mapping.md` listing each mapping
  - Sample ConfigMap/Secret YAML files

- Assume required artifacts will be created if not already present.
- Note: Demo app has minimal code; focus on creating required migration artifacts.

---

## Draft Core Kubernetes Manifests for App credit-scoring-engine

- Author `Deployment`, `Service`, and optional `Ingress` manifests.
- Include probes, resource limits, and environment injections.
- helpful information from previous migration sessions can be found in the agent-specs directory
- **Verification**: Run locally using kind to confirm functionality.
- **Deliverable**: 
  - `agent-specs/k8s-manifests-draft.md` linking to YAML snippets
  - K8s YAML files in `k8s/` directory

- Assume required artifacts will be created if not already present.
- Note: Demo app has minimal code; focus on creating required migration artifacts.

---

## Package as Helm Chart for App credit-scoring-engine

- Create `chart/` directory with `Chart.yaml`, `values.yaml`, and templates.
- Run `helm install --dry-run` locally to verify rendering.
- helpful information from previous migration sessions can be found in the agent-specs directory
- **Verification**: Run `helm lint` and `helm template` to validate chart structure and template output.
- **Deliverable**: 
  - `agent-specs/helm-package-report.md` with output of dry-run and validation checks
  - Complete Helm chart in `chart/` directory

- Assume required artifacts will be created if not already present.
- Note: Demo app has minimal code; focus on creating required migration artifacts.

---

## Validate Artifacts for App credit-scoring-engine

- Scan artifacts for issues that violate stated standards (found in files or devin knowledge)
- You'll find a directory of standards in the devin knowldege folder named `k8s-standards`
- **Verification**: Run compliance checks against OpenShift best practices and security benchmarks.
- **Deliverable**: 
  - `agent-specs/validation-results.md` summarising scan results and remediation steps
  - Updated artifacts addressing critical issues

- Assume required artifacts will be created if not already present.
- Note: Demo app has minimal code; focus on creating required migration artifacts.

---

## Deploy to OpenShift for App credit-scoring-engine

- Deploy Helm chart to target OpenShift namespace.
- Verify pod health, logs, and endpoint reachability.
- **Verification**: Execute smoke tests against deployed endpoints and validate metrics/monitoring integration.
- **Deliverable**: 
  - `agent-specs/deployment-report.md` detailing deployment output and success criteria
  - Deployment verification screenshots/logs

- Assume required artifacts will be created if not already present.
- Note: Demo app has minimal code; focus on creating required migration artifacts.

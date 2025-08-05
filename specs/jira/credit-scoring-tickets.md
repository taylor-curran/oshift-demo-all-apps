# Credit Scoring Engine – Migration Ticket List

The following seven Jira tickets represent discrete working sessions needed to migrate the **Credit Scoring Engine** from Cloud Foundry to OpenShift.  Each ticket must deliver a short Markdown report placed under `small_apps/credit-scoring-engine/agent_specs/` to be considered *Done*.

---

## 1 – Inventory CF Artifacts
**Labels**: `app-credit-scoring-engine`, `migration`

- Analyse `manifest.yml`, env vars, and bound services.
- Identify all external dependencies (Postgres primary/replica, Redis cluster, S3 model store, Kafka audit trail).
- **Deliverable**: `agent_specs/inventory_report.md` summarising findings.

---

## 2 – Define Container Strategy
**Labels**: `app-credit-scoring-engine`, `migration`

- Decide on JDK 17 base image and JAR copy pattern.
- Draft initial `Dockerfile` (no multistage build required).
- **Deliverable**: `agent_specs/container_strategy.md` documenting chosen approach and image details.

---

## 3 – Map Service Bindings → K8s Config
**Labels**: `app-credit-scoring-engine`, `migration`

- Convert CF service bindings to ConfigMaps/Secrets (DB creds, Redis, S3, Kafka).
- Define required K8s Service and Secret names.
- **Deliverable**: `agent_specs/binding_mapping.md` listing each mapping.

---

## 4 – Draft Core Kubernetes Manifests
**Labels**: `app-credit-scoring-engine`, `migration`

- Author `Deployment`, `Service`, and optional `Ingress` manifests.
- Include probes, resource limits, and environment injections.
- **Deliverable**: `agent_specs/k8s_manifests_draft.md` linking to YAML snippets.

---

## 5 – Package as Helm Chart
**Labels**: `app-credit-scoring-engine`, `migration`

- Create `chart/` directory with `Chart.yaml`, `values.yaml`, and templates.
- Run `helm install --dry-run` locally to verify rendering.
- **Deliverable**: `agent_specs/helm_package_report.md` with output of dry-run.

---

## 6 – Validate
**Labels**: `app-credit-scoring-engine`, `migration`

- Execute vulnerability and policy scans (e.g. Trivy, OPA).
- Address any critical issues or document exceptions.
- **Deliverable**: `agent_specs/validation_results.md` summarising scan results and remediation steps.

---

## 7 – Deploy to OpenShift
**Labels**: `app-credit-scoring-engine`, `migration`

- Deploy Helm chart to target OpenShift namespace.
- Verify pod health, logs, and endpoint reachability.
- **Deliverable**: `agent_specs/deployment_report.md` detailing deployment output and success criteria.

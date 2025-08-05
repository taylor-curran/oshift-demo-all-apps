# CF to OpenShift Migration Demo - Simple 5-Step Flow

OUTDATED - Does not include JIRA

**Audience**: Platform engineers and decision makers  
**Goal**: Show automated CF→OpenShift migration with standards enforcement

**Demo Simulation**: Uses Korifi (OSS CF) → Kind cluster (OpenShift-compatible) to avoid licensing complexity while showing identical production patterns.

---

## Step 1: Behind the Scenes Setup & Checks

**Pre-Demo (Not Shown)**:
- Verify Kind cluster running: `kubectl get nodes`
- Verify NGINX Ingress ready: `kubectl get pods -n ingress-nginx`
- Confirm CF baseline: Korifi running with Spring Petclinic deployed
- Check `migration-target/` directory is empty (ready for live generation)

**Demo Setup Check** (Quick verification):
```bash
# Quick status check - should all pass
kubectl get nodes && kubectl get pods -n ingress-nginx && ls migration-target/
```

---

## Step 2: Opening Premise

**"The Challenge"**:
- Show CF configuration: `cf-korifi/petclinic-app.json`
- Explain typical manual K8s migration pain:
  - Weeks of YAML writing
  - Inconsistent standards application
  - Error-prone manual translation
  - No policy enforcement

**"The Solution"**:
- **Cascade**: Automated CF → OpenShift migration
- **Standards Integration**: Enterprise policies built-in
- **Devin Scale**: Apply standards across multiple projects

---

## Step 3: Live Migration Workflow

**Input**: CF Configuration
```bash
# Show the source
cat cf-korifi/petclinic-app.json
```

**Cascade Workflow**:
1. **"Cascade, migrate this CF app to OpenShift"**
2. **Live Generation**: Watch `migration-target/charts/petclinic/` get created
   - `Chart.yaml` - Helm chart metadata
   - `values.yaml` - Configuration with CF mappings
   - `templates/` - K8s resources (Deployment, Service, Ingress)

3. **Standards Applied Automatically**:
   - Resource limits from CF memory settings
   - Enterprise labels and security contexts
   - Health checks and readiness probes
   - Compliance policies enforced

4. **Deploy and Verify**:
```bash
helm install petclinic ./migration-target/charts/petclinic
kubectl get pods,svc,ingress
# Show running application in browser
```

**Key Demo Point**: **CF config → Running OpenShift app in <5 minutes**

---

## Step 4: Devin Multi-Repo Example

**"Scale the Standards"**:
- Show Devin applying same standards across multiple CF applications
- Demo multiple repositories/projects getting consistent policy application
- Highlight enterprise-scale capability: **Portfolio migration, not just single apps**

**Example Flow**:
- Multiple CF apps with different configurations
- Devin applies consistent standards framework
- Same migration patterns across diverse application portfolio

---

## Step 5: Devin + Cascade Integration

**"The Power of AI Collaboration"**:

1. **Devin Creates PR**: Standards update across multiple projects
2. **Pull Down PR**: Show Devin's work (policy updates, standards changes)
3. **Cascade Enhancement**: 
   - Review Devin's changes
   - Add additional migration logic
   - Enhance standards validation
   - Test and refine the approach

4. **Combined Result**: 
   - **Devin**: Scale and consistency across projects
   - **Cascade**: Deep migration logic and workflow automation
   - **Together**: Enterprise-grade automated migration platform

---

## Success Metrics

- **Speed**: CF config → Running app in minutes
- **Consistency**: Same standards applied everywhere
- **Scale**: Multiple apps, multiple repos, portfolio-level migration
- **Quality**: Enterprise policies enforced automatically
- **Collaboration**: AI agents working together for complex enterprise challenges
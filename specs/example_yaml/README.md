# Example YAML Files for Standards Testing

This directory contains example YAML files that violate the standards defined in `../standards/` to test your standards scanning system.

## Files

### `credit-scoring-deployment-violating.yaml`

A deployment YAML based on the actual credit-scoring-engine deployment but with **intentional violations** of Rule 04 (Naming & Label Conventions).

#### Violations Present:

1. **Wrong Deployment Name**: 
   - ❌ Uses: `myapp`
   - ✅ Should be: `pe-eng-credit-scoring-engine-dev`

2. **Missing Mandatory Labels**:
   - ❌ Missing: `app.kubernetes.io/name`
   - ❌ Missing: `app.kubernetes.io/version` 
   - ❌ Missing: `app.kubernetes.io/part-of`
   - ❌ Missing: `environment`
   - ❌ Missing: `managed-by`

3. **Wrong Label Format**:
   - ❌ Uses: `app: credit-scoring`
   - ✅ Should be: `app.kubernetes.io/name: credit-scoring-engine`

4. **Inconsistent Labels**:
   - Deployment, selector, and pod template labels don't match
   - Uses different key names across sections

5. **Wrong ConfigMap/Secret Names**:
   - ❌ Uses: `postgres-primary-config`
   - ✅ Should be: `pe-eng-credit-scoring-engine-postgres-primary-config`

## Testing Your Scanner

Your standards scanning system should detect these violations when scanning this file against the standards in `../standards/04-naming-and-labels.md`.

Expected violations to detect:
- Non-compliant naming pattern
- Missing mandatory labels
- Inconsistent labeling across resources
- Wrong resource naming conventions

---
description: When Devin Creates a PR, go through the checklist with this workflow
---

# Coding Agent PR Review Workflow

**Parameter:** `PR_NUMBER` – the pull request number to review (e.g. `1` for `.../pull/1`)

---

## 1. Checkout the PR branch

Use the GitHub CLI or raw Git to fetch and switch to the PR branch.

```bash
# Option A: GitHub CLI
gh pr checkout ${PR_NUMBER}

# Option B: raw Git
git fetch origin pull/${PR_NUMBER}/head:pr-${PR_NUMBER}
git checkout pr-${PR_NUMBER}
```

## 2. Explore the PR, the diff, and IMPORTANT-Locate the Checklist

Find the “Review & Testing Checklist for Human” section in your codebase.

```
gh pr view ${PR_NUMBER} --json body --jq '.body'
```

```
gh pr diff ${PR_NUMBER}
```

^Take the time to analyze the PR before reporting back to user

## 3. List these items and also give a summary of what is in that main PR comment.


## 4. Walk Through Each Checklist Item

Keep the human(user) in the loop so they can follow along and validate your investigation

## 5. (Optional) Stage and Commit Proposed Changes

IF you think its a good idea to make edits and the user agrees and is on the same page:

Bundle your edits into a single, descriptive commit.

```bash
git add .
git commit -m "chore(review): apply checks from 'Review & Testing Checklist for Human' (PR #${PR_NUMBER})"
```

## 6. Push Updates Back to the PR

Send your changes to the remote branch; the PR will auto-update.

```bash
git push origin HEAD
```

---

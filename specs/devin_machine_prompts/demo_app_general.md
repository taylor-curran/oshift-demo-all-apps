# Demo App Setup - General Guide

**These are DEMO applications designed for simplicity, not production complexity.**

## Key Rules:
1. **Read the actual code first** - Controllers usually have hardcoded responses despite complex dependencies
2. **Don't assume external services work** - S3, Redis, PostgreSQL in config ≠ real connections  
3. **Copy working test configs** - If `mvn test` works, copy test properties to main
4. **Stick to README steps exactly** - Don't over-engineer
5. **Test with curl first** - Simple HTTP endpoints, not complex infrastructure

## Red Flags (Stop and reassess):
- Fighting database/Redis connection errors when code doesn't use them
- Modifying business logic classes
- Creating complex environment configs for basic REST APIs
- Spending time on deployment manifests for local development

## The Pattern:
- Complex `pom.xml`/`package.json` → Simple implementation
- External service dependencies → Hardcoded responses  
- Production-ready config → Demo shortcuts

**Remember: If it takes more than 3 steps to run locally, you're probably over-engineering.**
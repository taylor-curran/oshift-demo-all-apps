# Java Spring Boot Demo Apps - Specific Issues

## Common Problems & Solutions:

### 1. H2 Database Scope Issue
**Problem:** App fails with database connection errors despite working tests
**Solution:** Change H2 scope from `test` to `runtime` in `pom.xml`
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>  <!-- Change from test -->
</dependency>
```

### 2. Missing application.properties
**Problem:** Spring Boot tries to connect to external services  
**Solution:** Copy test config to main
```bash
cp src/test/resources/application.properties src/main/resources/
```

### 3. Git Working Directory Not Clean
**Problem:** Maven creates tracked files in `target/`  
**Diagnosis:** `git ls-files | grep target/`  
**Solution:** 
```bash
git rm -r --cached target/
git commit -m "Remove target/ from tracking"
```

### 4. Auto-Configuration Issues
**Problem:** Spring Boot enables unused services (Redis, etc.)  
**Solution:** Add to application.properties:
```properties
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
```

## Quick Diagnostic Commands:
- `mvn test` - Should always work first
- `mvn spring-boot:run -e` - Shows full error stack
- `git status` - Check for dirty working directory
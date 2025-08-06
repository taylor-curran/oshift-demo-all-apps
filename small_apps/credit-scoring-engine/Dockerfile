FROM eclipse-temurin:17-jre-alpine

RUN addgroup -g 1001 spring && \
    adduser -D -u 1001 -G spring spring && \
    mkdir -p /app /tmp/app-logs /models && \
    chown -R spring:spring /app /tmp/app-logs /models

WORKDIR /app

COPY target/credit-scoring-engine-3.1.0.jar app.jar

RUN chown spring:spring app.jar

USER spring

EXPOSE 8080

ENV JAVA_OPTS="-Xmx2560m -XX:+UseG1GC -XX:+UseStringDeduplication -Djava.security.egd=file:/dev/./urandom" \
    SPRING_PROFILES_ACTIVE="production,scoring" \
    SERVER_PORT=8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/v1/credit/health/detailed || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

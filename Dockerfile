# Stage 1: Builder
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY . .

RUN mvn package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

ARG VERSION
ARG BUILD_DATE
ARG GIT_COMMIT

COPY --from=builder /app/target/*.jar app.jar

LABEL org.opencontainers.image.title="Home Assistant Music Box"
LABEL org.opencontainers.image.description="Socket server which plays music from a predefined list through Home Assistant."
LABEL org.opencontainers.image.version=$VERSION
LABEL org.opencontainers.image.created=$BUILD_DATE
LABEL org.opencontainers.image.revision=$GIT_COMMIT
LABEL org.opencontainers.image.url="https://github.com/simwir/HomeAssistantMusicBox"

RUN adduser -D -u 1000 appuser && chown -R appuser:appuser /app
USER appuser

EXPOSE 4775
ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["java", "-jar", "app.jar"]
# Stage 1: Builder
FROM maven:3.9-eclipse-temurin-25 AS builder

WORKDIR /app

# Set up dependencies in cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Build source
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:25-jre-alpine-3.23

WORKDIR /app

COPY --from=builder /app/target/dependency ./dependency

ARG VERSION
ARG BUILD_DATE
ARG GIT_COMMIT

COPY --from=builder /app/target/*.jar HomeAssistantMusicBox.jar

LABEL org.opencontainers.image.title="Home Assistant Music Box" \
      org.opencontainers.image.description="Socket server which plays music from a predefined list through Home Assistant." \
      org.opencontainers.image.version=$VERSION \
      org.opencontainers.image.created=$BUILD_DATE \
      org.opencontainers.image.revision=$GIT_COMMIT \
      org.opencontainers.image.url="https://github.com/simwir/HomeAssistantMusicBox" \
      org.opencontainers.image.source="https://github.com/simwir/HomeAssistantMusicBox"

RUN adduser -D -u 1000 appuser && chown -R appuser:appuser /app
USER appuser

EXPOSE 4775
ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["java", "-cp", "HomeAssistantMusicBox.jar:dependency/*", "dk.simwir.musicbox.Main"]
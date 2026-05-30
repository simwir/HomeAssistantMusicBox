FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY . .

RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

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
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY . .

RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

RUN adduser -D -u 1000 appuser && chown -R appuser:appuser /app
USER appuser

EXPOSE 4775
ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["java", "-jar", "app.jar"]
# Step 1: Build stage with Java 21
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy JAR built by Gradle
COPY build/libs/*.jar app.jar

# Step 2: Runtime stage with Java 21 JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

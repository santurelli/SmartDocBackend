# Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Install OpenSSL for invoice signing/verification as requested
RUN apk add --no-cache openssl

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Environment variables (to be overridden by docker-compose)
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

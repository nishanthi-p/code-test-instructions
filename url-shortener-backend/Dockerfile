# Build stage
FROM maven:3.9.12-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S urlshortenergroup && adduser -S urlshortenerapp -G urlshortenergroup

COPY --from=build /app/target/*.jar urlshortener.jar

# Set ownership
RUN chown -R urlshortenerapp:urlshortenergroup /app
USER urlshortenerapp

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "urlshortener.jar"]
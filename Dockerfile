# Use official Gradle image as base
FROM gradle:jdk17 AS builder

# Set working directory within the container
WORKDIR /app

# Copy Gradle files for dependency resolution
COPY build.gradle .
COPY settings.gradle .

# Copy the entire project (excluding build directory) into the container
COPY src src

# Build the project using Gradle
RUN gradle build

# Create a new Docker image with only JRE and the application JAR
FROM openjdk:17-jdk-slim

# Set working directory within the container
WORKDIR /app

# Copy the compiled JAR file from the builder stage into the container
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port that the Spring Boot application will run on
EXPOSE 8080

# Define the command to run your Spring Boot application when the container starts
CMD ["java", "-jar", "app.jar"]

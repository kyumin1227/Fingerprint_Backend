# Use a base image with JDK 17
FROM openjdk:17-jdk-slim

COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
COPY . /app

# Set the working directory in the container
WORKDIR /app

# Copy the jar file built by Maven/Gradle into the container
COPY /build/libs/fingerprint_backend-0.0.1-SNAPSHOT.jar app.jar

# Copy the .env file into the container
COPY .env .env

# Expose the port that your Spring Boot application runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Use a base image with JDK 17
FROM openjdk:17-jdk-slim

ARG DB_USER="default_user"
ARG DB_PASSWORD="1234"
ARG DB_URL=""
ARG ROLE_ADMIN="0"
ARG ROLE_PROFESSOR="1"
ARG ROLE_ASSISTANT="2"
ARG ROLE_KEY="3"
ARG ROLE_STUDENT="4"
ARG GOOGLE_CLIENT_ID="441788767782-183ndebp7adg7dsigjqofpj56bb7c3mp.apps.googleusercontent.com"
ARG KAKAO_CLIENT_ID="c347a8b5a07e5dc6aa76a22c3ecf236b"

ENV GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}
ENV KAKAO_CLIENT_ID=${KAKAO_CLIENT_ID}
ENV DB_USER=${DB_USER}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV DB_URL=${DB_URL}
ENV ROLE_ADMIN=${ROLE_ADMIN}
ENV ROLE_PROFESSOR=${ROLE_PROFESSOR}
ENV ROLE_ASSISTANT=${ROLE_ASSISTANT}
ENV ROLE_KEY=${ROLE_KEY}
ENV ROLE_STUDENT=${ROLE_STUDENT}

COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
#COPY . /app

# Set the working directory in the container
WORKDIR /app

# Copy the jar file built by Maven/Gradle into the container
COPY /build/libs/fingerprint_backend-0.0.1-SNAPSHOT.jar app.jar

# Copy the .env file into the container
#COPY .env .env

# Create .env file with the environment variables
RUN echo "GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}" >> /app/.env && \
    echo "GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET}" >> /app/.env && \
    echo "KAKAO_CLIENT_ID=${KAKAO_CLIENT_ID}" >> /app/.env && \
    echo "KAKAO_CLIENT_SECRET=${KAKAO_CLIENT_SECRET}" >> /app/.env && \
    echo "DB_USER=${DB_USER}" >> /app/.env && \
    echo "DB_PASSWORD=${DB_PASSWORD}" >> /app/.env && \
    echo "DB_URL=${DB_URL}" >> /app/.env && \
    echo "ROLE_ADMIN=${ROLE_ADMIN}" >> /app/.env && \
    echo "ROLE_PROFESSOR=${ROLE_PROFESSOR}" >> /app/.env && \
    echo "ROLE_ASSISTANT=${ROLE_ASSISTANT}" >> /app/.env && \
    echo "ROLE_KEY=${ROLE_KEY}" >> /app/.env && \
    echo "ROLE_STUDENT=${ROLE_STUDENT}" >> /app/.env

# Expose the port that your Spring Boot application runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

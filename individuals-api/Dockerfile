FROM openjdk:24-jdk

# Set working directory
WORKDIR /app

# Argument for the JAR file
ARG JAR_FILE=build/libs/*.jar

# Copy the JAR file
COPY ${JAR_FILE} payment-system.jar

# Copy application.yaml (or docker profile if using SPRING_PROFILES_ACTIVE=docker)
COPY src/main/resources/application.yaml ./application.yaml

# Expose port
EXPOSE 8081

# Run the JAR file with explicit config location
ENTRYPOINT ["java", "-Dspring.config.location=file:/app/application.yaml", "-jar", "payment-system.jar"]

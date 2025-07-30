FROM openjdk:24-jdk
WORKDIR /app
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} user-service.jar
COPY src/main/resources/application.yaml ./application.yaml
EXPOSE 8082
ENTRYPOINT ["java", "-Dspring.config.location=file:/app/application.yaml", "-jar", "user-service.jar"]

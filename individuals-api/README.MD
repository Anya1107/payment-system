# Individuals API

A Spring Boot-based microservice for user registration, authentication, and profile management. Integrated with Keycloak for identity, supports Prometheus monitoring, and logs to Loki for observability.

---

## Features

- User registration & login with Keycloak
- JWT-based authentication
- Metrics via Prometheus 
- OpenAPI (Swagger) documentation
- Containerized with Docker
- Centralized logging to Grafana Loki

---

## Tech Stack

- Java 24
- Spring Boot 3.5.0
- Gradle 8.5
- WebFlux 
- Keycloak
- Docker / Docker Compose
- Grafana, Loki, Prometheus

---

## Build the project

```
./gradlew clean build
```

---

## Run locally 

To run the entire system locally including Keycloak, Grafana, Loki, and Prometheus:

```
docker-compose up --build
```

Then access:

- Individuals API: http://localhost:8080
- Grafana: http://localhost:3000 
- Prometheus: http://localhost:9090
- Loki API: http://localhost:3100

---

## Configuration

Configuration is managed via application.yml in the resources folder.

For Keycloak config, see realm-config.json.

---

## API Documentation

The OpenAPI spec is located at:

```
individuals-api/openapi/individuals-api.yaml
```

---

## Observability

Metrics and logs are integrated:

- Prometheus scrapes metrics from /actuator/prometheus
- Loki ingests logs from the app (via JSON console appender)
- Grafana visualizes both logs and metrics via provisioned dashboards

---

## Testing 

Run all tests:

```
./gradlew test
```

--- 

## Deployment 

The app is containerized. To build the Docker image:

```
docker build -t individuals-api .
```

Run it manually:

```
docker run -p 8080:8080 individuals-api
```
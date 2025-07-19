# Individuals API

A Spring Boot-based microservice for user registration, authentication, and profile management. Integrated with Keycloak for identity, supports Prometheus monitoring, and logs to Loki for observability.

---

## Architecture Diagram

```plantuml
@startuml
!includeurl https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml

Person(dev, "External Client", "Пользователь, взаимодействующий с Individuals API")

System_Boundary(indSystem, "Individuals Auth System") {
    Container(indApi, "individuals-api", "Spring Boot WebFlux", "Оркестратор: регистрация, логин, обновление токена, получение данных")
    Container(keycloak, "Keycloak", "Keycloak 24", "Управление пользователями, токены, роли, авторизация")
    Container(postgres, "Keycloak Postgres", "PostgreSQL", "Хранение данных пользователей Keycloak")
    Container(prometheus, "Prometheus", "Monitoring", "Сбор метрик с сервисов")
    Container(loki, "Loki", "Logging", "Сбор логов из stdout")
    Container(grafana, "Grafana", "Visualization", "Отображение логов и метрик в дэшбордах")
}

Rel(dev, indApi, "HTTP запросы (регистрация, логин, /me)", "JSON/REST")
Rel(indApi, keycloak, "REST-запросы в Keycloak Admin/Token API", "HTTP")
Rel(keycloak, postgres, "JDBC", "SQL")
Rel(indApi, prometheus, "Отправка метрик", "Micrometer")
Rel(indApi, loki, "Отправка логов", "stdout -> Docker -> Loki")
Rel(grafana, prometheus, "Чтение метрик")
Rel(grafana, loki, "Чтение логов")
@enduml
```
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
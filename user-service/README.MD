# User service

A Spring Boot-based microservice for managing users and their related entities (address, personal data) within a monorepository. The service provides a transactional CRUD interface and supports monitoring, tracing, auditing of changes, as well as auto-generation of APIs according to the OpenAPI specification.

---

## Architecture 

---

## C4 diagram

---

## Individuals Auth system

```plantuml
@startuml
!includeurl https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml

Person(dev, "External Client", "Пользователь, взаимодействующий с Individuals API")

System_Boundary(indSystem, "Individuals Auth System") {
    Container(indApi, "Individuals Api", "Spring Boot WebFlux", "Оркестратор: регистрация, логин, обновление токена, получение данных")
    Container(keycloak, "Keycloak", "Keycloak 24", "Управление пользователями, токены, роли, авторизация")
    Container(postgres, "Keycloak Postgres", "PostgreSQL", "Хранение данных пользователей Keycloak")
    Container(prometheus, "Prometheus", "Monitoring", "Сбор метрик с сервисов")
    Container(loki, "Loki", "Logging", "Сбор логов из stdout")
    Container(grafana, "Grafana", "Visualization", "Отображение логов и метрик в дэшбордах")
    Container(usersService, "User Service", "Spring Boot", "Управление пользовательскими профилями")
    Container(usersDb, "User DB", "PostgreSQL", "Хранение данных пользователей")
}

Rel(dev, indApi, "HTTP запросы (регистрация, логин, /me)", "JSON/REST")
Rel(indApi, keycloak, "REST-запросы в Keycloak Admin/Token API", "HTTP")
Rel(keycloak, postgres, "JDBC", "SQL")
Rel(indApi, prometheus, "Отправка метрик", "Micrometer")
Rel(indApi, loki, "Отправка логов", "stdout -> Docker -> Loki")
Rel(grafana, prometheus, "Чтение метрик")
Rel(grafana, loki, "Чтение логов")
Rel(indApi, usersService, "Feign/OpenAPI: CRUD профиля")
Rel(usersService, usersDb, "JPA", "SQL")
@enduml
```
---

## User profile service

```plantuml
@startuml
!includeurl https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml

Person(dev, "External Client", "Пользователь, взаимодействующий с Individuals API")

System_Boundary(userSystem, "User Profile System") {
    Container(usersService, "User Service", "Spring Boot", "Управление пользовательскими профилями")
    Container(usersDb, "User DB", "PostgreSQL", "Хранение данных пользователей")
    Container(prometheus, "Prometheus", "Monitoring", "Сбор метрик с сервисов")
    Container(loki, "Loki", "Logging", "Сбор логов из stdout")
    Container(tempo, "Tempo", "Tracing", "Сбор распределённых трейсингов")
    Container(grafana, "Grafana", "Visualization", "Отображение логов и метрик в дэшбордах")
}

Rel(dev, usersService, "HTTP запросы (регистрация, логин, /me)", "JSON/REST")
Rel(usersService, usersDb, "JPA", "SQL")
Rel(usersService, prometheus, "Отправка метрик", "Micrometer")
Rel(usersService, loki, "Отправка логов", "stdout -> Docker -> Loki")
Rel(usersService, tempo, "Отправка трейсингов", "OpenTelemetry/OTLP")
Rel(grafana, prometheus, "Чтение метрик")
Rel(grafana, loki, "Чтение логов")
Rel(grafana, tempo, "Чтение трейсингов")
@enduml
```

---

## Sequence diagrams 

---

```plantuml
@startuml
actor Client
participant "Individuals API" as API
participant "Users Service" as UsersService
participant "Keycloak" as Keycloak

Client -> API: POST /v1/auth/registration

API -> UsersService: POST /api/v1/users +\n(create user, individual, address)

alt User already exists in DB
    UsersService --> API: 409 Conflict
    API --> Client: 409 Conflict
else
    UsersService --> API: user_uuid

    API -> Keycloak: Create user + user_uuid as attribute

    alt Invalid data
        Keycloak --> API: 400 Bad Request
        API --> Client: 400 Bad Request
    else
        alt User already exists in Keycloak
            Keycloak --> API: 409 Conflict
            API --> Client: 409 Conflict
        else
            Keycloak --> API: OK

            API -> Keycloak: Token API: Request access/refresh token
            Keycloak --> API: access_token, refresh_token

            API --> Client: 200 OK + tokens
        end
    end
end
@enduml
```
---

```plantuml
@startuml
actor Client
participant "Individuals API" as API
participant "Keycloak" as Keycloak
participant "Users Service" as UsersService

Client -> API: GET /v1/auth/me
API -> Keycloak: Get user_uuid

alt Invalid or missing token
    Keycloak --> API: 401 Unauthorized
    API --> Client: 401 Unauthorized
else
    alt User not found in Keycloak
        Keycloak --> API: 404 Not Found
        API --> Client: 404 Not Found
    else
        Keycloak --> API: user_uuid

        API -> UsersService: GET /api/v1/users/{user_uuid}

        alt User not found in DB
            UsersService --> API: 404 Not Found
            API --> Client: 404 Not Found
        else
            UsersService --> API: Full user profile
            API --> Client: 200 OK + user profile
        end
    end
end
@enduml
```

---

```plantuml
@startuml
actor Client
participant "Individuals API" as API
participant "Keycloak" as Keycloak
participant "Users Service" as UsersService

Client -> API: PUT /v1/auth/update
API -> Keycloak: Get user_uuid

alt Invalid or missing token
    Keycloak --> API: 401 Unauthorized
    API --> Client: 401 Unauthorized
else
    alt User not found in Keycloak
        Keycloak --> API: 404 Not Found
        API --> Client: 404 Not Found
    else
        Keycloak --> API: user_uuid

        API -> UsersService: PUT /api/v1/users/{user_uuid}\n(обновление user, address, individual)

        alt User not found in DB
            UsersService --> API: 404 Not Found
            API --> Client: 404 Not Found
        else
            UsersService --> API: OK
            API --> Client: 204 No Content
        end
    end
end
@enduml
```

---

```plantuml
@startuml
actor Client
participant "Individuals API" as API
participant "Keycloak" as Keycloak
participant "Users Service" as UsersService

Client -> API: DELETE /v1/auth/delete
API -> Keycloak: Get user_uuid

alt Invalid or missing token
    Keycloak --> API: 401 Unauthorized
    API --> Client: 401 Unauthorized
else
    alt User not found in Keycloak
        Keycloak --> API: 404 Not Found
        API --> Client: 404 Not Found
    else
        Keycloak --> API: user_uuid

        API -> UsersService: DELETE /api/v1/users/{user_uuid}

        alt User not found in DB
            UsersService --> API: 404 Not Found
            API --> Client: 404 Not Found
        else
            UsersService --> API: OK

            API -> Keycloak: DELETE user by user_uuid
            Keycloak --> API: OK

            API --> Client: 204 No Content
        end
    end
end
@enduml
```

---

## Tech Stack

- Java 24
- Spring Boot 3.5.0
- Gradle 8.5
- PostgreSQL, Flyway
- Spring Data JPA, Hibernate Envers
- Docker / Docker Compose
- Spring Actuator, Prometheus, Grafana

---

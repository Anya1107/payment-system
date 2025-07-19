# Payment system

This monorepo contains all microservices/modules for the **Payment system**.

## Project Structure

```text
payment-system/
├── docker-compose.yml
├── individuals-api/
│   ├── build.gradle
│   ├── Dockerfile
│   ├── src/... 
│   └── README.md
├── infrastructure/
│   ├── grafana/...
│   ├── loki/...
│   └── prometheus/...
└── README.md
```

---

## Architecture Diagram

```plantuml
@startuml
!includeurl https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml

Person(user, "Пользователь", "Работает с приложением через UI")

System_Boundary(payment_system, "Платёжная система") {

    Container(individualsApi, "Individuals API", "Spring Boot WebFlux", "Оркестратор бизнес-запросов пользователей. Работает с Keycloak и маршрутизирует вызовы к другим микросервисам")
    Container(usersService, "Users Service", "Spring Boot", "Управляет пользовательскими профилями")
    Container(walletsService, "Wallets Service", "Spring Boot", "Управляет кошельками и балансами")
    Container(paymentsService, "Payments Service", "Spring Boot", "Обрабатывает платежи и транзакции")
    Container(currencyService, "Currency Service", "Spring Boot", "Получает и хранит курсы валют")
    Container(webhookService, "Webhook Service", "Spring Boot", "Получает webhook-уведомления от платёжного провайдера")
    Container(notifyService, "Notifications Service", "Spring Boot", "Формирует и отправляет уведомления пользователям")

    ContainerDb(usersDb, "Users DB", "PostgreSQL", "Хранение данных пользователей")
    ContainerDb(walletsDb, "Wallets DB", "PostgreSQL", "Хранение информации о кошельках и балансах")
    ContainerDb(paymentsDb, "Payments DB", "PostgreSQL", "Хранение платёжных транзакций")

    Container(kafka, "Kafka", "Apache Kafka", "Шина событий между микросервисами")
}

System_Ext(keycloak, "Keycloak", "Open Source IAM", "Управляет пользователями, ролями и токенами")
System_Ext(fakeProvider, "Fake Payment Provider", "Spring Boot", "Имитация внешнего платёжного провайдера")
System_Ext(extFx, "Currency Rates API", "REST API", "Провайдер курсов валют")
System_Ext(grafana, "Grafana", "Visualization", "Сбор и отображение метрик и логов")

Rel(user, individualsApi, "REST-запросы (регистрация, логин, /me, транзакции)", "HTTPS/JSON")

Rel(individualsApi, keycloak, "REST-запросы (аутентификация, токены, user info)", "HTTPS")
Rel(keycloak, usersDb, "JDBC", "SQL")

Rel(individualsApi, usersService, "Feign/OpenAPI: CRUD профиля")
Rel(individualsApi, walletsService, "Feign/OpenAPI: управление кошельком")
Rel(individualsApi, paymentsService, "Feign/OpenAPI: создание/подтверждение транзакций")

Rel(usersService, usersDb, "JPA", "SQL")
Rel(walletsService, walletsDb, "JPA", "SQL")
Rel(paymentsService, paymentsDb, "JPA", "SQL")

Rel(paymentsService, currencyService, "REST-запросы за курсами валют")
Rel(currencyService, extFx, "HTTP-запросы", "JSON API")

Rel(paymentsService, fakeProvider, "Инициирует платёж (REST API)")
Rel(fakeProvider, webhookService, "Отправка webhook о статусе платежа", "HTTP POST")

Rel(webhookService, kafka, "Публикация события PaymentResult")
Rel(kafka, paymentsService, "Событие: обновить статус транзакции")
Rel(kafka, walletsService, "Событие: обновить баланс кошелька")
Rel(kafka, notifyService, "Событие: отправить уведомление")

Rel(notifyService, user, "Email/SMS/внутренние уведомления")

Rel(grafana, individualsApi, "Метрики и логи (Prometheus + Loki)")
Rel(grafana, paymentsService, "Метрики и логи")
Rel(grafana, walletsService, "Метрики и логи")

@enduml
```
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

COMPOSE_FILE=docker-compose.yml

DC=docker compose -f $(COMPOSE_FILE)

up:
	$(DC) up -d --build

down:
	$(DC) down

stop:
	$(DC) stop

restart: down up

logs:
	$(DC) logs -f $(SERVICE)

ps:
	$(DC) ps

sh:
	$(DC) exec $(SERVICE) sh

clean:
	$(DC) down -v --remove-orphans

rebuild:
	$(DC) build --no-cache

health:
	@$(DC) ps --format json | jq -r '.[] | "\(.Name): \(.State)"'

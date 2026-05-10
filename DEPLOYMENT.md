# POS Backend Deployment

## Local Production-Like Run

1. Create production env file:

```bash
cp .env.prod.example .env.prod
```

2. Edit `.env.prod` and replace every placeholder secret.

3. Start the stack:

```bash
docker compose --env-file .env.prod up --build
```

4. Check health:

```bash
curl http://localhost:8080/actuator/health
```

## Database Migrations

Flyway runs SQL files from:

```text
src/main/resources/db/migration
```

Rules:

- Do not edit a migration after it has run on any shared database.
- Add new changes as `V3__description.sql`, `V4__description.sql`, and so on.
- Keep Hibernate `ddl-auto=validate` in production.
- Use `baseline-on-migrate` only for adopting an existing database, not for fresh production databases.

## Required Production Environment

Minimum required values:

- `SPRING_PROFILES_ACTIVE=prod`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `REDIS_HOST`
- `REDIS_PORT`
- `JWT_SECRET`
- `APP_CORS_ALLOWED_ORIGINS`
- `FRONTEND_RESET_PASSWORD_URL`
- mail settings if password reset is enabled
- Midtrans settings if online payment is enabled

## Capacity Notes

For about 1000 users, start simple:

- 1 app instance, 1 PostgreSQL instance, 1 Redis instance.
- Hikari pool 20-30 connections max.
- Keep checkout transaction short.
- Watch DB CPU, slow queries, connection pool usage, Redis latency, and JVM memory.

Scale the app horizontally only after moving rate limiting and all shared state to Redis or the database.

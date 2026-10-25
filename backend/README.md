# TechnoBoost Backend

> A React frontend lives in the sibling `../frontend` folder (login, registration, profile, admin user management). See its README for setup.

Spring Boot 4.1 / Java 25 starter API with JWT authentication, refresh tokens, email verification, request audit logging, and Flyway-managed PostgreSQL.

## Requirements

- Java 25
- Docker (for local PostgreSQL and Testcontainers-based tests)

No local Maven needed — use the included wrapper (`./mvnw`).

## Quick start

```bash
docker compose up -d        # PostgreSQL 17 with technoboost_dev pre-created
./mvnw spring-boot:run      # starts on :8080 with the dev profile
```

Swagger UI (dev only): http://localhost:8080/swagger-ui.html

## Profiles

| Profile | Database | Schema | Notes |
|---|---|---|---|
| `dev` (default) | localhost:5432/`technoboost_dev` | Flyway migrates, Hibernate validates | Swagger on, SQL logging on |
| `test` | Throwaway Testcontainers PostgreSQL | Flyway migrates, Hibernate validates | Used by `./mvnw test` |
| `prod` | From AWS Secrets Manager | Flyway migrates, Hibernate validates | Swagger off; requires `JWT_SECRET` |

Schema changes go in `src/main/resources/db/migration/V<n>__<description>.sql` — Flyway owns the schema in every profile (`ddl-auto` is always `validate`).

## Environment variables

| Variable | Required | Default |
|---|---|---|
| `JWT_SECRET` | prod: yes (startup fails without it) | dev/test: built-in local-only value |
| `AWS_SECRET_NAME` | prod | `prod/technoboost/db` |
| `AWS_REGION` | prod | `ap-south-1` |

## API

- `POST /api/v1/auth/register` · `login` · `refresh-token` · `logout` · `resend-verification`
- `GET  /api/v1/auth/verify-email?token=...`
- `GET  /api/v1/users/me`
- `GET  /api/v1/admin/users` (paged) · `PATCH /api/v1/admin/users/{id}/status` (activate/deactivate)
- `GET  /api/v1/admin/audit-logs` (+ `/users/{id}`, `/users/{id}/today`, `/users/{id}/last-week`)

The dev profile seeds an admin account: `admin@technoboost.com` / `Admin@123`.

Access tokens last 15 minutes; refresh tokens 7 days (rotated on use). Auth endpoints are rate-limited (20 requests/min per IP).

### Date-times and time zones

The JVM, database, and all persisted timestamps are pinned to **UTC** regardless of host (EC2, laptop, CI). Responses render date-times in the caller's zone: send an IANA zone in the `X-Timezone` header and every timestamp comes back as ISO-8601 with that offset; omit it for UTC.

```bash
curl -H "X-Timezone: Asia/Kolkata" ...   # "timestamp": "2026-06-11T10:30:00+05:30"
curl ...                                 # "timestamp": "2026-06-11T05:00:00Z"
```

Invalid zone values are ignored (falls back to UTC). Date-time **query parameters** (e.g. audit log `from`/`to`) are interpreted as UTC.

### API docs

Swagger UI and the OpenAPI endpoint are **dev-only**, enforced twice: springdoc is disabled outside the dev profile, and the security layer only permits `/swagger-ui/**` + `/v3/api-docs/**` when `app.swagger.public=true` (set only in dev).

## Tests

```bash
./mvnw test
```

Tests boot the full context against a Testcontainers PostgreSQL, so migrations are exercised on every run. Docker must be running.

## Known gaps

- **Email sending is a stub** — `EmailVerificationService` logs the verification link instead of sending it (see the `TODO` there). Wire up SES/SMTP before relying on verification.
- Registration does not currently trigger a verification email; only `resend-verification` creates a token.

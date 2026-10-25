# StarterProject

Full-stack starter: Spring Boot 4.1 / Java 25 API (`backend/`) + Vite / React 19 client (`frontend/`).

```bash
# 1. database
cd backend && docker compose up -d

# 2. API on :8080
./mvnw spring-boot:run

# 3. UI on :5173
cd ../frontend && npm install && npm run dev
```

Dev admin login: `admin@technoboost.com` / `Admin@123`. See `backend/README.md` and `frontend/README.md` for details.

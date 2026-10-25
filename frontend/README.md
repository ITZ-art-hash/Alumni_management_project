# TechnoBoost Frontend

Vite + React 19 client for the TechnoBoost backend: login, registration, email verification, profile, and admin user management.

## Run

```bash
npm install
npm run dev      # http://localhost:5173, proxies /api -> localhost:8080
```

Start the backend first (`cd ../backend && docker compose up -d && ./mvnw spring-boot:run`).

Dev admin login: `admin@technoboost.com` / `Admin@123` (seeded by the backend dev profile).

## How it's wired

- `src/api/axiosClient.js` — single axios instance. Every request carries `Authorization: Bearer <token>` and `X-Timezone` (the browser's IANA zone, so the API returns timestamps in the user's local time). A 401 triggers one single-flight refresh-token attempt, then retries; if refresh fails it redirects to `/login`.
- `src/contexts/AuthContext.jsx` — `{ user, isAdmin, login, register, logout }`; tokens persist in `localStorage` under `auth_tokens`.
- `src/components/ProtectedRoute.jsx` — auth gate; `requireAdmin` variant for `/users`.
- Pages own routes (`src/pages/`), API calls live in `src/api/`, never inline in components.

## Production build

```bash
npm run build    # outputs dist/
```

Set `VITE_API_BASE_URL` (e.g. `https://api.example.com/api/v1`) in `.env.production` when the API isn't behind the same origin.

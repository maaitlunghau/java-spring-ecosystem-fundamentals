# 09 · Fullstack User Management

Project tổng hợp: Spring Boot backend + React (Vite) frontend, auth bằng httpOnly cookie.

## Cấu trúc
- `backend/` — Spring Boot API (:8081) + Docker Compose (MySQL, Redis, Mailpit)
- `frontend/` — React + Vite SPA (:5173)

## Chạy
```bash
# Hạ tầng + backend
cd backend && docker compose up -d && ./mvnw spring-boot:run
# Frontend (terminal khác)
cd frontend && npm install && npm run dev
```

Tài khoản admin seed sẵn: `admin@usermanagement.dev` / `112233`.

Chi tiết: `docs/plans/09-frontend-react-vite-setup.md`.

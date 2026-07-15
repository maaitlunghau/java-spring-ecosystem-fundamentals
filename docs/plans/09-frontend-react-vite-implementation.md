# Project 09 Frontend (React + Vite, cookie auth) — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Dựng frontend React + Vite hoàn chỉnh cho Project 09, xác thực bằng httpOnly cookie, chạy end-to-end với backend Spring Boot.

**Architecture:** Backend Spring Boot (`:8081`) chuyển từ Bearer-header sang **httpOnly cookie** (access + refresh) + CSRF double-submit. Frontend SPA (`:5173`) không đọc token — chỉ `withCredentials`, xác định đăng nhập qua `GET /api/me`, auto-refresh khi 401 bằng single-flight interceptor. Repo project tách thành `backend/` + `frontend/`.

**Tech Stack:** React 19, Vite, TypeScript, TanStack Query v5, Axios, React Router v7, React Hook Form + Zod, Tailwind CSS v4, js-cookie.

**Spec nguồn:** `docs/plans/09-frontend-react-vite-setup.md`

## Global Constraints

- React **19**, Vite latest, TypeScript, Tailwind CSS **v4** (`@tailwindcss/vite`), TanStack Query **v5**, React Router **v7**.
- Auth token lưu **httpOnly cookie** — TUYỆT ĐỐI không dùng localStorage/sessionStorage cho token.
- Frontend chạy `:5173`, backend `:8081`. CORS `allowCredentials=true`, origin cụ thể.
- Cookie dev: `SameSite=Lax`, `secure(false)`; production sẽ đổi `Secure` + `SameSite=None`.
- Backend giữ token **cả trong body lẫn cookie** (Swagger còn dùng). Frontend chỉ dựa cookie.
- CSRF: `CookieCsrfTokenRepository.withHttpOnlyFalse()`, miễn cho `/api/auth/**`.
- Commit convention: `type(scope): subject` — scope `user-management`, lowercase, không dấu chấm cuối, không co-author.
- Không có test infra ở cả 2 tầng → mỗi task xác minh bằng **compile + curl + DevTools/browser** (không phải unit test). Đây là chủ ý theo phạm vi spec ("ngoài phạm vi: test frontend").

**Backend paths** (trước khi restructure — Task 4 sẽ đổi): `projects/09-fullstack-user-management/src/main/java/com/maaitlunghau/__fullstack_user_management/`

---

## Task 1: Backend — đọc access token từ cookie

**Files:**
- Modify: `.../security/JwtAuthenticationFilter.java` (hiện đọc header ở dòng ~52–58)

**Interfaces:**
- Produces: `JwtAuthenticationFilter` xác thực dựa trên cookie `access_token`, fallback header `Authorization: Bearer`.

- [ ] **Step 1: Xem code hiện tại**

Run: `sed -n '40,70p' projects/09-fullstack-user-management/src/main/java/com/maaitlunghau/__fullstack_user_management/security/JwtAuthenticationFilter.java`
Xác nhận đoạn `String header = request.getHeader("Authorization")`.

- [ ] **Step 2: Thêm import + helper `resolveToken`**

Thêm import `jakarta.servlet.http.Cookie;` (nếu chưa có). Thêm method private trong class:

```java
private String resolveToken(HttpServletRequest request) {
    if (request.getCookies() != null) {
        for (Cookie c : request.getCookies()) {
            if ("access_token".equals(c.getName())) {
                return c.getValue();
            }
        }
    }
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
        return header.substring(7);
    }
    return null;
}
```

- [ ] **Step 3: Dùng `resolveToken` trong `doFilterInternal`**

Thay đoạn đọc header cũ bằng:

```java
String token = resolveToken(request);
if (token == null) {
    filterChain.doFilter(request, response);
    return;
}
// ... phần validate token giữ nguyên như cũ (dùng biến `token`)
```

- [ ] **Step 4: Compile**

Run: `cd projects/09-fullstack-user-management && ./mvnw -q compile`
Expected: BUILD SUCCESS.

- [ ] **Step 5: Commit**

```bash
git add projects/09-fullstack-user-management/src/main/java/com/maaitlunghau/__fullstack_user_management/security/JwtAuthenticationFilter.java
git commit -m "feat(user-management): read jwt from access_token cookie in auth filter"
```

---

## Task 2: Backend — set httpOnly cookie khi login / refresh / social-exchange

**Files:**
- Create: `.../util/CookieUtils.java`
- Modify: `.../controller/AuthController.java` (login, refresh)
- Modify: `.../controller/OAuth2ExchangeController.java` (exchange)

**Interfaces:**
- Consumes: `AuthResponse { accessToken, refreshToken, tokenType, expiresIn }`, `HttpServletResponse`.
- Produces: `CookieUtils.setAuthCookies(HttpServletResponse res, AuthResponse tokens)` và `CookieUtils.clearAuthCookies(HttpServletResponse res)`.

- [ ] **Step 1: Tạo `CookieUtils`**

Create `.../util/CookieUtils.java`:

```java
package com.maaitlunghau.__fullstack_user_management.util;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import com.maaitlunghau.__fullstack_user_management.dto.response.AuthResponse;

import jakarta.servlet.http.HttpServletResponse;

public final class CookieUtils {

    private CookieUtils() {}

    public static void setAuthCookies(HttpServletResponse response, AuthResponse tokens) {
        ResponseCookie access = ResponseCookie.from("access_token", tokens.accessToken())
            .httpOnly(true).secure(false)
            .path("/").sameSite("Lax")
            .maxAge(Duration.ofSeconds(tokens.expiresIn()))
            .build();

        ResponseCookie refresh = ResponseCookie.from("refresh_token", tokens.refreshToken())
            .httpOnly(true).secure(false)
            .path("/api/auth/refresh-token").sameSite("Lax")
            .maxAge(Duration.ofDays(7))
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, access.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());
    }

    public static void clearAuthCookies(HttpServletResponse response) {
        ResponseCookie access = ResponseCookie.from("access_token", "")
            .httpOnly(true).path("/").maxAge(0).build();
        ResponseCookie refresh = ResponseCookie.from("refresh_token", "")
            .httpOnly(true).path("/api/auth/refresh-token").maxAge(0).build();
        response.addHeader(HttpHeaders.SET_COOKIE, access.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());
    }
}
```

- [ ] **Step 2: Set cookie trong `AuthController.login`**

Thêm param `HttpServletResponse response` vào method `login`, gọi `CookieUtils.setAuthCookies(response, tokens);` trước khi `return`. Giữ nguyên body trả về.

```java
@PostMapping("/login")
public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                       HttpServletRequest servletRequest,
                                       HttpServletResponse response) {
    AuthResponse tokens = authService.login(request,
        RequestUtils.clientIp(servletRequest), RequestUtils.userAgent(servletRequest));
    CookieUtils.setAuthCookies(response, tokens);
    return ApiResponse.ok("Đăng nhập thành công", tokens);
}
```

- [ ] **Step 3: Set cookie trong `AuthController.refresh`**

Tương tự: thêm `HttpServletResponse response`, gọi `CookieUtils.setAuthCookies(response, tokens);` trước return.

- [ ] **Step 4: Set cookie trong `OAuth2ExchangeController.exchange`**

Thêm `HttpServletResponse response` vào `exchange`, gọi `CookieUtils.setAuthCookies(response, tokens);` trước return.

- [ ] **Step 5: Import `HttpServletResponse`**

Đảm bảo cả 2 controller có `import jakarta.servlet.http.HttpServletResponse;`.

- [ ] **Step 6: Compile**

Run: `cd projects/09-fullstack-user-management && ./mvnw -q compile`
Expected: BUILD SUCCESS.

- [ ] **Step 7: Commit**

```bash
git add projects/09-fullstack-user-management/src/main/java/com/maaitlunghau/__fullstack_user_management/util/CookieUtils.java \
        projects/09-fullstack-user-management/src/main/java/com/maaitlunghau/__fullstack_user_management/controller/AuthController.java \
        projects/09-fullstack-user-management/src/main/java/com/maaitlunghau/__fullstack_user_management/controller/OAuth2ExchangeController.java
git commit -m "feat(user-management): set httponly auth cookies on login refresh exchange"
```

---

## Task 3: Backend — CSRF, logout clear cookie, CORS header

**Files:**
- Modify: `.../config/SecurityConfig.java` (csrf hiện đang disable)
- Modify: `.../controller/AuthController.java` (logout)
- Modify: `.../config/CorsConfig.java` (allowedHeaders)

**Interfaces:**
- Consumes: `CookieUtils.clearAuthCookies` (Task 2).
- Produces: server phát cookie `XSRF-TOKEN`, chấp nhận header `X-XSRF-TOKEN`, logout xóa cookie.

- [ ] **Step 1: Bật CSRF trong `SecurityConfig`**

Thay `.csrf(AbstractHttpConfigurer::disable)` bằng:

```java
.csrf(csrf -> csrf
    .csrfTokenRepository(org.springframework.security.web.csrf.CookieCsrfTokenRepository.withHttpOnlyFalse())
    .csrfTokenRequestHandler(new org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler())
    .ignoringRequestMatchers("/api/auth/**")
)
```

- [ ] **Step 2: Đảm bảo cookie `XSRF-TOKEN` được phát ngay (Spring Security 6 nạp lazy)**

`CookieCsrfTokenRepository` chỉ ghi cookie khi token được "chạm". Thêm filter buộc render token trên mỗi request, để `GET /api/me` lúc load app đã trả kèm `XSRF-TOKEN`.

Create `.../security/CsrfCookieFilter.java`:

```java
package com.maaitlunghau.__fullstack_user_management.security;

import java.io.IOException;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Buộc CsrfToken deferred được render → CookieCsrfTokenRepository ghi cookie XSRF-TOKEN. */
public class CsrfCookieFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        if (csrfToken != null) {
            csrfToken.getToken();   // chạm token → repository ghi cookie
        }
        filterChain.doFilter(request, response);
    }
}
```

Trong `SecurityConfig.filterChain`, đăng ký sau `BasicAuthenticationFilter`:

```java
.addFilterAfter(new CsrfCookieFilter(),
    org.springframework.security.web.authentication.www.BasicAuthenticationFilter.class)
```

- [ ] **Step 3: logout xóa cookie**

Trong `AuthController.logout`, thêm `HttpServletResponse response` param, gọi `CookieUtils.clearAuthCookies(response);` sau khi `authService.logout(...)`.

- [ ] **Step 4: CORS thêm header CSRF**

Trong `CorsConfig`, sửa allowedHeaders:

```java
config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-XSRF-TOKEN"));
```

- [ ] **Step 5: Compile**

Run: `cd projects/09-fullstack-user-management && ./mvnw -q compile`
Expected: BUILD SUCCESS.

- [ ] **Step 6: Verify end-to-end bằng curl (hạ tầng phải chạy)**

```bash
cd projects/09-fullstack-user-management
docker compose up -d
./mvnw spring-boot:run &   # đợi khởi động
# Login, lưu cookie
curl -i -c cookies.txt -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@usermanagement.dev","password":"112233"}'
# Kỳ vọng: có "Set-Cookie: access_token=...; HttpOnly" và "refresh_token"
# Gọi /api/me CHỈ bằng cookie (không Authorization header)
curl -i -b cookies.txt http://localhost:8081/api/me
# Kỳ vọng: 200 + JSON user admin. Login response cũng phải có "Set-Cookie: XSRF-TOKEN"
```

- [ ] **Step 7: Commit**

```bash
git add projects/09-fullstack-user-management/src/main/java/com/maaitlunghau/__fullstack_user_management/config/SecurityConfig.java \
        projects/09-fullstack-user-management/src/main/java/com/maaitlunghau/__fullstack_user_management/config/CorsConfig.java \
        projects/09-fullstack-user-management/src/main/java/com/maaitlunghau/__fullstack_user_management/controller/AuthController.java \
        projects/09-fullstack-user-management/src/main/java/com/maaitlunghau/__fullstack_user_management/security/CsrfCookieFilter.java
git commit -m "feat(user-management): enable csrf and clear auth cookies on logout"
```

---

## Task 4: Restructure — tách `backend/` + `frontend/`

**Files:**
- Move: toàn bộ file backend hiện ở root project → `backend/`
- Create: `projects/09-fullstack-user-management/README.md`

**Interfaces:**
- Produces: cấu trúc `09-fullstack-user-management/{backend/, frontend/, README.md}`.

- [ ] **Step 1: Tạo thư mục & di chuyển bằng git mv**

```bash
cd projects/09-fullstack-user-management
mkdir backend
git mv src pom.xml mvnw mvnw.cmd .mvn HELP.md note.txt docker-compose.yml .gitattributes backend/
```

- [ ] **Step 2: Verify backend vẫn build ở vị trí mới**

Run: `cd projects/09-fullstack-user-management/backend && ./mvnw -q compile`
Expected: BUILD SUCCESS.

- [ ] **Step 3: Tạo README fullstack**

Create `projects/09-fullstack-user-management/README.md`:

```markdown
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
```

- [ ] **Step 4: Commit**

```bash
git add -A projects/09-fullstack-user-management
git commit -m "refactor(user-management): split project into backend and frontend folders"
```

---

## Task 5: Scaffold Vite + cài dependencies

**Files:**
- Create: `projects/09-fullstack-user-management/frontend/` (Vite react-ts)

**Interfaces:**
- Produces: dev server chạy `:5173`, các package đã cài.

- [ ] **Step 1: Scaffold**

```bash
cd projects/09-fullstack-user-management
npm create vite@latest frontend -- --template react-ts
cd frontend
```

- [ ] **Step 2: Cài runtime deps**

```bash
npm install axios @tanstack/react-query react-router-dom react-hook-form zod @hookform/resolvers js-cookie
```

- [ ] **Step 3: Cài dev deps**

```bash
npm install -D tailwindcss @tailwindcss/vite @types/js-cookie
```

- [ ] **Step 4: Verify React 19**

Run: `cd projects/09-fullstack-user-management/frontend && node -p "require('./package.json').dependencies.react"`
Expected: `^19...`. Nếu là 18, chạy `npm install react@19 react-dom@19`.

- [ ] **Step 5: Verify dev server chạy**

Run: `npm run dev` → mở `http://localhost:5173` thấy trang Vite mặc định. Ctrl-C dừng.

- [ ] **Step 6: Commit**

```bash
cd projects/09-fullstack-user-management/frontend
git add -A
git commit -m "chore(user-management): scaffold react vite frontend with deps"
```

---

## Task 6: Cấu hình Vite + Tailwind v4

**Files:**
- Modify: `frontend/vite.config.ts`
- Modify: `frontend/src/index.css`

**Interfaces:**
- Produces: Tailwind hoạt động, server cố định port 5173.

- [ ] **Step 1: vite.config.ts**

Replace nội dung `frontend/vite.config.ts`:

```ts
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: { port: 5173 },
})
```

- [ ] **Step 2: index.css**

Replace `frontend/src/index.css` bằng:

```css
@import "tailwindcss";
```

- [ ] **Step 3: Thử một class Tailwind**

Trong `frontend/src/App.tsx`, tạm để `<h1 className="text-3xl font-bold text-blue-600">Tailwind OK</h1>`.

- [ ] **Step 4: Verify**

Run: `npm run dev` → thấy chữ xanh, to, đậm ⇒ Tailwind hoạt động. Dừng server.

- [ ] **Step 5: Commit**

```bash
git add frontend/vite.config.ts frontend/src/index.css frontend/src/App.tsx
git commit -m "chore(user-management): configure vite and tailwind v4"
```

---

## Task 7: Nền tảng — env, types, queryClient, providers

**Files:**
- Create: `frontend/.env`, `frontend/.env.example`
- Create: `frontend/src/lib/env.ts`, `frontend/src/lib/queryClient.ts`
- Create: `frontend/src/types/api.ts`
- Modify: `frontend/src/main.tsx`

**Interfaces:**
- Produces: `API_URL`, `queryClient`, types `ApiResponse<T>`, `Page<T>`, `UserResponse`. `main.tsx` bọc `QueryClientProvider` + `BrowserRouter`.

- [ ] **Step 1: env**

Create `frontend/.env`:
```
VITE_API_URL=http://localhost:8081
```
Create `frontend/.env.example` với cùng nội dung.

- [ ] **Step 2: lib/env.ts**

```ts
export const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8081'
```

- [ ] **Step 3: types/api.ts**

```ts
export interface ApiResponse<T> {
  status: number
  message: string
  data: T
  timestamp: string
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export interface UserResponse {
  id: number
  email: string
  fullName: string
  avatarUrl: string | null
  isEmailVerified: boolean
  isEnabled: boolean
  role: 'ADMIN' | 'USER'
  createdAt: string
}
```

- [ ] **Step 4: lib/queryClient.ts**

```ts
import { QueryClient } from '@tanstack/react-query'

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      staleTime: 30_000,
      refetchOnWindowFocus: false,
    },
  },
})
```

- [ ] **Step 5: main.tsx**

Replace `frontend/src/main.tsx` (AuthProvider thêm ở Task 9 — tạm chưa bọc):

```tsx
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { QueryClientProvider } from '@tanstack/react-query'
import { BrowserRouter } from 'react-router-dom'
import { queryClient } from './lib/queryClient'
import App from './App'
import './index.css'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </QueryClientProvider>
  </StrictMode>
)
```

- [ ] **Step 6: Verify compile**

Run: `cd projects/09-fullstack-user-management/frontend && npx tsc --noEmit`
Expected: không lỗi.

- [ ] **Step 7: Commit**

```bash
git add frontend/.env.example frontend/src/lib frontend/src/types frontend/src/main.tsx
git commit -m "feat(user-management): add env types queryclient and app providers"
```

> Lưu ý: `.env` thường gitignored — chỉ commit `.env.example`.

---

## Task 8: Axios instance + interceptors (CSRF + single-flight refresh)

**Files:**
- Create: `frontend/src/api/axios.ts`

**Interfaces:**
- Consumes: `API_URL` (Task 7), cookie `XSRF-TOKEN` (backend Task 3).
- Produces: `api` (AxiosInstance) — `withCredentials`, tự gắn CSRF, tự refresh khi 401.

- [ ] **Step 1: Tạo axios.ts**

```ts
import axios from 'axios'
import Cookies from 'js-cookie'
import { API_URL } from '../lib/env'

export const api = axios.create({
  baseURL: API_URL,
  withCredentials: true,
})

api.interceptors.request.use((config) => {
  const method = config.method?.toUpperCase()
  if (method && ['POST', 'PUT', 'PATCH', 'DELETE'].includes(method)) {
    const csrf = Cookies.get('XSRF-TOKEN')
    if (csrf) config.headers['X-XSRF-TOKEN'] = csrf
  }
  return config
})

let isRefreshing = false
let waiters: Array<() => void> = []

api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config
    const status = error.response?.status
    const isAuthCall = original?.url?.includes('/api/auth/')

    if (status !== 401 || isAuthCall || original?._retry) {
      return Promise.reject(error)
    }
    original._retry = true

    if (isRefreshing) {
      await new Promise<void>((resolve) => waiters.push(resolve))
      return api(original)
    }

    isRefreshing = true
    try {
      await api.post('/api/auth/refresh-token')
      waiters.forEach((w) => w())
      waiters = []
      return api(original)
    } catch (refreshErr) {
      waiters = []
      window.location.href = '/login'
      return Promise.reject(refreshErr)
    } finally {
      isRefreshing = false
    }
  }
)
```

- [ ] **Step 2: Verify compile**

Run: `npx tsc --noEmit`
Expected: không lỗi. (Nếu TS phàn nàn `_retry`, thêm `// @ts-expect-error augment` hoặc khai báo `declare module 'axios'` mở rộng `InternalAxiosRequestConfig` với `_retry?: boolean`.)

- [ ] **Step 3: Commit**

```bash
git add frontend/src/api/axios.ts
git commit -m "feat(user-management): add axios instance with csrf and refresh interceptor"
```

---

## Task 9: Auth API, hooks, AuthProvider, ProtectedRoute

**Files:**
- Create: `frontend/src/api/auth.ts`
- Create: `frontend/src/hooks/useAuth.ts`
- Create: `frontend/src/context/AuthProvider.tsx`
- Create: `frontend/src/components/ProtectedRoute.tsx`
- Modify: `frontend/src/main.tsx` (bọc AuthProvider)

**Interfaces:**
- Consumes: `api` (Task 8), `UserResponse`/`ApiResponse` (Task 7).
- Produces: `useMe()`, `useLogin()`, `useLogout()`; `useAuth()` → `{ user, isLoading }`; `<ProtectedRoute role?>`.

- [ ] **Step 1: api/auth.ts**

```ts
import { api } from './axios'
import type { ApiResponse, UserResponse } from '../types/api'

export const authApi = {
  me: () => api.get<ApiResponse<UserResponse>>('/api/me').then((r) => r.data.data),
  login: (body: { email: string; password: string }) =>
    api.post('/api/auth/login', body),
  logout: () => api.post('/api/auth/logout'),
}
```

- [ ] **Step 2: hooks/useAuth.ts**

```ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { authApi } from '../api/auth'

export function useMe() {
  return useQuery({
    queryKey: ['me'],
    queryFn: authApi.me,
    retry: false,
    staleTime: 5 * 60_000,
  })
}

export function useLogin() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: authApi.login,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['me'] }),
  })
}

export function useLogout() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: authApi.logout,
    onSuccess: () => qc.setQueryData(['me'], null),
  })
}
```

- [ ] **Step 3: context/AuthProvider.tsx**

```tsx
import { createContext, useContext, type ReactNode } from 'react'
import { useMe } from '../hooks/useAuth'
import type { UserResponse } from '../types/api'

interface AuthState {
  user: UserResponse | null | undefined
  isLoading: boolean
}

const AuthContext = createContext<AuthState>({ user: undefined, isLoading: true })

export function AuthProvider({ children }: { children: ReactNode }) {
  const { data, isLoading } = useMe()
  return (
    <AuthContext.Provider value={{ user: data ?? null, isLoading }}>
      {children}
    </AuthContext.Provider>
  )
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  return useContext(AuthContext)
}
```

- [ ] **Step 4: components/ProtectedRoute.tsx**

```tsx
import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthProvider'

export function ProtectedRoute({ role }: { role?: 'ADMIN' | 'USER' }) {
  const { user, isLoading } = useAuth()
  if (isLoading) return <div className="p-8">Đang tải…</div>
  if (!user) return <Navigate to="/login" replace />
  if (role && user.role !== role) return <Navigate to="/" replace />
  return <Outlet />
}
```

- [ ] **Step 5: Bọc AuthProvider trong main.tsx**

Trong `frontend/src/main.tsx`, import `AuthProvider` và bọc quanh `<App />` (bên trong `BrowserRouter`):

```tsx
import { AuthProvider } from './context/AuthProvider'
// ...
<BrowserRouter>
  <AuthProvider>
    <App />
  </AuthProvider>
</BrowserRouter>
```

- [ ] **Step 6: Verify compile**

Run: `npx tsc --noEmit`
Expected: không lỗi.

- [ ] **Step 7: Commit**

```bash
git add frontend/src/api/auth.ts frontend/src/hooks/useAuth.ts frontend/src/context/AuthProvider.tsx frontend/src/components/ProtectedRoute.tsx frontend/src/main.tsx
git commit -m "feat(user-management): add cookie-based auth hooks provider and route guard"
```

---

## Task 10: Router + LoginPage

**Files:**
- Create: `frontend/src/pages/LoginPage.tsx`
- Modify: `frontend/src/App.tsx`

**Interfaces:**
- Consumes: `useLogin` (Task 9), `ProtectedRoute` (Task 9).
- Produces: route `/login` + route bảo vệ `/users` (UsersPage thêm ở Task 11 — tạm placeholder).

- [ ] **Step 1: pages/LoginPage.tsx**

```tsx
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useNavigate } from 'react-router-dom'
import { useLogin } from '../hooks/useAuth'

const schema = z.object({
  email: z.string().email('Email không hợp lệ'),
  password: z.string().min(1, 'Bắt buộc'),
})
type FormValues = z.infer<typeof schema>

export default function LoginPage() {
  const { register, handleSubmit, formState: { errors } } =
    useForm<FormValues>({ resolver: zodResolver(schema) })
  const login = useLogin()
  const navigate = useNavigate()

  const onSubmit = (values: FormValues) =>
    login.mutate(values, { onSuccess: () => navigate('/users') })

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="max-w-sm mx-auto mt-20 space-y-4">
      <h1 className="text-2xl font-bold">Đăng nhập</h1>
      <input {...register('email')} placeholder="Email"
        className="w-full border rounded px-3 py-2" />
      {errors.email && <p className="text-red-600 text-sm">{errors.email.message}</p>}
      <input {...register('password')} type="password" placeholder="Mật khẩu"
        className="w-full border rounded px-3 py-2" />
      {errors.password && <p className="text-red-600 text-sm">{errors.password.message}</p>}
      <button disabled={login.isPending}
        className="w-full bg-blue-600 text-white rounded py-2 disabled:opacity-50">
        {login.isPending ? 'Đang đăng nhập…' : 'Đăng nhập'}
      </button>
      {login.isError && <p className="text-red-600 text-sm">Sai email hoặc mật khẩu</p>}
    </form>
  )
}
```

- [ ] **Step 2: App.tsx router**

Replace `frontend/src/App.tsx`:

```tsx
import { Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import { ProtectedRoute } from './components/ProtectedRoute'

function UsersPlaceholder() {
  return <div className="p-8 text-xl">Users (placeholder)</div>
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route element={<ProtectedRoute role="ADMIN" />}>
        <Route path="/users" element={<UsersPlaceholder />} />
      </Route>
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  )
}
```

- [ ] **Step 3: Verify compile**

Run: `npx tsc --noEmit`
Expected: không lỗi.

- [ ] **Step 4: Commit**

```bash
git add frontend/src/pages/LoginPage.tsx frontend/src/App.tsx
git commit -m "feat(user-management): add login page and protected router"
```

---

## Task 11: Users API + hook + UsersPage (bảng + phân trang)

**Files:**
- Create: `frontend/src/api/users.ts`
- Create: `frontend/src/hooks/useUsers.ts`
- Create: `frontend/src/pages/UsersPage.tsx`
- Modify: `frontend/src/App.tsx` (thay placeholder bằng UsersPage)

**Interfaces:**
- Consumes: `api` (Task 8), `Page<UserResponse>` (Task 7), `useLogout` (Task 9).
- Produces: `useUsers(params)`; trang `/users` thật.

- [ ] **Step 1: api/users.ts**

```ts
import { api } from './axios'
import type { ApiResponse, Page, UserResponse } from '../types/api'

export interface UsersParams {
  page?: number
  size?: number
  sort?: string
  keyword?: string
  role?: string
  enabled?: boolean
}

export const usersApi = {
  list: (params: UsersParams) =>
    api.get<ApiResponse<Page<UserResponse>>>('/api/users', { params }).then((r) => r.data.data),
}
```

- [ ] **Step 2: hooks/useUsers.ts**

```ts
import { useQuery } from '@tanstack/react-query'
import { usersApi, type UsersParams } from '../api/users'

export function useUsers(params: UsersParams) {
  return useQuery({
    queryKey: ['users', params],
    queryFn: () => usersApi.list(params),
    placeholderData: (prev) => prev,
  })
}
```

- [ ] **Step 3: pages/UsersPage.tsx**

```tsx
import { useState } from 'react'
import { useUsers } from '../hooks/useUsers'
import { useLogout } from '../hooks/useAuth'
import { useNavigate } from 'react-router-dom'

export default function UsersPage() {
  const [page, setPage] = useState(0)
  const [keyword, setKeyword] = useState('')
  const { data, isLoading, isError } = useUsers({ page, size: 20, sort: 'createdAt,desc', keyword })
  const logout = useLogout()
  const navigate = useNavigate()

  return (
    <div className="max-w-4xl mx-auto p-8">
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-bold">Quản lý User</h1>
        <button onClick={() => logout.mutate(undefined, { onSuccess: () => navigate('/login') })}
          className="text-sm text-red-600">Đăng xuất</button>
      </div>

      <input value={keyword} onChange={(e) => { setPage(0); setKeyword(e.target.value) }}
        placeholder="Tìm theo tên/email…" className="border rounded px-3 py-2 mb-4 w-full" />

      {isLoading && <p>Đang tải…</p>}
      {isError && <p className="text-red-600">Không tải được danh sách</p>}

      {data && (
        <>
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b">
                <th className="py-2">ID</th><th>Email</th><th>Họ tên</th><th>Role</th><th>Enabled</th>
              </tr>
            </thead>
            <tbody>
              {data.content.map((u) => (
                <tr key={u.id} className="border-b">
                  <td className="py-2">{u.id}</td>
                  <td>{u.email}</td>
                  <td>{u.fullName}</td>
                  <td>{u.role}</td>
                  <td>{u.isEnabled ? '✓' : '✗'}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="flex items-center gap-4 mt-4">
            <button disabled={page === 0} onClick={() => setPage((p) => p - 1)}
              className="px-3 py-1 border rounded disabled:opacity-50">Trước</button>
            <span>Trang {data.number + 1} / {data.totalPages}</span>
            <button disabled={page + 1 >= data.totalPages} onClick={() => setPage((p) => p + 1)}
              className="px-3 py-1 border rounded disabled:opacity-50">Sau</button>
          </div>
        </>
      )}
    </div>
  )
}
```

- [ ] **Step 4: Nối UsersPage vào router**

Trong `frontend/src/App.tsx`: bỏ `UsersPlaceholder`, import `UsersPage from './pages/UsersPage'`, đổi element route `/users` thành `<UsersPage />`.

- [ ] **Step 5: Verify compile**

Run: `npx tsc --noEmit`
Expected: không lỗi.

- [ ] **Step 6: Commit**

```bash
git add frontend/src/api/users.ts frontend/src/hooks/useUsers.ts frontend/src/pages/UsersPage.tsx frontend/src/App.tsx
git commit -m "feat(user-management): add users list page with search and pagination"
```

---

## Task 12: Xác minh end-to-end

**Files:** (không sửa code — chỉ kiểm thử thủ công)

- [ ] **Step 1: Khởi động cả 2 tầng**

```bash
# Terminal 1
cd projects/09-fullstack-user-management/backend && docker compose up -d && ./mvnw spring-boot:run
# Terminal 2
cd projects/09-fullstack-user-management/frontend && npm run dev
```

- [ ] **Step 2: Login flow**

Mở `http://localhost:5173/login` → đăng nhập `admin@usermanagement.dev` / `112233` → chuyển sang `/users`, thấy bảng.

- [ ] **Step 3: Cookie đúng chuẩn**

DevTools > Application > Cookies (`localhost:8081`): có `access_token` (HttpOnly ✓), `refresh_token`, `XSRF-TOKEN` (không HttpOnly).

- [ ] **Step 4: Auto-refresh**

DevTools xóa cookie `access_token` (giữ `refresh_token`) → thao tác gọi API (đổi trang) → interceptor tự refresh, request thành công, không bị đá về login.

- [ ] **Step 5: Logout**

Bấm Đăng xuất → cookie bị xóa → về `/login`. Vào lại `/users` → bị chặn về `/login`.

- [ ] **Step 6: Phân trang & search**

Gõ keyword → bảng lọc; nút Trước/Sau đổi trang.

- [ ] **Step 7: (không commit — chỉ xác nhận)**

Nếu tất cả pass: đánh dấu plan hoàn tất. Ghi lại issue (nếu có) để xử lý ở roadmap.

---

## Roadmap sau plan này (ngoài phạm vi — không bắt buộc)

Nhân bản pattern Task 9–11 cho: RegisterPage, VerifyEmailPage, OAuthCallbackPage, ProfilePage (`/api/me` PATCH), UserDetailPage, Users CRUD (create/update/delete mutation + invalidate `['users']`), forgot/reset password, toast lỗi tập trung. Xem mục 13 của `docs/plans/09-frontend-react-vite-setup.md`.

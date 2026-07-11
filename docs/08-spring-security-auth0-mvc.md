# 08 — Spring Security Auth0 MVC

Hướng dẫn tích hợp **Auth0** làm Identity Provider vào Spring MVC + Thymeleaf — đây là cách tiếp cận thực tế trong production khi không muốn tự quản lý user store, password, OAuth2 credentials của từng provider.

---

## Mục tiêu

- Hiểu sự khác biệt giữa tự implement OAuth2 (project 07) và dùng Auth0 làm IdP tập trung
- Tích hợp Auth0 login vào Spring MVC chỉ với config — không cần custom service phức tạp
- Implement custom `LogoutHandler` để clear cả local session lẫn Auth0 session
- Hiển thị thông tin user từ OIDC claims (`name`, `picture`, v.v.)

---

## Auth0 là gì? Tại sao dùng?

Auth0 là **Identity Provider (IdP)** — nền tảng xác thực bên thứ ba thay bạn quản lý:
- User store (đăng ký, đăng nhập, quên mật khẩu)
- Social login (Google, GitHub, Facebook, ...) — chỉ config trong Auth0 Dashboard, không cần code
- MFA, brute-force protection, email verification

**So sánh với project 07:**

| | Project 07 (OAuth2 tự implement) | Project 08 (Auth0) |
|---|---|---|
| Provider | Google + GitHub (tự cấu hình từng cái) | Auth0 (1 provider duy nhất) |
| Custom service | 2 service riêng (OidcUserService + OAuth2UserService) | Không cần — Auth0 chuẩn OIDC |
| Social login thêm | Phải code thêm service | Bật trong Auth0 Dashboard |
| User store | Tự lưu vào DB | Auth0 quản lý (hoặc tự lưu thêm) |
| Độ phức tạp code | Cao hơn | Thấp hơn |
| Phù hợp | Học, kiểm soát toàn bộ flow | Production, MVP nhanh |

---

## Tech Stack

| Thành phần | Lựa chọn |
|---|---|
| Spring Boot | 4.0.7 |
| Spring Security | OAuth2 Client + Security |
| Template engine | Thymeleaf + thymeleaf-extras-springsecurity6 |
| Database | MySQL + Spring Data JPA |
| Identity Provider | Auth0 |
| Build tool | Maven |

---

## Auth0 Authorization Code Flow

Auth0 vẫn dùng OAuth2 Authorization Code Flow + OIDC — nhưng Spring Security chỉ cần biết `issuer-uri` là tự lo hết:

```
1. User click "Login"
2. App redirect sang Auth0 Universal Login page
   (Auth0 tự hiển thị Google, GitHub, v.v. nếu đã bật trong Dashboard)
3. User đăng nhập trên Auth0
4. Auth0 redirect về: /login/oauth2/code/auth0?code=AUTH_CODE
5. Spring Security đổi code lấy access_token + ID Token (OIDC)
6. Spring Security parse ID Token — lấy sub, email, name, picture
7. Redirect về trang được bảo vệ hoặc default URL
```

Bước 2→6: Spring Security + Auth0 provider tự xử lý hoàn toàn.

---

## Tại sao chỉ cần 1 service (không cần 2 như project 07)?

Auth0 **luôn dùng OIDC** — trả về ID Token chuẩn dù user login bằng Google hay GitHub bên trong Auth0. Spring Security chỉ thấy 1 provider (Auth0), không biết user login bằng gì bên trong.

```
Project 07:  Spring App → Google (OIDC) | GitHub (OAuth2)  → 2 service
Project 08:  Spring App → Auth0 (OIDC)  → 1 service (OidcUser mặc định)
```

---

## Cấu trúc thư mục

```
08-spring-security-auth0-mvc/
├── src/main/java/.../
│   ├── Application.java
│   ├── config/
│   │   └── SecurityConfig.java
│   └── controller/
│       ├── HomeController.java
│       └── LogoutHandler.java
└── src/main/resources/
    ├── application.properties          ← config chính (commit được)
    ├── application-local.properties    ← credentials thật (gitignored)
    ├── static/
    │   └── js/
    │       └── color-modes.js
    └── templates/
        ├── index.html
        └── profile.html
```

---

## Bước 1 — Đăng ký App trên Auth0 Dashboard

1. Vào [auth0.com](https://auth0.com) → đăng ký tài khoản miễn phí
2. **Applications** → **Create Application** → chọn **Regular Web Applications**
3. Vào tab **Settings** của app vừa tạo:
   - **Allowed Callback URLs**: `http://localhost:8081/login/oauth2/code/auth0`
   - **Allowed Logout URLs**: `http://localhost:8081`
   - **Allowed Web Origins**: `http://localhost:8081`
   - Scroll xuống → **Save Changes**
4. Lưu lại 3 thứ:
   - **Domain** — dạng `dev-xxxx.us.auth0.com`
   - **Client ID**
   - **Client Secret**

> URL `/login/oauth2/code/auth0` là endpoint Spring Security tự tạo — không cần code thêm.

---

## Bước 2 — `application.properties` và credentials

### Tại sao tách ra `application-local.properties`?

Nếu hardcode credentials vào `application.properties` rồi commit → lộ key lên GitHub. Pattern chuẩn: dùng file riêng chứa secrets, gitignore file đó.

### `application.properties` (commit được — chỉ có placeholder)

```properties
spring.application.name=08-spring-security-auth0-mvc
server.port=8081
spring.profiles.active=local

# MySQL
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:3306/spring-security-auth0-mvc?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=112233
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false

# Auth0 OAuth2 — credentials defined in application-local.properties (gitignored)
spring.security.oauth2.client.registration.auth0.client-id=${AUTH0_CLIENT_ID}
spring.security.oauth2.client.registration.auth0.client-secret=${AUTH0_CLIENT_SECRET}
spring.security.oauth2.client.registration.auth0.scope=openid,profile,email
spring.security.oauth2.client.provider.auth0.issuer-uri=https://YOUR_AUTH0_DOMAIN/
```

**`scope=openid`** → kích hoạt OIDC, Auth0 trả về ID Token chứa `sub`, `email`, `name`, `picture`.

### `application-local.properties` (gitignored — chứa credentials thật)

```properties
AUTH0_CLIENT_ID=your-auth0-client-id
AUTH0_CLIENT_SECRET=your-auth0-client-secret
```

### `.gitignore`

```
**/application-local.properties
```

---

## Bước 3 — `SecurityConfig`

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final LogoutHandler logoutHandler;

    public SecurityConfig(LogoutHandler logoutHandler) {
        this.logoutHandler = logoutHandler;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/js/**", "/css/**", "/images/**", "/error").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(Customizer.withDefaults())
            .logout(logout -> logout
                .logoutUrl("/logout")
                .addLogoutHandler(logoutHandler)
            )
            .build();
    }
}
```

**Điểm quan trọng:**
- Static resources (`/js/**`, `/css/**`) phải `permitAll` — nếu không Spring Security sẽ save URL của static file làm "saved request" và redirect về đó sau khi login
- `.logoutUrl("/logout")` — chỉ accept **POST** (CSRF protection). Logout button phải dùng form POST, không phải `<a href>`
- `LogoutHandler` custom để clear cả Auth0 session (xem bước 4)

---

## Bước 4 — `LogoutHandler`

Đây là điểm khác biệt quan trọng nhất của project này. Nếu chỉ clear local Spring session mà không clear Auth0 session → user click Login lại sẽ ngay lập tức được đăng nhập lại (Auth0 vẫn còn session).

```java
@Component
public class LogoutHandler extends SecurityContextLogoutHandler {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public LogoutHandler(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {
        // Bước 1: clear Spring Security local session
        super.logout(request, response, authentication);

        // Bước 2: redirect sang Auth0 /v2/logout để clear Auth0 session
        String issuer = (String) clientRegistrationRepository
            .findByRegistrationId("auth0")
            .getProviderDetails()
            .getConfigurationMetadata()
            .get("issuer");

        String clientId = clientRegistrationRepository
            .findByRegistrationId("auth0")
            .getClientId();

        String returnTo = ServletUriComponentsBuilder
            .fromCurrentContextPath().build().toString();

        String logoutUrl = UriComponentsBuilder
            .fromUriString(issuer + "/v2/logout?client_id={clientId}&returnTo={returnTo}")
            .encode()
            .buildAndExpand(clientId, returnTo)
            .toUriString();

        response.sendRedirect(logoutUrl);
    }
}
```

**Luồng logout đầy đủ:**

```
User click "Logout"
    ↓ POST /logout (form submit — không phải GET)
Spring Security → LogoutHandler.logout()
    ↓ super.logout() — xóa SecurityContext, invalidate HTTP session
    ↓ redirect → https://YOUR_DOMAIN/v2/logout?client_id=...&returnTo=http://localhost:8081
Auth0 xóa session phía Auth0
    ↓ redirect về returnTo (http://localhost:8081)
User về trang chủ, đã logout hoàn toàn
```

---

## Bước 5 — `HomeController`

```java
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal OidcUser oidcUser) {
        model.addAttribute("profile", oidcUser.getClaims());
        return "profile";
    }
}
```

`@AuthenticationPrincipal OidcUser` — inject OIDC user từ SecurityContext. Auth0 dùng OIDC nên principal luôn là `OidcUser`. `getClaims()` trả về `Map<String, Object>` chứa tất cả OIDC claims (`name`, `email`, `picture`, `sub`, v.v.).

---

## Bước 6 — Templates

### `index.html` — Login/Logout toggle theo auth state

```html
<!-- Chỉ hiện khi chưa login -->
<a class="btn btn-sm btn-outline-secondary"
   sec:authorize="!isAuthenticated()"
   th:href="@{/oauth2/authorization/auth0}">Login</a>

<!-- Chỉ hiện khi đã login — dùng form POST để trigger LogoutHandler -->
<form sec:authorize="isAuthenticated()"
      th:action="@{/logout}" method="post" class="d-inline">
    <button type="submit" class="btn btn-sm btn-danger text-white">Logout</button>
</form>
```

**Tại sao Logout dùng form POST thay vì `<a href>`?**

Spring Security chỉ process logout với **POST** request (CSRF protection). GET request đến `/logout` bị bỏ qua → `LogoutHandler` không được gọi → Auth0 session không bị clear.

### `profile.html` — Hiển thị OIDC user info

```html
<!-- Avatar từ Auth0 -->
<img th:src="${profile.get('picture')}"
     class="rounded-circle img-fluid">

<!-- Tên user -->
<h2 th:text="${profile.get('name')}"></h2>
```

`profile` là `Map<String, Object>` từ `oidcUser.getClaims()` — truy cập bằng key string của OIDC standard claims.

---

## Chạy project

```bash
cd projects/08-spring-security-auth0-mvc

# Spring tự load application-local.properties vì spring.profiles.active=local
mvn spring-boot:run
```

Truy cập `http://localhost:8081` → click **Login** → Auth0 Universal Login → về `/profile`.

---

## Luồng hoàn chỉnh

```
[Chưa login]
User → GET /profile
    → Spring Security chặn → redirect /oauth2/authorization/auth0
    → Auth0 Universal Login page
    → User đăng nhập
    → Auth0 redirect về /login/oauth2/code/auth0?code=...
    → Spring Security xử lý: đổi code → token → parse OIDC claims
    → Redirect về /profile (saved request)
    → Controller inject OidcUser, truyền claims vào model
    → Thymeleaf render profile.html với name + avatar

[Đã login]
User → POST /logout (form submit)
    → LogoutHandler: clear Spring session + redirect Auth0 /v2/logout
    → Auth0 clear session + redirect về http://localhost:8081
    → Về trang chủ, đã logout hoàn toàn
```

---

## Điểm khác biệt so với các project trước

| | Project 06 (JWT) | Project 07 (OAuth2) | Project 08 (Auth0) |
|---|---|---|---|
| Authentication | Username/password + JWT | Google + GitHub OAuth2 | Auth0 (tất cả provider) |
| Session | Stateless | Stateful (Spring session) | Stateful (Spring session) |
| CSRF | Disable | Enable | Enable |
| Custom service | Filter + Service phức tạp | 2 OAuth2/OIDC service | Không cần custom service |
| Logout | Blacklist JWT trên Redis | Clear Spring session | Clear Spring + Auth0 session |
| User store | Tự quản lý trong DB | Tự lưu vào DB | Auth0 quản lý |

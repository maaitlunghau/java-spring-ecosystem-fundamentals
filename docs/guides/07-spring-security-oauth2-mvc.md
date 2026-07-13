# 07 — Spring Security OAuth2 Social Login

Hướng dẫn implement OAuth2 Social Login (Google + GitHub) với Spring Security từ đầu, không dùng thư viện trung gian.

---

## Mục tiêu

- Hiểu OAuth2 Authorization Code Flow thực sự hoạt động như thế nào
- Biết cách Google (OIDC) và GitHub (OAuth2 thuần) khác nhau và xử lý riêng từng loại
- Lưu thông tin user vào DB sau khi login thành công
- Bảo vệ route, xử lý session đúng chuẩn production

---

## Tech Stack

| Thành phần | Lựa chọn |
|---|---|
| Spring Boot | 4.0.7 |
| Spring Security | OAuth2 Client + Security |
| Template engine | Thymeleaf + thymeleaf-extras-springsecurity6 |
| Database | MySQL + Spring Data JPA |
| Build tool | Maven |

---

## OAuth2 Authorization Code Flow

Trước khi code, cần hiểu flow này:

```
1. User click "Login with Google"
2. App redirect sang Google với: client_id, redirect_uri, scope, state
3. Google hiển thị trang xác nhận quyền cho user
4. User đồng ý → Google redirect về: /login/oauth2/code/google?code=AUTH_CODE
5. App gửi AUTH_CODE lên Google để đổi lấy access_token
6. App dùng access_token fetch UserInfo từ Google
7. App lưu user vào DB, tạo session
8. Redirect user về /dashboard
```

Spring Security OAuth2 Client tự xử lý bước 2→6. Developer chỉ cần xử lý bước 7→8.

---

## Tại sao Google và GitHub cần 2 service riêng?

| | Google | GitHub |
|---|---|---|
| Protocol | **OIDC** (OpenID Connect) | **OAuth2 thuần** |
| Token trả về | ID Token (JWT) + access_token | access_token |
| UserInfo | Trong ID Token (`sub`, `email`, `name`, `picture`) | Fetch riêng từ `/user` endpoint |
| Spring service | `OidcUserService` | `DefaultOAuth2UserService` |
| Principal type | `OidcUser` | `OAuth2User` |

OIDC là extension của OAuth2 — thêm chuẩn để trả về thông tin user trong ID Token. Spring Security route 2 loại này sang 2 service khác nhau nên không thể dùng chung 1 class.

---

## Cấu trúc thư mục

```
07-spring-security-oauth2-mvc/
├── src/main/java/com/maaitlunghau/__spring_security_oauth2_mvc/
│   ├── Application.java
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   ├── HomeController.java
│   │   └── DashboardController.java
│   ├── handler/
│   │   └── OAuth2LoginSuccessHandler.java
│   ├── model/
│   │   ├── AuthProvider.java
│   │   ├── Role.java
│   │   └── User.java
│   ├── repository/
│   │   └── UserRepository.java
│   ├── security/
│   │   ├── UserAware.java
│   │   ├── OAuth2UserPrincipal.java
│   │   └── OidcUserPrincipal.java
│   └── service/
│       ├── CustomOAuth2UserService.java
│       └── CustomOidcUserService.java
└── src/main/resources/
    ├── application.properties
    ├── application-local.properties  ← gitignored, chứa credentials
    └── templates/
        ├── home.html
        ├── login.html
        ├── dashboard.html
        └── error.html
```

---

## Bước 1 — Đăng ký OAuth2 App trên provider

Cần làm trước khi code — phải có `client-id` và `client-secret`.

### Google

1. Vào [console.cloud.google.com](https://console.cloud.google.com)
2. Tạo project mới (hoặc chọn project có sẵn)
3. APIs & Services → Credentials → Create Credentials → **OAuth 2.0 Client ID**
4. Application type: **Web application**
5. Authorized redirect URIs: `http://localhost:8081/login/oauth2/code/google`
6. Lưu lại `client-id` và `client-secret`

### GitHub

1. GitHub → Settings → Developer settings → OAuth Apps → **New OAuth App**
2. Homepage URL: `http://localhost:8081`
3. Authorization callback URL: `http://localhost:8081/login/oauth2/code/github`
4. Lưu lại `client-id` và `client-secret`

> URL `/login/oauth2/code/{provider}` là endpoint Spring Security tự tạo sẵn — không cần code thêm.

---

## Bước 2 — Cấu hình `application.properties`

### Tại sao tách ra `application-local.properties`?

Nếu hardcode credentials vào `application.properties` rồi commit → lộ key lên GitHub → Google/GitHub tự động revoke key. Pattern chuẩn: dùng file riêng chứa secrets, gitignore file đó.

### `application.properties` (commit được — chỉ có placeholder)

```properties
spring.application.name=07-spring-security-oauth2-mvc
server.port=8081
spring.profiles.active=local

# MySQL
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:3306/spring-security-oauth2-mvc?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=112233
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false

# OAuth2 credentials — defined in application-local.properties (gitignored)
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
spring.security.oauth2.client.registration.google.scope=openid,profile,email

spring.security.oauth2.client.registration.github.client-id=${GITHUB_CLIENT_ID}
spring.security.oauth2.client.registration.github.client-secret=${GITHUB_CLIENT_SECRET}
spring.security.oauth2.client.registration.github.scope=read:user,user:email
```

**`scope=openid`** cho Google → kích hoạt OIDC flow, trả về ID Token chứa `sub`, `email`, `name`, `picture`.

### `application-local.properties` (gitignored — chứa credentials thật)

```properties
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret

GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret
```

### `.gitignore` — thêm pattern

```
**/application-local.properties
```

---

## Bước 3 — Model: `AuthProvider`, `Role`, `User`

### Tại sao `User` entity không có field `password`?

OAuth2 user xác thực qua provider — server không bao giờ nhận hay lưu password.

### Tại sao dùng `(provider, providerId)` làm unique key thay vì `email`?

Cùng một email `abc@gmail.com` có thể login bằng Google lẫn GitHub → 2 account khác nhau. Nếu unique theo email thì conflict. `providerId` là ID do provider cấp (Google `sub`, GitHub `id`) — unique trong phạm vi từng provider. Kết hợp `(provider + providerId)` mới unique toàn cục.

### `AuthProvider.java`

```java
public enum AuthProvider {
    GOOGLE,
    GITHUB
}
```

### `Role.java`

```java
public enum Role {
    USER,
    ADMIN
}
```

### `User.java`

```java
@Entity
@Table(
    name = "users",
    uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id"})
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected User() {}

    public User(String email, String name, String avatarUrl, AuthProvider provider, String providerId) {
        this.email = email;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.provider = provider;
        this.providerId = providerId;
        this.role = Role.USER;
    }

    // Cập nhật mỗi lần login lại — đảm bảo name/avatar sync với provider
    public void updateProfile(String name, String avatarUrl) {
        this.name = name;
        this.avatarUrl = avatarUrl;
    }

    // Getters only — không có setter, dùng domain method để update
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getAvatarUrl() { return avatarUrl; }
    public AuthProvider getProvider() { return provider; }
    public String getProviderId() { return providerId; }
    public Role getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }

    @Override
    public String toString() {
        return "User[id=%d, email=%s, provider=%s]".formatted(id, email, provider);
    }
}
```

---

## Bước 4 — `UserRepository`

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
}
```

Chỉ cần 1 method — tìm user khi OAuth2 callback về để xác định đây là user mới hay đã tồn tại.

---

## Bước 5 — Custom OAuth2 User Services (cốt lõi)

Đây là bước quan trọng nhất. Spring Security gọi service này sau khi nhận token từ provider.

### `UserAware.java` — interface chung

```java
public interface UserAware {
    User getUser();
}
```

Cho phép controller gọi `getUser()` mà không cần biết principal là Google hay GitHub.

### `OAuth2UserPrincipal.java` — principal cho GitHub

```java
public class OAuth2UserPrincipal implements OAuth2User, UserAware {

    private final User user;
    private final Map<String, Object> attributes;

    public OAuth2UserPrincipal(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override public Map<String, Object> getAttributes() { return attributes; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getName() { return user.getProviderId(); }

    @Override
    public User getUser() { return user; }
}
```

### `OidcUserPrincipal.java` — principal cho Google

```java
// Extend DefaultOidcUser để kế thừa toàn bộ OIDC methods
// Chỉ thêm getUser() để controller truy cập được User entity
public class OidcUserPrincipal extends DefaultOidcUser implements UserAware {

    private final User user;

    public OidcUserPrincipal(User user, OidcUser oidcUser) {
        super(
            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
            oidcUser.getIdToken(),
            oidcUser.getUserInfo()
        );
        this.user = user;
    }

    @Override
    public User getUser() { return user; }
}
```

### `CustomOAuth2UserService.java` — xử lý GitHub

```java
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);

        // GitHub attributes: id (Integer), login, email (nullable), avatar_url
        String providerId = oAuth2User.getAttribute("id").toString();
        String name = oAuth2User.getAttribute("login");
        String avatarUrl = oAuth2User.getAttribute("avatar_url");
        String email = oAuth2User.getAttribute("email");
        if (email == null) email = ""; // GitHub email nullable nếu user set private

        User user = saveOrUpdate(providerId, email, name, avatarUrl);
        return new OAuth2UserPrincipal(user, oAuth2User.getAttributes());
    }

    private User saveOrUpdate(String providerId, String email, String name, String avatarUrl) {
        return userRepository.findByProviderAndProviderId(AuthProvider.GITHUB, providerId)
            .map(existing -> {
                existing.updateProfile(name, avatarUrl);
                return userRepository.save(existing);
            })
            .orElseGet(() -> userRepository.save(
                new User(email, name, avatarUrl, AuthProvider.GITHUB, providerId)
            ));
    }
}
```

### `CustomOidcUserService.java` — xử lý Google

```java
@Service
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;

    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(request);

        // Google OIDC claims: sub (unique user ID), email, name, picture
        String providerId = oidcUser.getSubject();
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();
        String avatarUrl = oidcUser.getPicture();

        User user = saveOrUpdate(providerId, email, name, avatarUrl);
        return new OidcUserPrincipal(user, oidcUser);
    }

    private User saveOrUpdate(String providerId, String email, String name, String avatarUrl) {
        return userRepository.findByProviderAndProviderId(AuthProvider.GOOGLE, providerId)
            .map(existing -> {
                existing.updateProfile(name, avatarUrl);
                return userRepository.save(existing);
            })
            .orElseGet(() -> userRepository.save(
                new User(email, name, avatarUrl, AuthProvider.GOOGLE, providerId)
            ));
    }
}
```

**Logic `saveOrUpdate`:**
- Tìm user theo `(provider, providerId)` trong DB
- Nếu tìm thấy → cập nhật `name` và `avatarUrl` (user có thể đổi avatar trên Google)
- Nếu không tìm thấy → tạo user mới, lưu vào DB

---

## Bước 6 — `OAuth2LoginSuccessHandler`

```java
@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public OAuth2LoginSuccessHandler() {
        setDefaultTargetUrl("/dashboard");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
```

`SavedRequestAwareAuthenticationSuccessHandler` tự động:
1. Kiểm tra session xem user định vào URL nào trước khi bị redirect sang login
2. Redirect về URL đó nếu có → UX tốt
3. Fallback về `/dashboard` nếu không có saved request

Ví dụ: user chưa login cố vào `/profile` → bị redirect sang `/login` → login thành công → redirect về `/profile` (không phải `/dashboard`).

---

## Bước 7 — `SecurityConfig`

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;
    private final OAuth2LoginSuccessHandler successHandler;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                          CustomOidcUserService customOidcUserService,
                          OAuth2LoginSuccessHandler successHandler) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customOidcUserService = customOidcUserService;
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/error", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)   // GitHub
                    .oidcUserService(customOidcUserService) // Google
                )
                .successHandler(successHandler)
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            )
            .sessionManagement(session -> session
                .sessionFixation().newSession()   // tạo session mới sau login
                .maximumSessions(1)               // mỗi user chỉ login 1 nơi
            )
            .build();
    }
}
```

**Các điểm quan trọng:**

- **CSRF bật** (không disable như REST API) — MVC app dùng session + form, cần bảo vệ CSRF
- **Session fixation** — tạo session ID mới sau khi login, tránh attacker steal session ID trước login
- **`maximumSessions(1)`** — session thứ 2 tự hết hạn khi user login từ thiết bị khác
- **`deleteCookies("JSESSIONID")`** — logout xóa cookie session trên browser, không chỉ server-side

---

## Bước 8 — Controllers và Templates

### `HomeController.java`

```java
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) return "redirect:/dashboard";
        return "home";
    }

    @GetMapping("/login")
    public String login(@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) return "redirect:/dashboard";
        return "login";
    }
}
```

### `DashboardController.java`

```java
@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal instanceof UserAware userAware) {
            model.addAttribute("user", userAware.getUser());
        }
        return "dashboard";
    }
}
```

`@AuthenticationPrincipal OAuth2User` — inject principal từ SecurityContext. Cả `OidcUserPrincipal` lẫn `OAuth2UserPrincipal` đều implement `OAuth2User` nên nhận được cả 2. Sau đó cast qua `UserAware` để lấy `User` entity.

### `login.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Sign In</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="min-vh-100 d-flex align-items-center justify-content-center">
    <div class="card shadow-sm" style="width: 420px;">
        <div class="card-body p-5">
            <h4 class="text-center fw-bold mb-4">Sign in</h4>

            <!-- Spring Security tự tạo /oauth2/authorization/{registrationId} -->
            <a href="/oauth2/authorization/google" class="btn btn-outline-dark w-100 mb-3">
                Continue with Google
            </a>
            <a href="/oauth2/authorization/github" class="btn btn-dark w-100">
                Continue with GitHub
            </a>
        </div>
    </div>
</div>
</body>
</html>
```

**`/oauth2/authorization/google`** và **`/oauth2/authorization/github`** là URL Spring Security tự tạo — click vào đây sẽ bắt đầu OAuth2 flow (redirect sang Google/GitHub).

### `dashboard.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
<head>
    <title>Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-dark bg-dark px-4">
    <span class="navbar-brand">OAuth2 Demo</span>
    <div class="ms-auto d-flex gap-3 align-items-center">
        <span class="text-white-50 small" sec:authentication="name">username</span>
        <form th:action="@{/logout}" method="post">
            <button type="submit" class="btn btn-outline-light btn-sm">Sign out</button>
        </form>
    </div>
</nav>

<div class="container py-5">
    <div class="card mx-auto" style="max-width: 500px;">
        <div class="card-body text-center p-4">
            <img th:if="${user.avatarUrl != null}"
                 th:src="${user.avatarUrl}"
                 class="rounded-circle mb-3" width="80" height="80">

            <h5 th:text="${user.name}">Name</h5>
            <p class="text-muted" th:text="${user.email}">email</p>
            <span class="badge bg-secondary" th:text="${user.provider}">PROVIDER</span>

            <hr>

            <div class="text-start small">
                <div class="row mb-1">
                    <div class="col-4 text-muted">Provider ID</div>
                    <div class="col-8" th:text="${user.providerId}"></div>
                </div>
                <div class="row mb-1">
                    <div class="col-4 text-muted">Role</div>
                    <div class="col-8" th:text="${user.role}"></div>
                </div>
                <div class="row">
                    <div class="col-4 text-muted">Joined</div>
                    <div class="col-8" th:text="${#temporals.format(user.createdAt, 'dd/MM/yyyy HH:mm')}"></div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
```

**Lưu ý logout:** Form dùng `method="post"` vì CSRF đang bật — GET logout dễ bị tấn công CSRF (attacker nhúng `<img src="/logout">` vào trang khác là logout user). POST + CSRF token an toàn hơn.

---

## Chạy project

```bash
cd projects/07-spring-security-oauth2-mvc

# Spring tự load application-local.properties vì spring.profiles.active=local
mvn spring-boot:run
```

Truy cập `http://localhost:8081` → click **Continue with Google** hoặc **Continue with GitHub**.

---

## Điểm khác biệt so với project 06 (JWT)

| | Project 06 (JWT) | Project 07 (OAuth2) |
|---|---|---|
| Authentication | Username/password + JWT | OAuth2 provider (Google, GitHub) |
| Session | Stateless (không có session) | Stateful (có session) |
| CSRF | Disable | Enable |
| Password | BCrypt hash lưu DB | Không có |
| User tạo tài khoản | Form đăng ký | Tự động khi login lần đầu |
| Token | JWT access + refresh | Provider access token (Spring quản lý) |

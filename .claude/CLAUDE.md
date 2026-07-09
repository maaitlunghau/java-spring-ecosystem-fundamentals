# Java Spring Boot Fundamentals

Dự án học và luyện tập **Java Spring Framework + Spring Boot** theo kiểu mono-repo — nhiều sub-project Spring Boot độc lập trong cùng một repo, mỗi sub-project tập trung vào một chủ đề.

## Mục tiêu học

- Spring Core: IoC Container, Dependency Injection, Bean lifecycle, AOP
- Spring Boot: Auto-configuration, Starters, `application.yml`
- Spring MVC: REST API, Controllers, Exception handling, Bean Validation
- Spring Data JPA: Repository pattern, Entity mapping, Hibernate, JPQL/Criteria
- Spring Security: Authentication, Authorization, JWT, OAuth2
- Spring Boot Testing: JUnit 5, Mockito, `@SpringBootTest`, `@DataJpaTest`, MockMvc

## Cấu trúc Mono-repo

```
java-spring-boot-fundamentals/
├── .claude/                      # Claude Code config & skills
├── 01-spring-core/               # IoC, DI, Bean lifecycle
├── 02-spring-mvc/                # REST API, Controllers, Validation
├── 03-spring-data-jpa/           # JPA, Hibernate, Repositories
├── 04-spring-security/           # Security, JWT
├── 05-spring-boot-testing/       # Test strategies
└── ...                           # Sub-project mới thêm theo tiến độ
```

Mỗi sub-project là một Spring Boot project độc lập với `pom.xml` riêng và `README.md` ghi chú về chủ đề đang học.

## Tech Stack

| Thành phần | Lựa chọn |
|-----------|---------|
| Java | 21 (LTS) |
| Spring Boot | 3.x (latest stable) |
| Build tool | Maven (mặc định) |
| Database (học) | H2 embedded — không cần setup |
| Database (persist) | PostgreSQL 16 (Docker) |
| Testing | JUnit 5 + Mockito + Spring Test |

## Chạy Sub-project

```bash
cd [sub-project-name]

# Chạy ứng dụng
./mvnw spring-boot:run

# Chạy tất cả tests
./mvnw test

# Chạy một test class cụ thể
./mvnw test -Dtest=UserServiceTest

# Build JAR
./mvnw clean package

# Build, bỏ qua tests
./mvnw clean package -DskipTests
```

## Database Mặc Định — H2 Embedded

Cấu hình mặc định trong `application.yml` cho các sub-project học:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`, User: `sa`)

## PostgreSQL qua Docker (khi cần persistence)

```bash
docker compose up -d    # Start
docker compose down     # Stop (giữ data)
docker compose down -v  # Stop + xóa data
```

## Ghi Chú Học Tập

- Mỗi sub-project có `README.md` ghi: khái niệm đang học, cách chạy, điểm quan trọng
- Dùng `/teach` để tạo bài học có cấu trúc với quiz và reference docs
- Dùng `/grill-me` để tự kiểm tra hiểu biết về một concept
- Dùng `/study-repo` để phân tích Spring Boot repo mẫu trên GitHub
- Dùng `/tdd` khi implement feature để hiểu behavior qua tests

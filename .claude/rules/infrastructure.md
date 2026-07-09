# Infrastructure Rules — Spring Boot Learning

## Database Mặc Định: H2 Embedded

Không cần Docker — nhanh gọn cho learning. Chỉ cần add dependency và config:

```xml
<!-- pom.xml -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

```yaml
# application.yml
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
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop   # Tạo schema khi start, xóa khi stop
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

**H2 Console**: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (để trống)

## PostgreSQL với Docker

Dùng khi cần persistence hoặc muốn gần với môi trường thực tế:

```yaml
# docker-compose.yml (đặt trong thư mục sub-project)
services:
  postgres:
    image: postgres:16
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: springdb
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/springdb
    username: postgres
    password: password
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: update
    show-sql: true
```

```bash
# Trong thư mục sub-project (nơi có docker-compose.yml)
docker compose up -d      # Start containers
docker compose down       # Stop (giữ data)
docker compose down -v    # Stop + xóa volumes (reset data)
docker compose logs -f    # Xem logs
```

## Ports

| Service | Port | URL |
|---------|------|-----|
| Spring Boot App | 8080 | http://localhost:8080 |
| H2 Console | 8080 | http://localhost:8080/h2-console |
| PostgreSQL | 5432 | jdbc:postgresql://localhost:5432/springdb |

## DDL Auto Strategy

| Giá trị | Khi dùng | Hành vi |
|---------|---------|---------|
| `create-drop` | Learning / Test | Tạo schema lúc start, xóa lúc stop |
| `create` | Dev mới bắt đầu | Tạo schema lúc start, giữ lại data |
| `update` | Dev ongoing | Chỉ ALTER TABLE để thêm column mới |
| `validate` | Production | Chỉ kiểm tra schema khớp entity, không sửa |
| `none` | Production với Flyway | Không làm gì với schema |

## Database Migration (Flyway — khi học advanced)

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

Migration files đặt tại: `src/main/resources/db/migration/`

```
V1__Create_users_table.sql
V2__Add_email_column_to_users.sql
V3__Create_orders_table.sql
```

Khi dùng Flyway, set `ddl-auto: validate` hoặc `none`.

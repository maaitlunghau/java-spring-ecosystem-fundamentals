# Infrastructure Rules — Spring Boot Learning

## Database Mặc Định: MySQL

Các sub-project chủ yếu dùng **MySQL 8** (chạy qua Docker), gần với môi trường thực tế.

```xml
<!-- pom.xml -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

```yaml
# docker-compose.yml (đặt trong thư mục sub-project)
services:
  mysql:
    image: mysql:8
    ports:
      - "3306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: 112233
      MYSQL_DATABASE: springdb
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

```properties
# application.properties
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:3306/springdb?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=112233
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false
```

```bash
# Trong thư mục sub-project (nơi có docker-compose.yml)
docker compose up -d      # Start containers
docker compose down       # Stop (giữ data)
docker compose down -v    # Stop + xóa volumes (reset data)
docker compose logs -f    # Xem logs
```

## H2 (tùy chọn — in-memory, quick test)

Khi muốn chạy thử nhanh, zero-setup (không cần Docker/cài DB) — vd project học thuần logic:

```xml
<!-- pom.xml -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

```properties
# application.properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

**H2 Console**: `http://localhost:8081/h2-console` — JDBC URL `jdbc:h2:mem:testdb`, User `sa`, Password để trống.

## Database khác (khi cần)

| Database | Dependency | Ghi chú |
|---------|-----------|---------|
| SQL Server | `com.microsoft.sqlserver:mssql-jdbc` (scope: runtime) | Driver JDBC cho MS SQL Server |
| MongoDB | `spring-boot-starter-data-mongodb` | NoSQL — dùng Spring Data MongoDB (không phải JPA) |

## Ports

| Service | Port | URL |
|---------|------|-----|
| Spring Boot App | 8081 | http://localhost:8081 |
| H2 Console | 8081 | http://localhost:8081/h2-console |
| MySQL | 3306 | jdbc:mysql://localhost:3306/springdb |

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

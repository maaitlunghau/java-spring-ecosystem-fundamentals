# Workflow Rules — Java Spring Boot Learning

## Thêm Sub-project Mới

1. Tạo thư mục với tên theo pattern: `[số]-[topic-name]` (ví dụ: `03-spring-data-jpa`)
2. Init Spring Boot project từ [start.spring.io](https://start.spring.io) hoặc IntelliJ IDEA
3. Tạo `README.md` trong sub-project ghi: mục tiêu học, cách chạy, key concepts

## Maven Commands (trong thư mục sub-project)

```bash
# Chạy ứng dụng
./mvnw spring-boot:run

# Chạy tất cả tests
./mvnw test

# Chạy một test class cụ thể
./mvnw test -Dtest=UserServiceTest

# Chạy một test method cụ thể
./mvnw test -Dtest=UserServiceTest#should_return_user_when_id_exists

# Build JAR
./mvnw clean package

# Build, bỏ qua tests
./mvnw clean package -DskipTests

# Xem dependency tree
./mvnw dependency:tree

# Reload dependencies
./mvnw dependency:resolve
```

## Testing Flow (TDD)

Ưu tiên viết test trước khi implement:

```
1. RED   — Viết test mô tả behavior mong muốn → chạy → fail
2. GREEN — Implement code tối thiểu để test pass → chạy → pass
3. REFACTOR — Dọn code, chạy lại tests
```

Dùng skill `/tdd` để được hướng dẫn từng bước.

## Git Conventions

```bash
# Branch cho từng chủ đề học
git checkout -b topic/spring-data-jpa

# Commit nhỏ, mỗi concept một commit
git commit -m "feat: add User entity with JPA mapping"
git commit -m "feat: implement UserRepository with custom queries"
git commit -m "test: add UserService unit tests"
git commit -m "docs: update README with JPA key concepts"
```

Commit types: `feat`, `fix`, `test`, `docs`, `chore`, `refactor`

## Thêm Dependency vào pom.xml

```xml
<dependencies>
    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- H2 Database (test/dev) -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

Sau khi thêm: để Maven reload hoặc chạy `./mvnw dependency:resolve`.

## application.yml Mẫu

```yaml
spring:
  application:
    name: [topic-name]

server:
  port: 8080

logging:
  level:
    org.hibernate.SQL: DEBUG              # Xem SQL queries
    org.hibernate.orm.jdbc.bind: TRACE    # Xem bind parameters (Spring Boot 3.x trở lên)
    com.example: DEBUG                    # Xem log của code mình
```

## Khi Gặp Lỗi

1. Đọc stack trace từ dưới lên — root cause thường ở cuối
2. Dùng skill `/diagnosing-bugs` cho bug phức tạp
3. Common Spring Boot errors:
   - `NoSuchBeanDefinitionException` → thiếu annotation (`@Service`, `@Repository`, etc.) hoặc component scan không thấy
   - `BeanCreationException` → lỗi trong constructor của Bean, xem inner exception
   - `LazyInitializationException` → truy cập lazy relation ngoài transaction
   - `DataIntegrityViolationException` → vi phạm constraint DB (unique, not null, FK)

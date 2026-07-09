# Tech Defaults — Spring Boot

Khi thêm tính năng mới vào bất kỳ sub-project nào, dùng các thư viện sau. Không thêm alternative mà không thảo luận trước.

## Spring Boot Starters

| Concern | Dependency | Ghi chú |
|---------|-----------|---------|
| Web / REST API | `spring-boot-starter-web` | Jackson tự động, embedded Tomcat |
| JPA / Hibernate | `spring-boot-starter-data-jpa` | Spring Data JPA + Hibernate |
| Security | `spring-boot-starter-security` | Auth & Authorization |
| Validation | `spring-boot-starter-validation` | Bean Validation (Jakarta) |
| Testing | `spring-boot-starter-test` | JUnit 5 + Mockito + Spring Test |
| Actuator | `spring-boot-starter-actuator` | Health check, metrics |
| DevTools | `spring-boot-devtools` | Hot reload, H2 console tự enable |

## Database

| Môi trường | Dependency | Ghi chú |
|-----------|-----------|---------|
| Learning / Dev | `com.h2database:h2` (scope: runtime) | In-memory, không cần cài đặt |
| Production-like | `org.postgresql:postgresql` (scope: runtime) | Dùng kèm Docker |
| MySQL (nếu cần) | `com.mysql:mysql-connector-j` (scope: runtime) | |

## Versions Mặc Định

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.x</version>  <!-- latest stable -->
</parent>

<properties>
    <java.version>21</java.version>
</properties>
```

- **Java**: 21 (LTS)
- **Spring Boot**: 3.x (parent POM quản lý version của tất cả dependency)
- **Build tool**: Maven với Maven Wrapper (`./mvnw`) — không cần cài Maven global

## Lombok (Optional — dùng thận trọng)

Dùng để giảm boilerplate, nhưng học Java thuần trước:

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

- **Dùng**: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`, `@Slf4j`
- **TRÁNH `@Data` trên Entity**: gây vấn đề với JPA lazy loading và `equals`/`hashCode`
- **Ưu tiên Records**: cho DTO thuần túy thay vì Lombok

## Testing Stack

| Loại test | Annotation | Scope |
|-----------|-----------|-------|
| Unit test (service) | `@ExtendWith(MockitoExtension.class)` | Mock dependencies |
| Slice test (JPA) | `@DataJpaTest` | Chỉ load JPA layer |
| Slice test (Web) | `@WebMvcTest(Controller.class)` | Chỉ load Web layer |
| Integration test | `@SpringBootTest` | Load full context |
| Integration + HTTP | `@SpringBootTest + TestRestTemplate` | Chạy server thật |

## Không Dùng (trong learning project này)

- **MapStruct / ModelMapper**: map DTO thủ công để học rõ hơn
- **QueryDSL**: dùng JPQL `@Query` trước
- **Spring WebFlux / Reactive**: học Servlet (blocking) trước

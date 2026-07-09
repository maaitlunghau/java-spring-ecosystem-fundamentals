# Coding Standards — Java Spring Boot

## Naming Conventions

### Classes & Files

| Loại | Convention | Ví dụ |
|------|-----------|-------|
| Controller | `PascalCase + Controller` | `UserController.java` |
| Service | `PascalCase + Service` | `UserService.java` |
| Service impl | `PascalCase + ServiceImpl` | `UserServiceImpl.java` |
| Repository | `PascalCase + Repository` | `UserRepository.java` |
| Entity | `PascalCase` (tên domain) | `User.java`, `Order.java` |
| DTO (response) | `PascalCase + Dto` hoặc `Response` | `UserDto.java`, `UserResponse.java` |
| DTO (request) | `PascalCase + Request` | `CreateUserRequest.java` |
| Exception | `PascalCase + Exception` | `ResourceNotFoundException.java` |
| Config | `PascalCase + Config` | `SecurityConfig.java` |
| Test | `[ClassName] + Test` | `UserServiceTest.java` |
| Integration Test | `[ClassName] + IT` | `UserControllerIT.java` |

### Code

- **Classes**: `PascalCase` — `UserService`, `OrderController`
- **Methods & variables**: `camelCase` — `findById()`, `userRepository`
- **Constants**: `UPPER_SNAKE_CASE` — `MAX_RETRY_COUNT`, `DEFAULT_PAGE_SIZE`
- **Packages**: `lowercase.dots` — `com.example.springmvc.controller`
- **Enums**: class `PascalCase`, values `UPPER_SNAKE_CASE`

```java
public enum UserRole {
    ADMIN,
    USER,
    MODERATOR
}
```

## Java Patterns

### Constructor Injection (bắt buộc dùng)

```java
// ✅ ĐÚNG — constructor injection: tường minh, dễ test, immutable dependency
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}

// ❌ TRÁNH — field injection: khó test, che giấu dependency
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
}
```

### Java Records cho DTO (Java 16+)

```java
// Concise, immutable — dùng cho DTO
public record UserDto(Long id, String name, String email) {
    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}

public record CreateUserRequest(
    @NotBlank(message = "Name is required") String name,
    @Email @NotBlank(message = "Valid email is required") String email
) {}
```

### Optional — tránh return null

```java
// ✅ ĐÚNG — trả về Optional, để caller quyết định
public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
}

// ✅ ĐÚNG — throw exception khi không tìm thấy
public UserDto findById(Long id) {
    return userRepository.findById(id)
        .map(UserDto::from)
        .orElseThrow(() -> new ResourceNotFoundException("User", id));
}

// ❌ TRÁNH — return null
public User findByEmail(String email) {
    return userRepository.findByEmail(email).orElse(null);
}
```

### @Transactional

```java
// Đặt readOnly = true ở class level, override cho write operations
@Service
@Transactional(readOnly = true)
public class UserService {

    public UserDto findById(Long id) { ... }      // readOnly = true (thừa kế)

    @Transactional                                  // readOnly = false (override)
    public UserDto create(CreateUserRequest req) { ... }

    @Transactional
    public void delete(Long id) { ... }
}
```

## Import Order

IntelliJ IDEA tự sắp xếp. Thứ tự chuẩn:

```java
// 1. java.* và javax.*
import java.util.List;
import java.util.Optional;

// 2. Third-party libs
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 3. Internal project packages
import com.example.myapp.entity.User;
import com.example.myapp.repository.UserRepository;
```

## Formatting

- Indent: **4 spaces** (Java standard — không dùng tabs)
- Max line length: 120 chars
- Braces: **K&R style** (mở ngoặc cùng dòng)
- Blank line giữa các method
- Dùng IntelliJ IDEA built-in formatter hoặc `google-java-format`

## Comments

- Chỉ comment khi **WHY** không rõ từ code
- Không comment "what" — tên biến/method đã nói lên điều đó
- Javadoc cho public API trong shared library (không bắt buộc trong learning project)

```java
// ✅ Comment WHY — giải thích lý do không hiển nhiên
// H2 requires DB_CLOSE_DELAY=-1 để giữ connection pool không đóng DB sớm
String url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";

// ❌ Comment WHAT — thừa
// Tìm user theo id
return userRepository.findById(id);
```

## Test Conventions

```java
// Tên test: should_[expected]_when_[condition]
@Test
void should_return_user_when_id_exists() { ... }

@Test
void should_throw_exception_when_user_not_found() { ... }

// Cấu trúc: Arrange - Act - Assert
@Test
void should_save_user_successfully() {
    // Arrange
    CreateUserRequest request = new CreateUserRequest("Alice", "alice@example.com");

    // Act
    UserDto result = userService.create(request);

    // Assert
    assertThat(result.name()).isEqualTo("Alice");
    assertThat(result.id()).isNotNull();
}
```

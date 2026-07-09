# Architecture Rules — Spring Boot

## Layered Architecture (Standard)

```
HTTP Request
    ↓
@RestController      ← Nhận request, trả response, không có business logic
    ↓
@Service             ← Business logic, transaction management
    ↓
@Repository          ← Data access (Spring Data JPA / JdbcTemplate)
    ↓
Database
```

## Layer Responsibilities

### Controller (`controller/`)

- Annotate với `@RestController` + `@RequestMapping`
- Chỉ nhận request, extract params, gọi Service, trả response — không có logic
- Trả về `ResponseEntity<T>` hoặc DTO trực tiếp
- **Constructor injection** — không dùng `@Autowired` field injection

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDto created = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
```

### Service (`service/`)

- Annotate với `@Service`
- Chứa toàn bộ business logic
- Quản lý transaction: `@Transactional` ở class level, override method nếu cần
- Throw custom exceptions khi không tìm thấy hoặc vi phạm business rule

```java
@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto findById(Long id) {
        return userRepository.findById(id)
            .map(UserDto::from)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Transactional
    public UserDto create(CreateUserRequest request) {
        User user = new User(request.name(), request.email());
        return UserDto.from(userRepository.save(user));
    }
}
```

### Repository (`repository/`)

- Extend `JpaRepository<Entity, ID>` — Spring Data tự implement
- Annotate `@Repository` optional (Spring Data tự detect)
- Chỉ chứa data access — không có business logic
- Custom queries dùng derived method names hoặc `@Query` JPQL

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByActiveTrue();

    @Query("SELECT u FROM User u WHERE u.name LIKE %:keyword%")
    List<User> searchByName(@Param("keyword") String keyword);
}
```

### Entity (`entity/` hoặc `domain/`)

- Annotate với `@Entity` + `@Table(name = "users")`
- `@Id` + `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- Tránh đặt logic phức tạp trong entity, giữ đơn giản
- Không expose Entity ra ngoài API — dùng DTO

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    // constructors, getters, setters
}
```

### DTO (`dto/`)

- Plain Java objects (hoặc Records) — không có JPA annotation
- Dùng Java Records cho DTO đơn giản (immutable, boilerplate-free)
- Static factory method `from(Entity)` để convert

```java
public record UserDto(Long id, String name, String email) {
    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}

public record CreateUserRequest(
    @NotBlank String name,
    @Email @NotBlank String email
) {}
```

## Exception Handling

Custom exception hierarchy:

```java
// Base
public class AppException extends RuntimeException {
    public AppException(String message) { super(message); }
}

// Specific
public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " not found with id: " + id);
    }
}
```

Global handler với `@RestControllerAdvice`:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(404, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(new ErrorResponse(400, message));
    }
}
```

## Package Structure (per sub-project)

```
src/main/java/com/example/[topic]/
├── [TopicApplication].java     ← Main class
├── controller/
├── service/
├── repository/
├── entity/                     (hoặc domain/ hoặc model/)
├── dto/
├── exception/
└── config/
```

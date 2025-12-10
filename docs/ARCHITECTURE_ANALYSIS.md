# 🏗️ UniCovoit - Complete Architecture Analysis

**Project**: UniCovoit - Student Carpooling Platform
**Technology Stack**: Spring Boot 3.4.12 + Vaadin 24.9.6 + MySQL 8
**Architecture Pattern**: Layered N-Tier Architecture
**Total Classes**: 50 Java files

---

## 📊 Executive Summary

UniCovoit follows a **clean, layered architecture** based on industry best practices:
- **Separation of Concerns**: Each layer has a single, well-defined responsibility
- **Dependency Inversion**: Higher layers depend on abstractions, not implementations
- **SOLID Principles**: Applied throughout the codebase
- **MVC Pattern**: Model-View-Controller separation with Vaadin Flow

---

## 🎯 Architecture Overview Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                         PRESENTATION LAYER                           │
│                          (Vaadin Views)                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐             │
│  │ LoginView    │  │ SearchView   │  │ MessagesView │  ... 16 views│
│  │ RegisterView │  │ CreateRide   │  │ VehicleForm  │             │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘             │
│         │                  │                  │                      │
└─────────┼──────────────────┼──────────────────┼──────────────────────┘
          │                  │                  │
          ▼                  ▼                  ▼
┌─────────────────────────────────────────────────────────────────────┐
│                           SERVICE LAYER                              │
│                        (Business Logic)                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐             │
│  │ UserService  │  │ RideService  │  │ MessageSvc   │  ... 5 svc   │
│  │ @Service     │  │ @Service     │  │ @Service     │             │
│  │ @Validated   │  │ @Transactional│ │ @Transactional│            │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘             │
│         │                  │                  │                      │
└─────────┼──────────────────┼──────────────────┼──────────────────────┘
          │                  │                  │
          ▼                  ▼                  ▼
┌─────────────────────────────────────────────────────────────────────┐
│                       DATA ACCESS LAYER                              │
│                    (Spring Data Repositories)                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐             │
│  │UserAccountDao│  │  RideDao     │  │ MessageDao   │  ... 5 DAOs  │
│  │@Repository   │  │ @Repository  │  │ @Repository  │             │
│  │extends JPA..│  │ + JPQL       │  │ + JPQL       │             │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘             │
│         │                  │                  │                      │
└─────────┼──────────────────┼──────────────────┼──────────────────────┘
          │                  │                  │
          ▼                  ▼                  ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         PERSISTENCE LAYER                            │
│                       (JPA/Hibernate + MySQL)                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐             │
│  │ UserAccount  │  │    Ride      │  │   Message    │  ... 6 entities│
│  │   @Entity    │  │   @Entity    │  │   @Entity    │             │
│  │  + Relations │  │ + Validation │  │ + Indexes    │             │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘             │
│         │                  │                  │                      │
└─────────┼──────────────────┼──────────────────┼──────────────────────┘
          │                  │                  │
          ▼                  ▼                  ▼
     ┌──────────────────────────────────────────────┐
     │            MySQL Database                     │
     │   user_account | vehicle | ride | booking    │
     │        message | password_reset_token         │
     └──────────────────────────────────────────────┘

     CROSS-CUTTING CONCERNS (Support All Layers)
     ┌────────────────────────────────────────────────────┐
     │ Exception Handling │ DTOs │ Utilities │ Config    │
     └────────────────────────────────────────────────────┘
```

---

## 🔍 Detailed Layer Analysis

### **LAYER 1: PRESENTATION (Views) - 16 Classes**

**Purpose**: User interface and user interaction handling

**Technology**: Vaadin Flow 24.9.6 (Server-side Java UI framework)

**Structure**:
```
views/
├── auth/          LoginView, RegisterView
├── home/          HomeView
├── layout/        MainLayout (with drawer navigation)
├── ride/          SearchView, CreateRideView, MyRidesView, RideDetailView
├── vehicle/       VehicleListView, VehicleFormView
├── booking/       MyBookingsView, BookingRequestsView
└── message/       MessagesView, ConversationView
```

**Responsibilities**:
- ✅ Render UI components (buttons, forms, grids)
- ✅ Handle user input (clicks, form submissions)
- ✅ Validate client-side data
- ✅ Display notifications
- ✅ Route navigation
- ✅ Session management (check authentication)

**Key Features**:
- Card-based responsive design
- Real-time server-side rendering
- Session-based authentication guards
- Lumo theme customization
- Route parameters support (`/rides/:id`)

**Example Flow (LoginView)**:
```
1. User enters email + password
2. View validates fields (not empty)
3. Creates LoginRequestDto
4. Calls UserService.authenticate()
5. On success: Store user in session via SessionManager
6. Navigate to home page
7. On error: Display error notification
```

**Why This Approach?**
- ✅ Server-side rendering = Better security (no exposed business logic)
- ✅ Vaadin = Type-safe UI in pure Java (no JavaScript needed)
- ✅ Component-based = Reusable UI elements
- ✅ Built-in CSRF protection

---

### **LAYER 2: SERVICE (Business Logic) - 5 Classes**

**Purpose**: Implement business rules and orchestrate operations

**Technology**: Spring Framework @Service beans

**Classes**:
```
service/
├── UserService.java           (Authentication, registration)
├── VehicleService.java        (CRUD + ownership validation)
├── RideService.java           (CRUD + search + status management)
├── BookingService.java        (Booking workflow + seat management)
└── MessageService.java        (Messaging + conversation management)
```

**Responsibilities**:
- ✅ Business rule enforcement
- ✅ Transaction management (@Transactional)
- ✅ Input validation (@Valid, @Validated)
- ✅ Cross-entity operations
- ✅ Exception handling (throw custom exceptions)
- ✅ Security checks (ownership verification)

**Key Patterns**:
```java
@Service                    // Spring component
@Validated                  // Enable method-level validation
public class RideService {

    @Transactional          // Automatic rollback on exception
    public Ride createRide(@Valid CreateRideDto dto, UserAccount driver) {
        // 1. Validate business rules
        if (dto.getDepartureDateTime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Date must be in future");
        }

        // 2. Load related entities
        Vehicle vehicle = vehicleDao.findById(dto.getVehicleId())
            .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));

        // 3. Check ownership
        if (!vehicle.getOwner().getId().equals(driver.getId())) {
            throw new ValidationException("Not your vehicle");
        }

        // 4. Create and persist
        Ride ride = new Ride();
        // ... set properties
        return rideDao.save(ride);
    }
}
```

**Transaction Management**:
- **@Transactional(readOnly = true)**: For queries (optimization)
- **@Transactional**: For modifications (automatic commit/rollback)

**Example Business Rule (BookingService)**:
```java
// Cannot book your own ride
if (ride.getDriver().getId().equals(passenger.getId())) {
    throw new ValidationException("Cannot book your own ride");
}

// Cannot book twice
if (bookingDao.existsByPassengerIdAndRideId(passengerId, rideId)) {
    throw new BusinessException("Already booked this ride");
}

// Check seat availability
if (ride.getSeatsAvailable() < dto.getSeatsBooked()) {
    throw new BusinessException("Not enough seats");
}

// Update seat count atomically
ride.setSeatsAvailable(ride.getSeatsAvailable() - dto.getSeatsBooked());
```

**Why This Approach?**
- ✅ **Centralized business logic**: Easy to test and maintain
- ✅ **Transaction boundaries**: Data consistency guaranteed
- ✅ **Reusable**: Multiple views can use same service methods
- ✅ **Testable**: Can mock DAOs for unit tests

---

### **LAYER 3: DATA ACCESS (Repositories) - 5 Classes**

**Purpose**: Abstract database operations

**Technology**: Spring Data JPA (interfaces, no implementation needed)

**Classes**:
```
dao/
├── UserAccountDao.java        (Find by email, check existence)
├── VehicleDao.java            (Find by owner)
├── RideDao.java               (Search rides with JPQL)
├── BookingDao.java            (Complex booking queries)
└── MessageDao.java            (Conversation queries, unread count)
```

**Responsibilities**:
- ✅ CRUD operations (provided by JpaRepository)
- ✅ Custom queries (JPQL, derived methods)
- ✅ Query optimization
- ✅ Data retrieval

**Query Types**:

**1. Derived Query Methods** (Spring generates SQL automatically):
```java
List<Vehicle> findByOwnerId(Long ownerId);
boolean existsByEmail(String email);
List<Booking> findByPassengerId(Long passengerId);
```

**2. JPQL Queries** (Custom complex queries):
```java
@Query("""
    SELECT r
    FROM Ride r
    WHERE LOWER(r.departureCity) LIKE LOWER(CONCAT('%', :dep, '%'))
      AND LOWER(r.arrivalCity) LIKE LOWER(CONCAT('%', :arr, '%'))
      AND r.departureDateTime BETWEEN :startDateTime AND :endDateTime
    """)
List<Ride> findRides(
    @Param("dep") String departureCity,
    @Param("arr") String arrivalCity,
    @Param("startDateTime") LocalDateTime start,
    @Param("endDateTime") LocalDateTime end
);
```

**3. Native SQL** (Not used in this project - JPQL is preferred for portability)

**Example Complex Query (MessageDao)**:
```java
@Query("""
    SELECT m
    FROM Message m
    WHERE (m.sender.id = :user1Id AND m.receiver.id = :user2Id)
       OR (m.sender.id = :user2Id AND m.receiver.id = :user1Id)
    ORDER BY m.sentAt ASC
    """)
List<Message> findConversation(
    @Param("user1Id") Long user1Id,
    @Param("user2Id") Long user2Id
);
```

**Why This Approach?**
- ✅ **No boilerplate code**: Spring generates implementations
- ✅ **Type-safe**: Compile-time checking
- ✅ **Database agnostic**: JPQL works on any SQL database
- ✅ **Performance**: Can optimize specific queries
- ✅ **Maintainable**: Queries in one place, not scattered in code

---

### **LAYER 4: PERSISTENCE (Domain Model) - 6 Entities**

**Purpose**: Represent database structure in Java

**Technology**: JPA/Hibernate with Bean Validation

**Entities**:
```
entity/
├── UserAccount.java           (Users - students and admins)
├── Vehicle.java               (User-owned vehicles)
├── Ride.java                  (Carpooling rides)
├── Booking.java               (Ride reservations)
├── Message.java               (User-to-user messages)
└── PasswordResetToken.java    (Password reset functionality)

Enums:
├── Role.java                  (STUDENT, ADMIN)
├── RideStatus.java            (PUBLISHED, CANCELLED, COMPLETED)
└── BookingStatus.java         (PENDING, CONFIRMED, CANCELLED_BY_*)
```

**Entity Relationships**:
```
UserAccount 1 ──< N Vehicle
UserAccount 1 ──< N Ride (as driver)
UserAccount 1 ──< N Booking (as passenger)
UserAccount 1 ──< N Message (as sender/receiver)
Vehicle 1 ──< N Ride
Ride 1 ──< N Booking
Ride 1 ──< N Message (optional reference)
```

**Key Features**:

**1. Validation Annotations**:
```java
@Entity
@Table(name = "user_account")
public class UserAccount {

    @NotBlank(message = "Email required")
    @Email(message = "Email must be valid")
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password required")
    @Size(min = 8, message = "Min 8 characters")
    @Column(name = "password_hash")
    private String passwordHash;
}
```

**2. Lifecycle Callbacks**:
```java
@PrePersist
public void prePersist() {
    createdAt = LocalDateTime.now();
}

@PreUpdate
public void preUpdate() {
    updatedAt = LocalDateTime.now();
}
```

**3. Relationships**:
```java
// Bidirectional One-to-Many
@OneToMany(mappedBy = "owner")
private List<Vehicle> vehicles = new ArrayList<>();

// Many-to-One with join column
@ManyToOne(optional = false)
@JoinColumn(name = "driver_id", nullable = false)
private UserAccount driver;
```

**Why This Approach?**
- ✅ **Object-Relational Mapping**: Work with objects, not SQL
- ✅ **Validation at entity level**: Data integrity ensured
- ✅ **Lazy loading**: Performance optimization
- ✅ **Automatic schema generation**: Can generate DDL from entities

---

## 🛡️ Cross-Cutting Concerns

### **1. Exception Handling (5 Classes)**

**Hierarchy**:
```
UniCovoitException (base)
    ├── ValidationException         (400 Bad Request)
    ├── AuthenticationException     (401 Unauthorized)
    ├── ResourceNotFoundException   (404 Not Found)
    └── BusinessException          (422 Unprocessable Entity)
```

**Usage**:
```java
// Service layer throws
throw new ResourceNotFoundException("Ride", rideId);

// View layer catches
try {
    rideService.getRideById(id);
} catch (ResourceNotFoundException ex) {
    NotificationHelper.showError(ex.getMessage());
    navigateToSearch();
}
```

**Why?**
- ✅ Consistent error handling across application
- ✅ Meaningful error messages to users
- ✅ Type-safe exception handling

---

### **2. Data Transfer Objects (DTOs) - 7 Classes**

**Purpose**: Transfer data between layers with validation

**Classes**:
```
dto/
├── LoginRequestDto
├── RegisterRequestDto
├── VehicleDto
├── CreateRideDto
├── RideSearchRequestDto
├── CreateBookingDto
└── SendMessageDto
```

**Example**:
```java
public class CreateRideDto {

    @NotNull(message = "Vehicle required")
    private Long vehicleId;

    @NotBlank(message = "Departure city required")
    @Size(max = 150)
    private String departureCity;

    @NotNull(message = "Date required")
    private LocalDateTime departureDateTime;

    @DecimalMin("0.0")
    @DecimalMax("999.99")
    private BigDecimal pricePerSeat;

    // Getters and setters
}
```

**Why DTOs?**
- ✅ **Separation**: Don't expose entities to presentation layer
- ✅ **Validation**: Input validation at entry point
- ✅ **Flexibility**: DTO structure can differ from entity
- ✅ **Security**: Control what data is sent/received

---

### **3. Utilities (2 Classes)**

**SessionManager**:
```java
public class SessionManager {
    public static void setCurrentUser(UserAccount user);
    public static UserAccount getCurrentUser();
    public static boolean isLoggedIn();
    public static void logout();
    public static Long getCurrentUserId();
    public static String getCurrentUserFullName();
}
```

**NotificationHelper**:
```java
public class NotificationHelper {
    public static void showSuccess(String message);
    public static void showError(String message);
    public static void showWarning(String message);
    public static void showInfo(String message);
}
```

**Why?**
- ✅ **DRY principle**: Don't repeat yourself
- ✅ **Consistency**: Same behavior everywhere
- ✅ **Maintainability**: Change once, affects all usages

---

### **4. Configuration (1 Class)**

**SecurityConfig**:
```java
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**Why?**
- ✅ Centralized configuration
- ✅ Dependency injection
- ✅ Easy to modify

---

## 🔄 Complete Request Flow Example

### **Scenario: User Books a Ride**

```
1. USER ACTION
   │
   ├─→ User clicks "Réserver" button on RideDetailView
   │
   ▼

2. PRESENTATION LAYER (RideDetailView.java)
   │
   ├─→ Validate: User must be logged in
   ├─→ Validate: Not booking own ride
   ├─→ Validate: Seats requested > 0
   ├─→ Create CreateBookingDto with rideId, seatsBooked
   │
   ▼

3. SERVICE LAYER (BookingService.java)
   │
   ├─→ @Transactional begins
   ├─→ Validate DTO with @Valid
   ├─→ Load Ride entity from RideDao
   ├─→ Business rule: Check ride status = PUBLISHED
   ├─→ Business rule: Check driver != passenger
   ├─→ Business rule: Check no duplicate booking
   ├─→ Business rule: Check seats available >= requested
   ├─→ Create Booking entity
   ├─→ Update Ride.seatsAvailable (decrement)
   ├─→ Save both entities
   ├─→ @Transactional commits (or rollback on exception)
   │
   ▼

4. DATA ACCESS LAYER (BookingDao + RideDao)
   │
   ├─→ Spring Data JPA: bookingDao.save(booking)
   ├─→ Spring Data JPA: rideDao.save(ride)
   │
   ▼

5. PERSISTENCE LAYER (Hibernate)
   │
   ├─→ Generate SQL INSERT for booking
   ├─→ Generate SQL UPDATE for ride.seats_available
   ├─→ Execute in single transaction
   │
   ▼

6. DATABASE (MySQL)
   │
   ├─→ BEGIN TRANSACTION
   ├─→ INSERT INTO booking (ride_id, passenger_id, seats_booked, status)
   ├─→ UPDATE ride SET seats_available = seats_available - 1 WHERE id = ?
   ├─→ COMMIT
   │
   ▼

7. RETURN TO USER
   │
   ├─→ Service returns Booking entity
   ├─→ View shows success notification
   ├─→ View refreshes ride details
   └─→ User sees confirmation
```

---

## 🎯 Architectural Decisions & Justifications

### **1. Why Layered Architecture?**

**Decision**: Use strict layer separation (Views → Services → DAOs → Entities)

**Justification**:
- ✅ **Maintainability**: Each layer can be modified independently
- ✅ **Testability**: Can test each layer in isolation
- ✅ **Team scaling**: Different developers can work on different layers
- ✅ **Technology replacement**: Can swap Vaadin for another UI framework without touching business logic

**Alternative Rejected**: Monolithic architecture with logic in views
- ❌ Difficult to test
- ❌ Logic duplication across views
- ❌ Hard to maintain

---

### **2. Why Spring Data JPA (Not Native JDBC)?**

**Decision**: Use Spring Data JPA for data access

**Justification**:
- ✅ **Productivity**: Auto-generated queries
- ✅ **Database portability**: JPQL works on MySQL, PostgreSQL, Oracle, etc.
- ✅ **Object-oriented**: Work with objects, not result sets
- ✅ **Transaction management**: Automatic handling
- ✅ **Caching**: Built-in second-level cache support

**Alternative Rejected**: Native JDBC
- ❌ Boilerplate code (connection, prepared statements, result set mapping)
- ❌ Error-prone (SQL injection risks, connection leaks)
- ❌ Database-specific SQL

---

### **3. Why Vaadin (Not React/Angular)?**

**Decision**: Use Vaadin Flow for frontend

**Justification**:
- ✅ **Type safety**: Pure Java, compile-time checking
- ✅ **Security**: Business logic on server, not exposed to client
- ✅ **Productivity**: No need to learn JavaScript/TypeScript
- ✅ **Consistency**: Same language for frontend and backend
- ✅ **Built-in components**: Rich component library

**Trade-offs**:
- ⚠️ Server resources: More server memory per session
- ⚠️ Scalability: Requires sticky sessions for horizontal scaling

---

### **4. Why DTOs?**

**Decision**: Use separate DTOs instead of exposing entities

**Justification**:
- ✅ **Security**: Don't expose internal entity structure
- ✅ **Validation**: Input validation at API boundary
- ✅ **Flexibility**: DTO can differ from entity (e.g., flatten nested objects)
- ✅ **Backward compatibility**: Can change entity without breaking API

**Example**: CreateRideDto contains `vehicleId` (Long), but Ride entity has `vehicle` (Vehicle object)

---

### **5. Why Custom Exception Hierarchy?**

**Decision**: Create domain-specific exceptions

**Justification**:
- ✅ **Clarity**: Exception name tells what went wrong
- ✅ **Handling**: Can catch specific exceptions
- ✅ **User experience**: Map exceptions to user-friendly messages

**Alternative Rejected**: Generic exceptions
- ❌ `throw new RuntimeException("Invalid ride")` - Not descriptive
- ❌ Difficult to handle differently based on error type

---

### **6. Why Session-Based Auth (Not JWT)?**

**Decision**: Use Vaadin's session management

**Justification**:
- ✅ **Simplicity**: Built into Vaadin
- ✅ **Stateful**: Vaadin is inherently stateful
- ✅ **Server-side**: More secure (no token exposed to client)
- ✅ **Easy invalidation**: Just invalidate session

**Trade-off**:
- ⚠️ Sticky sessions needed for load balancing

---

## 📊 Architecture Metrics

| Metric | Value | Industry Standard |
|--------|-------|-------------------|
| **Layers** | 4 main + 3 cross-cutting | ✅ Good |
| **Classes** | 50 | ✅ Manageable |
| **Avg Methods/Class** | ~8 | ✅ Low complexity |
| **Dependencies** | 4 main (Spring, Vaadin, MySQL, Validation) | ✅ Minimal |
| **Coupling** | Loose (via interfaces) | ✅ Best practice |
| **Cohesion** | High (single responsibility) | ✅ Best practice |

---

## 🎓 For Your Presentation

### **Key Points to Emphasize**

1. **Clear Separation**: Each layer has ONE job
   - Views = User interface
   - Services = Business logic
   - DAOs = Data access
   - Entities = Data structure

2. **Industry Standards**: Following Spring Boot best practices
   - @Service, @Repository, @Entity annotations
   - Transaction management
   - Dependency injection

3. **Data Integrity**: Multiple validation layers
   - Client-side (Vaadin field validation)
   - DTO validation (@Valid)
   - Business rules (Service layer)
   - Database constraints (Entity annotations)

4. **Security by Design**:
   - Server-side rendering (no exposed business logic)
   - BCrypt password hashing
   - Ownership verification in services
   - Session-based authentication

5. **Maintainability**:
   - Easy to add new features (new view + service method)
   - Easy to test (mock dependencies)
   - Easy to understand (clear package structure)

6. **Performance Optimization**:
   - @Transactional(readOnly = true) for queries
   - Proper indexing in database
   - Lazy loading for relationships

---

## 📈 Evolution & Scalability

### **Current State**: MVP (Minimum Viable Product)
- ✅ Core features implemented
- ✅ Clean architecture
- ✅ Ready for production

### **Future Enhancements** (Architecture supports):
- 🔄 **REST API**: Add @RestController layer without touching service layer
- 🔄 **Mobile App**: Service layer can be reused
- 🔄 **Microservices**: Can split services into separate deployments
- 🔄 **Caching**: Add Redis without changing business logic
- 🔄 **Message Queue**: Add async processing (RabbitMQ) for notifications
- 🔄 **Search Engine**: Add Elasticsearch for advanced ride search

---

## 📚 References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Vaadin Documentation](https://vaadin.com/docs)
- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Domain-Driven Design](https://martinfowler.com/tags/domain%20driven%20design.html)

---

**Document Version**: 1.0
**Last Updated**: December 10, 2025
**Author**: Architecture Analysis for UniCovoit Presentation

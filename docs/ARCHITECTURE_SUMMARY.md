# 📐 UniCovoit Architecture - One-Page Summary

## 🏗️ Architecture Pattern
**Layered N-Tier Architecture** with clear separation of concerns

---

## 📊 System Overview

```
┌─────────────────────────────────────────────────────┐
│  PRESENTATION LAYER (16 Views)                      │
│  • Vaadin Flow UI Components                        │
│  • Session Management & Routing                     │
├─────────────────────────────────────────────────────┤
│  SERVICE LAYER (5 Services)                         │
│  • Business Logic & Validation                      │
│  • Transaction Management                           │
├─────────────────────────────────────────────────────┤
│  DATA ACCESS LAYER (5 DAOs)                         │
│  • Spring Data JPA Repositories                     │
│  • JPQL Custom Queries                              │
├─────────────────────────────────────────────────────┤
│  PERSISTENCE LAYER (6 Entities + 3 Enums)           │
│  • JPA/Hibernate Domain Model                       │
│  • Bean Validation                                  │
├─────────────────────────────────────────────────────┤
│  DATABASE (MySQL 8)                                 │
│  • 6 Tables with Foreign Keys & Indexes             │
└─────────────────────────────────────────────────────┘

CROSS-CUTTING: Exceptions (5) • DTOs (7) • Utils (2) • Config (1)
```

---

## 🎯 Key Components

| Layer | Components | Responsibility |
|-------|------------|----------------|
| **Presentation** | LoginView, SearchView, RideDetailView, etc. | User interface, input handling, navigation |
| **Service** | UserService, RideService, BookingService, etc. | Business rules, validation, orchestration |
| **Data Access** | UserAccountDao, RideDao, BookingDao, etc. | Database queries, CRUD operations |
| **Persistence** | UserAccount, Ride, Booking, Message, Vehicle | Data structure, relationships, constraints |

---

## 🔄 Request Flow Example

**Scenario**: User books a ride

1. **View** (RideDetailView) → Validates input, creates BookingDto
2. **Service** (BookingService) → Checks business rules, manages transaction
3. **DAO** (BookingDao, RideDao) → Persists booking, updates ride seats
4. **Database** (MySQL) → Executes INSERT + UPDATE in single transaction
5. **Return** → View shows success notification

**Transaction ensures atomicity**: Both operations succeed or both fail.

---

## 🛡️ Architecture Decisions

| Decision | Justification | Alternative Rejected |
|----------|---------------|----------------------|
| **Layered Architecture** | Maintainable, testable, scalable | Monolithic (unmaintainable) |
| **Spring Data JPA** | Less boilerplate, portable | JDBC (too verbose) |
| **Vaadin** | Type-safe Java UI, secure | React (requires JS) |
| **DTOs** | Input validation, security | Expose entities (unsafe) |
| **Custom Exceptions** | Clear error handling | Generic exceptions |
| **Session Auth** | Simple, fits Vaadin | JWT (overkill) |

---

## 📈 Project Metrics

- **Total Classes**: 50 Java files
- **Lines of Code**: ~8,000
- **Database Tables**: 6
- **Test Users**: 11 (10 students + 1 admin)
- **Sample Data**: 7 vehicles, 12 rides, 8 bookings, 15 messages

---

## 🔒 Security Features

✅ BCrypt password hashing (Spring Security)
✅ Server-side rendering (no exposed business logic)
✅ Session-based authentication
✅ Ownership verification (users can only modify their own data)
✅ SQL injection prevention (JPA parameterized queries)
✅ Bean Validation (multiple validation layers)

---

## 💪 Strengths

1. **Clean Separation**: Each layer has one responsibility
2. **Testability**: Can mock dependencies and test in isolation
3. **Maintainability**: Easy to understand and modify
4. **Scalability**: Can add features without breaking existing code
5. **Type Safety**: Compile-time checking throughout
6. **Transaction Integrity**: Database consistency guaranteed
7. **Reusability**: Services can be used by multiple views

---

## 🚀 Future Scalability

The architecture supports:
- ✅ REST API (add @RestController without touching services)
- ✅ Mobile App (reuse service layer)
- ✅ Microservices (split services into separate deployments)
- ✅ Caching (add Redis at service layer)
- ✅ Message Queue (add async processing)
- ✅ Search Engine (add Elasticsearch)

---

## 🛠️ Technology Stack

**Backend**:
- Spring Boot 3.4.12
- Spring Data JPA
- Hibernate ORM
- Bean Validation (JSR-380)
- MySQL 8

**Frontend**:
- Vaadin Flow 24.9.6
- Lumo Theme
- Server-side rendering

**Build & Deployment**:
- Maven
- Java 23

---

## 🎓 Key Principles Applied

| Principle | How Applied |
|-----------|-------------|
| **Single Responsibility** | Each class has one job |
| **Open/Closed** | Open for extension (interfaces), closed for modification |
| **Liskov Substitution** | DAOs implement JpaRepository interface |
| **Interface Segregation** | Small, focused interfaces |
| **Dependency Inversion** | Depend on abstractions (interfaces) |
| **DRY** | Utilities for common operations |
| **KISS** | Simple, straightforward design |

---

## 📋 Package Structure

```
com.unicovoit/
├── config/             SecurityConfig (BCrypt)
├── dao/                5 Spring Data repositories
├── dto/                7 validated data transfer objects
├── entity/             6 JPA entities + 3 enums
├── exception/          5 custom exceptions
├── service/            5 transactional services
├── util/               SessionManager, NotificationHelper
└── views/              16 Vaadin views
    ├── auth/           Login, Register
    ├── booking/        MyBookings, BookingRequests
    ├── home/           Home dashboard
    ├── layout/         MainLayout (navigation)
    ├── message/        Messages, Conversation
    ├── ride/           Search, Create, MyRides, Detail
    └── vehicle/        VehicleList, VehicleForm
```

---

## 🔗 Database Relationships

```
UserAccount 1 ──< N Vehicle
UserAccount 1 ──< N Ride (as driver)
UserAccount 1 ──< N Booking (as passenger)
UserAccount 1 ──< N Message (sender/receiver)
Vehicle 1 ──< N Ride
Ride 1 ──< N Booking
Ride 1 ──< N Message (optional)
```

---

## 📞 Contact & Resources

- **Source Code**: [GitHub Repository]
- **Database Schema**: `database/unicovoit_schema_with_data.sql`
- **Full Analysis**: `docs/ARCHITECTURE_ANALYSIS.md`
- **Presentation Guide**: `docs/PRESENTATION_GUIDE.md`
- **Documentation**: `database/README.md`

---

**UniCovoit** - A well-architected student carpooling platform demonstrating clean code, SOLID principles, and industry best practices.

*Version 1.0 | December 2025*

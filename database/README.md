# UniCovoit Database Setup

## 📁 Files

- `unicovoit_schema_with_data.sql` - Complete database schema with test data

## 🚀 Quick Start

### 1. Execute the SQL Script

```bash
mysql -u root -p < database/unicovoit_schema_with_data.sql
```

Or using MySQL Workbench:
1. Open MySQL Workbench
2. Open the file `unicovoit_schema_with_data.sql`
3. Execute the script (Ctrl+Shift+Enter)

### 2. Update Application Properties

Ensure your `src/main/resources/application.properties` matches:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/unicovoit?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=unicovoit
spring.datasource.password=mypassword
spring.jpa.hibernate.ddl-auto=validate
```

**Note**: Change `ddl-auto` from `update` to `validate` since we're using a pre-created schema.

### 3. Create MySQL User (Optional but Recommended)

```sql
CREATE USER 'unicovoit'@'localhost' IDENTIFIED BY 'mypassword';
GRANT ALL PRIVILEGES ON unicovoit.* TO 'unicovoit'@'localhost';
FLUSH PRIVILEGES;
```

## 👤 Test Credentials

All test users have the same password: **`password123`**

### Students

| Email | Name | University |
|-------|------|------------|
| sophie.martin@sorbonne.fr | Sophie Martin | Sorbonne Paris Nord |
| lucas.bernard@sorbonne.fr | Lucas Bernard | Sorbonne Paris Nord |
| emma.dubois@paris.fr | Emma Dubois | Université de Paris |
| thomas.petit@paris.fr | Thomas Petit | Université de Paris |
| lea.moreau@lyon.fr | Léa Moreau | Université Lyon 1 |
| hugo.simon@lyon.fr | Hugo Simon | Université Lyon 1 |
| chloe.laurent@toulouse.fr | Chloé Laurent | Université Toulouse |
| nathan.lefebvre@toulouse.fr | Nathan Lefebvre | Université Toulouse |
| camille.roux@marseille.fr | Camille Roux | Aix-Marseille Université |
| julien.fournier@marseille.fr | Julien Fournier | Aix-Marseille Université |

### Administrator

| Email | Password | Role |
|-------|----------|------|
| admin@unicovoit.fr | password123 | ADMIN |

## 📊 Test Data Overview

### Users
- **11 total users**: 10 students + 1 admin
- Distributed across 5 different universities

### Vehicles
- **7 vehicles** owned by various students
- Mix of popular French car brands (Renault, Peugeot, Citroën, etc.)
- 4-5 seats each

### Rides
- **12 total rides**:
  - **7 published** (future rides available for booking)
  - **2 completed** (past rides)
  - **2 cancelled** (for testing cancellation flow)
  - **1 fully booked** (for testing availability)

### Popular Routes
- Paris ↔ Lyon
- Paris → Toulouse
- Lyon → Marseille
- Marseille → Nice
- Toulouse → Bordeaux

### Bookings
- **8 bookings** across different rides
- Mix of statuses: `CONFIRMED`, `PENDING`, `CANCELLED_BY_PASSENGER`
- Some rides fully booked, others with availability

### Messages
- **15 messages** creating realistic conversations
- Mix of read/unread messages
- Conversations about:
  - Booking inquiries
  - Meeting point details
  - Cancellations
  - General discussions

### Password Reset Tokens
- **3 tokens** for testing:
  - 1 active (valid)
  - 1 expired
  - 1 already used

## 🧪 Testing Scenarios

### Scenario 1: User Registration & Login
```
1. Navigate to /register
2. Create a new account
3. Login with new credentials
```

### Scenario 2: Search & Book a Ride
```
1. Login as: emma.dubois@paris.fr
2. Navigate to /search
3. Search: Paris → Lyon on 2025-12-15
4. Book ride #1 (1 seat available)
5. Check "My Bookings"
```

### Scenario 3: Create a Ride
```
1. Login as: thomas.petit@paris.fr
2. Add a vehicle first (/vehicles)
3. Navigate to /create-ride
4. Create a new ride
5. Check "My Rides"
```

### Scenario 4: Driver Accepts Booking
```
1. Login as: lucas.bernard@sorbonne.fr (driver of ride #2)
2. Navigate to /booking-requests
3. See pending bookings from Thomas and Julien
4. Accept or reject bookings
```

### Scenario 5: Messaging
```
1. Login as: sophie.martin@sorbonne.fr
2. Navigate to /messages
3. See 1 unread message from Thomas
4. Reply to conversation
```

### Scenario 6: View Ride Details
```
1. Login as any user
2. Search for rides
3. Click on a ride card
4. View full ride details with booking option
```

## 🗄️ Database Schema

### Tables Created

1. **user_account** - User profiles
2. **vehicle** - User vehicles
3. **ride** - Carpooling rides
4. **booking** - Ride reservations
5. **message** - User messages
6. **password_reset_token** - Password reset functionality

### Key Relationships

```
user_account (1) ←→ (N) vehicle
user_account (1) ←→ (N) ride (as driver)
user_account (1) ←→ (N) booking (as passenger)
user_account (1) ←→ (N) message (as sender/receiver)
ride (1) ←→ (N) booking
ride (1) ←→ (N) message (optional)
vehicle (1) ←→ (N) ride
```

## 🔍 Useful Queries

### Check Available Rides
```sql
SELECT * FROM ride
WHERE status = 'PUBLISHED'
  AND departure_datetime > NOW()
  AND seats_available > 0;
```

### Check User's Bookings
```sql
SELECT b.*, r.departure_city, r.arrival_city, r.departure_datetime
FROM booking b
JOIN ride r ON b.ride_id = r.id
WHERE b.passenger_id = 4  -- Thomas Petit
ORDER BY r.departure_datetime;
```

### Check Unread Messages
```sql
SELECT * FROM message
WHERE receiver_id = 2  -- Lucas Bernard
  AND is_read = 0
ORDER BY sent_at DESC;
```

### Check Ride Statistics
```sql
SELECT
    u.first_name,
    u.last_name,
    COUNT(*) as rides_created,
    SUM(CASE WHEN r.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_rides
FROM ride r
JOIN user_account u ON r.driver_id = u.id
GROUP BY u.id, u.first_name, u.last_name;
```

## ⚠️ Important Notes

1. **Password Hashing**: All passwords are BCrypt hashed. The hash provided is for "password123"

2. **Date/Time**: Test rides use dates in December 2025. Adjust if needed:
   ```sql
   UPDATE ride
   SET departure_datetime = DATE_ADD(departure_datetime, INTERVAL 1 YEAR)
   WHERE status = 'PUBLISHED';
   ```

3. **Hibernate ddl-auto**:
   - Use `validate` to keep the manual schema
   - Use `update` to let Hibernate modify (not recommended)
   - Use `none` to disable Hibernate DDL generation

4. **Indexes**: All necessary indexes are created for optimal query performance

5. **Foreign Keys**: All relationships use `ON DELETE CASCADE` or `ON DELETE SET NULL` appropriately

## 🔄 Reset Database

To reset the database and reload test data:

```bash
mysql -u root -p < database/unicovoit_schema_with_data.sql
```

This will:
1. Drop existing database
2. Recreate all tables
3. Insert all test data

## 📝 Customization

### Add More Test Users

```sql
INSERT INTO user_account (first_name, last_name, email, university, password_hash, role)
VALUES ('John', 'Doe', 'john.doe@test.fr', 'Test University',
        '$2a$10$N9qo8uLOickgx2ZMRZoMye.L3PJL4xIpYJKR3YsZxBNBJKxLKo4rK', 'STUDENT');
```

### Add More Test Rides

```sql
INSERT INTO ride (driver_id, vehicle_id, departure_city, arrival_city,
                  departure_datetime, price_per_seat, seats_total, seats_available, status)
VALUES (1, 1, 'Paris', 'Lyon', '2025-12-25 10:00:00', 25.00, 3, 3, 'PUBLISHED');
```

## 🆘 Troubleshooting

### Problem: "Database already exists"
**Solution**: The script automatically drops and recreates the database

### Problem: "Access denied"
**Solution**: Check MySQL credentials and user permissions

### Problem: "Unknown database"
**Solution**: The script creates the database automatically

### Problem: "Dates in the past"
**Solution**: Update ride dates or adjust system time for testing

## 📚 Additional Resources

- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Spring Boot Data JPA Guide](https://spring.io/guides/gs/accessing-data-jpa/)
- [Hibernate Documentation](https://hibernate.org/orm/documentation/)

# 🚀 Quick Start Guide

## Run the SQL Script

```bash
mysql -u root -p < database/unicovoit_schema_with_data.sql
```

## Test Credentials

**All users password**: `password123`

```
sophie.martin@sorbonne.fr    (Student - Has vehicles & rides)
lucas.bernard@sorbonne.fr    (Student - Has rides with bookings)
emma.dubois@paris.fr         (Student)
thomas.petit@paris.fr        (Student)
admin@unicovoit.fr           (Admin)
```

## What You Get

✅ **11 users** (10 students + 1 admin)
✅ **7 vehicles** (various French car brands)
✅ **12 rides** (7 published, 2 completed, 2 cancelled, 1 fully booked)
✅ **8 bookings** (confirmed, pending, cancelled)
✅ **15 messages** (conversations with read/unread status)
✅ **3 password reset tokens** (active, expired, used)

## Database Created

- Database name: `unicovoit`
- Charset: `utf8mb4_unicode_ci`
- Engine: `InnoDB`
- All tables with proper indexes and foreign keys

## Test Scenarios Ready

1. ✅ Login with any test user
2. ✅ Search rides: Paris → Lyon (2025-12-15)
3. ✅ Book available rides
4. ✅ Create new rides (users with vehicles)
5. ✅ Accept/reject booking requests
6. ✅ Send messages between users
7. ✅ View ride details and driver info

## Files Created

📄 `unicovoit_schema_with_data.sql` - Complete SQL script (366 lines)
📖 `README.md` - Detailed documentation
🚀 `QUICK_START.md` - This file

---

**Ready to go!** Just run the SQL script and start your Spring Boot app.

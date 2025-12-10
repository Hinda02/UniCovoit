-- ============================================================================
-- UniCovoit Database Schema - Complete Setup with Test Data
-- Compatible with Spring Boot 3.4.12 + Hibernate
-- MySQL 8.0+
-- ============================================================================

-- Drop database if exists and create fresh
DROP DATABASE IF EXISTS unicovoit;
CREATE DATABASE unicovoit
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE unicovoit;

-- ============================================================================
-- TABLE: user_account
-- Stores student and admin users
-- ============================================================================
CREATE TABLE user_account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    university VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_university (university)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE: vehicle
-- Stores vehicles owned by users
-- ============================================================================
CREATE TABLE vehicle (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    color VARCHAR(50),
    plate_number VARCHAR(50),
    seats_total INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (owner_id) REFERENCES user_account(id) ON DELETE CASCADE,
    INDEX idx_owner (owner_id),
    INDEX idx_brand_model (brand, model)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE: ride
-- Stores carpooling rides
-- ============================================================================
CREATE TABLE ride (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    driver_id BIGINT NOT NULL,
    vehicle_id BIGINT,
    departure_city VARCHAR(150) NOT NULL,
    departure_address VARCHAR(255),
    arrival_city VARCHAR(150) NOT NULL,
    arrival_address VARCHAR(255),
    departure_datetime DATETIME NOT NULL,
    duration_minutes INT,
    price_per_seat DECIMAL(8,2) NOT NULL,
    seats_total INT NOT NULL,
    seats_available INT NOT NULL,
    description TEXT,
    music_enabled TINYINT(1) NOT NULL DEFAULT 0,
    pets_allowed TINYINT(1) NOT NULL DEFAULT 0,
    smoking_allowed TINYINT(1) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (driver_id) REFERENCES user_account(id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(id) ON DELETE SET NULL,
    INDEX idx_driver (driver_id),
    INDEX idx_vehicle (vehicle_id),
    INDEX idx_departure_city (departure_city),
    INDEX idx_arrival_city (arrival_city),
    INDEX idx_departure_datetime (departure_datetime),
    INDEX idx_status (status),
    INDEX idx_search (departure_city, arrival_city, departure_datetime, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE: booking
-- Stores ride reservations
-- ============================================================================
CREATE TABLE booking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ride_id BIGINT NOT NULL,
    passenger_id BIGINT NOT NULL,
    seats_booked INT NOT NULL DEFAULT 1,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (ride_id) REFERENCES ride(id) ON DELETE CASCADE,
    FOREIGN KEY (passenger_id) REFERENCES user_account(id) ON DELETE CASCADE,
    INDEX idx_ride (ride_id),
    INDEX idx_passenger (passenger_id),
    INDEX idx_status (status),
    UNIQUE KEY uk_passenger_ride (passenger_id, ride_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE: message
-- Stores messages between users
-- ============================================================================
CREATE TABLE message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    ride_id BIGINT,
    content TEXT NOT NULL,
    sent_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_read TINYINT(1) NOT NULL DEFAULT 0,

    FOREIGN KEY (sender_id) REFERENCES user_account(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES user_account(id) ON DELETE CASCADE,
    FOREIGN KEY (ride_id) REFERENCES ride(id) ON DELETE SET NULL,
    INDEX idx_sender (sender_id),
    INDEX idx_receiver (receiver_id),
    INDEX idx_ride (ride_id),
    INDEX idx_conversation (sender_id, receiver_id),
    INDEX idx_unread (receiver_id, is_read, sent_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE: password_reset_token
-- Stores password reset tokens
-- ============================================================================
CREATE TABLE password_reset_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(100) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME NOT NULL,
    used TINYINT(1) NOT NULL DEFAULT 0,

    FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_token (token),
    INDEX idx_expiry (expires_at, used)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- INSERT TEST DATA
-- ============================================================================

-- ----------------------------------------------------------------------------
-- USER ACCOUNTS (10 students + 1 admin)
-- Password for all: "password123" (BCrypt hash)
-- ----------------------------------------------------------------------------
INSERT INTO user_account (first_name, last_name, email, university, password_hash, role, created_at) VALUES
('Sophie', 'Martin', 'sophie.martin@sorbonne.fr', 'Université Sorbonne Paris Nord', '$2a$10$N9qo8uLOickgx2ZMRZoMye.L3PJL4xIpYJKR3YsZxBNBJKxLKo4rK', 'STUDENT', '2025-01-05 10:00:00'),
('Lucas', 'Bernard', 'lucas.bernard@sorbonne.fr', 'Université Sorbonne Paris Nord', '$2a$10$N9qo8uLOickgx2ZMRZoMye.L3PJL4xIpYJKR3YsZxBNBJKxLKo4rK', 'STUDENT', '2025-01-05 10:15:00'),
('Emma', 'Dubois', 'emma.dubois@paris.fr', 'Université de Paris', '$2a$10$N9qo8uLOickgx2ZMRZoMye.L3PJL4xIpYJKR3YsZxBNBJKxLKo4rK', 'STUDENT', '2025-01-05 10:30:00'),
('Thomas', 'Petit', 'thomas.petit@paris.fr', 'Université de Paris', '$2a$10$N9qo8uLOickgx2ZMRZoMye.L3PJL4xIpYJKR3YsZxBNBJKxLKo4rK', 'STUDENT', '2025-01-05 11:00:00'),
('Léa', 'Moreau', 'lea.moreau@lyon.fr', 'Université Lyon 1', '$2a$10$N9qo8uLOickgx2ZMRZoMye.L3PJL4xIpYJKR3YsZxBNBJKxLKo4rK', 'STUDENT', '2025-01-06 09:00:00'),
('Hugo', 'Simon', 'hugo.simon@lyon.fr', 'Université Lyon 1', '$2a$10$N9qo8uLOickgx2ZMRZoMye.L3PJL4xIpYJKR3YsZxBNBJKxLKo4rK', 'STUDENT', '2025-01-06 09:30:00'),
('Chloé', 'Laurent', 'chloe.laurent@toulouse.fr', 'Université Toulouse Jean Jaurès', '$2a$10$N9qo8uLOickgx2ZMRZoMye.L3PJL4xIpYJKR3YsZxBNBJKxLKo4rK', 'STUDENT', '2025-01-06 14:00:00'),
('Nathan', 'Lefebvre', 'nathan.lefebvre@toulouse.fr', 'Université Toulouse Jean Jaurès', '$2a$10$N9qo8uLOickgx2ZMRZoMye.L3PJL4xIpYJKR3YsZxBNBJKxLKo4rK', 'STUDENT', '2025-01-06 14:30:00'),
('Camille', 'Roux', 'camille.roux@marseille.fr', 'Aix-Marseille Université', '$2a$10$N9qo8uLOickgx2ZMRZoMye.L3PJL4xIpYJKR3YsZxBNBJKxLKo4rK', 'STUDENT', '2025-01-07 08:00:00'),
('Julien', 'Fournier', 'julien.fournier@marseille.fr', 'Aix-Marseille Université', '$2a$10$N9qo8uLOickgx2ZMRZoMye.L3PJL4xIpYJKR3YsZxBNBJKxLKo4rK', 'STUDENT', '2025-01-07 08:30:00'),
('Admin', 'UniCovoit', 'admin@unicovoit.fr', 'Administration UniCovoit', '$2a$10$N9qo8uLOickgx2ZMRZoMye.L3PJL4xIpYJKR3YsZxBNBJKxLKo4rK', 'ADMIN', '2025-01-01 00:00:00');

-- ----------------------------------------------------------------------------
-- VEHICLES (7 vehicles owned by different students)
-- ----------------------------------------------------------------------------
INSERT INTO vehicle (owner_id, brand, model, color, plate_number, seats_total, created_at) VALUES
(1, 'Renault', 'Clio', 'Bleu', 'AB-123-CD', 4, '2025-01-05 11:00:00'),
(2, 'Peugeot', '208', 'Rouge', 'EF-456-GH', 4, '2025-01-05 12:00:00'),
(3, 'Citroën', 'C3', 'Blanc', 'IJ-789-KL', 4, '2025-01-05 13:00:00'),
(5, 'Volkswagen', 'Golf', 'Gris', 'MN-012-OP', 5, '2025-01-06 10:00:00'),
(7, 'Toyota', 'Yaris', 'Noir', 'QR-345-ST', 4, '2025-01-06 15:00:00'),
(9, 'Ford', 'Fiesta', 'Vert', 'UV-678-WX', 4, '2025-01-07 09:00:00'),
(1, 'Renault', 'Megane', 'Gris', 'YZ-901-AB', 5, '2025-01-08 10:00:00');

-- ----------------------------------------------------------------------------
-- RIDES (12 rides with various statuses and dates)
-- ----------------------------------------------------------------------------
INSERT INTO ride (driver_id, vehicle_id, departure_city, departure_address, arrival_city, arrival_address,
                  departure_datetime, duration_minutes, price_per_seat, seats_total, seats_available,
                  description, music_enabled, pets_allowed, smoking_allowed, status, created_at) VALUES
-- Published rides (future dates)
(1, 1, 'Paris', '15 Rue de la République, 75001 Paris', 'Lyon', '10 Place Bellecour, 69002 Lyon',
 '2025-12-15 08:00:00', 270, 25.00, 3, 3,
 'Trajet régulier Paris-Lyon, départ matinal. Musique au choix des passagers !', 1, 0, 0, 'PUBLISHED', '2025-12-08 10:00:00'),

(2, 2, 'Paris', 'Gare de Lyon, 75012 Paris', 'Toulouse', 'Gare Matabiau, 31000 Toulouse',
 '2025-12-16 14:00:00', 420, 35.00, 3, 1,
 'Voyage confortable, pause prévue à Limoges. Animaux bienvenus !', 1, 1, 0, 'PUBLISHED', '2025-12-08 11:00:00'),

(3, 3, 'Lyon', '5 Avenue Jean Jaurès, 69007 Lyon', 'Marseille', '20 Boulevard d''Athènes, 13001 Marseille',
 '2025-12-17 09:30:00', 200, 20.00, 3, 3,
 'Trajet direct sans arrêt. Départ ponctuel requis.', 0, 0, 0, 'PUBLISHED', '2025-12-08 12:00:00'),

(5, 4, 'Marseille', 'Vieux Port, 13001 Marseille', 'Nice', 'Promenade des Anglais, 06000 Nice',
 '2025-12-18 10:00:00', 150, 15.00, 4, 4,
 'Belle route côtière, possibilité d''arrêt pour photos !', 1, 0, 0, 'PUBLISHED', '2025-12-08 13:00:00'),

(7, 5, 'Toulouse', 'Place du Capitole, 31000 Toulouse', 'Bordeaux', 'Place de la Bourse, 33000 Bordeaux',
 '2025-12-19 07:00:00', 150, 18.00, 3, 2,
 'Départ matinal pour arriver tôt. Café offert en route !', 1, 0, 0, 'PUBLISHED', '2025-12-08 14:00:00'),

(9, 6, 'Nice', 'Gare SNCF Nice-Ville, 06000 Nice', 'Lyon', 'Gare Part-Dieu, 69003 Lyon',
 '2025-12-20 15:00:00', 280, 30.00, 3, 3,
 'Retour après les vacances, ambiance détendue.', 1, 0, 0, 'PUBLISHED', '2025-12-08 15:00:00'),

(1, 7, 'Paris', 'Porte de Versailles, 75015 Paris', 'Nantes', 'Gare de Nantes, 44000 Nantes',
 '2025-12-21 11:00:00', 240, 22.00, 4, 4,
 'Trajet via autoroute A11, véhicule spacieux et confortable.', 1, 1, 0, 'PUBLISHED', '2025-12-08 16:00:00'),

-- Rides with bookings (some seats taken)
(2, 2, 'Lyon', 'Part-Dieu, 69003 Lyon', 'Paris', 'Gare de Lyon, 75012 Paris',
 '2025-12-14 16:00:00', 270, 25.00, 3, 0,
 'Retour sur Paris en fin de journée. Départ flexible (+/- 30min).', 0, 0, 0, 'PUBLISHED', '2025-12-07 10:00:00'),

-- Completed rides (past dates)
(3, 3, 'Paris', 'Châtelet, 75001 Paris', 'Lyon', 'Bellecour, 69002 Lyon',
 '2025-12-05 09:00:00', 270, 24.00, 3, 0,
 'Trajet complété avec succès. Merci aux passagers !', 1, 0, 0, 'COMPLETED', '2025-12-03 14:00:00'),

(5, 4, 'Marseille', 'Castellane, 13006 Marseille', 'Lyon', 'Perrache, 69002 Lyon',
 '2025-12-06 08:00:00', 210, 22.00, 4, 0,
 'Super trajet, bonne ambiance !', 1, 0, 0, 'COMPLETED', '2025-12-04 10:00:00'),

-- Cancelled rides
(7, 5, 'Toulouse', 'Université Paul Sabatier, 31062 Toulouse', 'Montpellier', 'Place de la Comédie, 34000 Montpellier',
 '2025-12-13 10:00:00', 150, 18.00, 3, 3,
 'Annulé pour raisons personnelles, désolé !', 0, 0, 0, 'CANCELLED', '2025-12-07 08:00:00'),

(9, 6, 'Bordeaux', 'Place Gambetta, 33000 Bordeaux', 'Toulouse', 'Capitole, 31000 Toulouse',
 '2025-12-12 14:00:00', 150, 17.00, 3, 3,
 'Trajet annulé - voiture en panne.', 0, 0, 0, 'CANCELLED', '2025-12-06 12:00:00');

-- ----------------------------------------------------------------------------
-- BOOKINGS (8 bookings with different statuses)
-- ----------------------------------------------------------------------------
INSERT INTO booking (ride_id, passenger_id, seats_booked, status, created_at) VALUES
-- Confirmed bookings for ride #8 (Lyon -> Paris)
(8, 4, 2, 'CONFIRMED', '2025-12-07 11:00:00'),
(8, 6, 1, 'CONFIRMED', '2025-12-07 12:00:00'),

-- Pending bookings for ride #2 (Paris -> Toulouse)
(2, 8, 1, 'PENDING', '2025-12-08 15:00:00'),
(2, 10, 1, 'PENDING', '2025-12-08 16:00:00'),

-- Confirmed booking for ride #5 (Toulouse -> Bordeaux)
(5, 6, 1, 'CONFIRMED', '2025-12-08 15:00:00'),

-- Completed bookings for past rides
(9, 6, 2, 'CONFIRMED', '2025-12-03 15:00:00'),
(10, 8, 3, 'CONFIRMED', '2025-12-04 11:00:00'),

-- Cancelled booking
(5, 10, 1, 'CANCELLED_BY_PASSENGER', '2025-12-08 14:30:00');

-- ----------------------------------------------------------------------------
-- MESSAGES (15 messages showing conversations)
-- ----------------------------------------------------------------------------
INSERT INTO message (sender_id, receiver_id, ride_id, content, sent_at, is_read) VALUES
-- Conversation about ride #2 (Paris -> Toulouse)
(8, 2, 2, 'Bonjour ! Est-ce qu''il reste une place pour le trajet Paris-Toulouse ?', '2025-12-08 15:05:00', 1),
(2, 8, 2, 'Oui bien sûr ! Je viens de valider ta réservation 😊', '2025-12-08 15:10:00', 1),
(8, 2, 2, 'Super merci ! On se retrouve où exactement ?', '2025-12-08 15:15:00', 1),
(2, 8, 2, 'Devant la Gare de Lyon, côté sortie principale. Je t''envoie mon numéro par SMS', '2025-12-08 15:20:00', 0),

-- Conversation about ride #8 (Lyon -> Paris)
(4, 2, 8, 'Salut Lucas ! Possible de prendre 2 places ? Je voyage avec ma sœur', '2025-12-07 10:30:00', 1),
(2, 4, 8, 'Pas de souci, confirmé pour 2 places ! À samedi 👍', '2025-12-07 11:15:00', 1),

-- Conversation about ride #5 (Toulouse -> Bordeaux)
(6, 7, 5, 'Coucou ! Le départ est bien à 7h pile ?', '2025-12-08 14:00:00', 1),
(7, 6, 5, 'Oui exactement, sois à l''heure car je ne pourrai pas attendre 🕐', '2025-12-08 14:05:00', 1),
(6, 7, 5, 'Compris, je serai là à 6h50 ! Merci', '2025-12-08 14:10:00', 0),

-- Conversation about ride #1 (Paris -> Lyon)
(4, 1, 1, 'Bonjour Sophie, est-ce que le trajet est toujours prévu pour vendredi ?', '2025-12-09 09:00:00', 0),

-- General conversation (no specific ride)
(3, 5, NULL, 'Salut Léa ! Tu fais souvent la route Lyon-Marseille ?', '2025-12-08 18:00:00', 1),
(5, 3, NULL, 'Salut Emma ! Oui environ 2 fois par mois, pourquoi ?', '2025-12-08 18:30:00', 0),

-- Conversation about booking cancellation
(10, 7, 5, 'Désolé, je dois annuler ma réservation, imprévu de dernière minute 😕', '2025-12-08 14:25:00', 1),
(7, 10, 5, 'Pas de problème, j''espère que tout va bien ! À une prochaine fois', '2025-12-08 14:35:00', 1),

-- Request for ride #4 (Marseille -> Nice)
(2, 5, 4, 'Coucou ! Il reste de la place pour Nice mercredi ?', '2025-12-09 10:00:00', 0);

-- ----------------------------------------------------------------------------
-- PASSWORD RESET TOKENS (3 tokens - 1 active, 1 expired, 1 used)
-- ----------------------------------------------------------------------------
INSERT INTO password_reset_token (user_id, token, created_at, expires_at, used) VALUES
-- Active token (valid for 24h)
(4, 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6', '2025-12-10 10:00:00', '2025-12-11 10:00:00', 0),

-- Expired token
(6, 'z9y8x7w6v5u4t3s2r1q0p9o8n7m6l5k4', '2025-12-08 08:00:00', '2025-12-09 08:00:00', 0),

-- Used token
(8, 'q1w2e3r4t5y6u7i8o9p0a1s2d3f4g5h6', '2025-12-07 14:00:00', '2025-12-08 14:00:00', 1);

-- ============================================================================
-- SUMMARY STATISTICS
-- ============================================================================

SELECT '=== DATABASE SETUP COMPLETE ===' AS '';
SELECT CONCAT('Users: ', COUNT(*), ' (', SUM(role = 'STUDENT'), ' students, ', SUM(role = 'ADMIN'), ' admins)') AS summary FROM user_account;
SELECT CONCAT('Vehicles: ', COUNT(*)) AS summary FROM vehicle;
SELECT CONCAT('Rides: ', COUNT(*),
              ' (Published: ', SUM(status = 'PUBLISHED'),
              ', Completed: ', SUM(status = 'COMPLETED'),
              ', Cancelled: ', SUM(status = 'CANCELLED'), ')') AS summary FROM ride;
SELECT CONCAT('Bookings: ', COUNT(*),
              ' (Confirmed: ', SUM(status = 'CONFIRMED'),
              ', Pending: ', SUM(status = 'PENDING'),
              ', Cancelled: ', SUM(status LIKE 'CANCELLED%'), ')') AS summary FROM booking;
SELECT CONCAT('Messages: ', COUNT(*), ' (Unread: ', SUM(is_read = 0), ')') AS summary FROM message;
SELECT CONCAT('Password Reset Tokens: ', COUNT(*), ' (Active: ', SUM(used = 0 AND expires_at > NOW()), ')') AS summary FROM password_reset_token;

SELECT '=== LOGIN CREDENTIALS FOR TESTING ===' AS '';
SELECT 'Email: sophie.martin@sorbonne.fr | Password: password123' AS credentials
UNION ALL SELECT 'Email: lucas.bernard@sorbonne.fr | Password: password123'
UNION ALL SELECT 'Email: emma.dubois@paris.fr | Password: password123'
UNION ALL SELECT 'Email: thomas.petit@paris.fr | Password: password123'
UNION ALL SELECT 'Email: admin@unicovoit.fr | Password: password123 (ADMIN)';

-- ============================================================================
-- VERIFICATION QUERIES (Optional - comment out if not needed)
-- ============================================================================

-- Available rides for search testing
SELECT '=== UPCOMING PUBLISHED RIDES ===' AS '';
SELECT
    CONCAT(departure_city, ' → ', arrival_city) AS route,
    DATE_FORMAT(departure_datetime, '%d/%m/%Y %H:%i') AS departure,
    CONCAT(u.first_name, ' ', u.last_name) AS driver,
    seats_available AS available_seats,
    price_per_seat AS price
FROM ride r
JOIN user_account u ON r.driver_id = u.id
WHERE r.status = 'PUBLISHED'
  AND r.departure_datetime > NOW()
ORDER BY r.departure_datetime;

-- Unread messages count per user
SELECT '=== UNREAD MESSAGES BY USER ===' AS '';
SELECT
    CONCAT(u.first_name, ' ', u.last_name) AS user,
    COUNT(*) AS unread_messages
FROM message m
JOIN user_account u ON m.receiver_id = u.id
WHERE m.is_read = 0
GROUP BY u.id, u.first_name, u.last_name
ORDER BY unread_messages DESC;

SELECT '=== SETUP COMPLETE! You can now run your Spring Boot application ===' AS '';

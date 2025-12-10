package com.unicovoit.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_account")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String university;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.STUDENT;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships (optional but useful)

    @OneToMany(mappedBy = "owner")
    private List<Vehicle> vehicles = new ArrayList<>();

    @OneToMany(mappedBy = "driver")
    private List<Ride> ridesAsDriver = new ArrayList<>();

    @OneToMany(mappedBy = "passenger")
    private List<Booking> bookingsAsPassenger = new ArrayList<>();

    @OneToMany(mappedBy = "sender")
    private List<Message> messagesSent = new ArrayList<>();

    @OneToMany(mappedBy = "receiver")
    private List<Message> messagesReceived = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<PasswordResetToken> resetTokens = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters & setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public List<Ride> getRidesAsDriver() {
        return ridesAsDriver;
    }

    public List<Booking> getBookingsAsPassenger() {
        return bookingsAsPassenger;
    }

    public List<Message> getMessagesSent() {
        return messagesSent;
    }

    public List<Message> getMessagesReceived() {
        return messagesReceived;
    }

    public List<PasswordResetToken> getResetTokens() {
        return resetTokens;
    }
}

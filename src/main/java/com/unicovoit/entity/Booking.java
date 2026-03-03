package com.unicovoit.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le trajet est obligatoire")
    @ManyToOne(optional = false)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @NotNull(message = "Le passager est obligatoire")
    @ManyToOne(optional = false)
    @JoinColumn(name = "passenger_id", nullable = false)
    private UserAccount passenger;

    @Min(value = 1, message = "Le nombre de places réservées doit être au moins 1")
    @Max(value = 8, message = "Le nombre de places réservées ne doit pas dépasser 8")
    @Column(name = "seats_booked", nullable = false)
    private int seatsBooked = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public UserAccount getPassenger() {
        return passenger;
    }

    public void setPassenger(UserAccount passenger) {
        this.passenger = passenger;
    }

    public int getSeatsBooked() {
        return seatsBooked;
    }

    public void setSeatsBooked(int seatsBooked) {
        this.seatsBooked = seatsBooked;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

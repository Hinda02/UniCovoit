package com.unicovoit.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ride")
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le conducteur est obligatoire")
    @ManyToOne(optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private UserAccount driver;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @NotBlank(message = "La ville de départ est obligatoire")
    @Size(max = 150, message = "La ville de départ ne doit pas dépasser 150 caractères")
    @Column(name = "departure_city", nullable = false, length = 150)
    private String departureCity;

    @Size(max = 255, message = "L'adresse de départ ne doit pas dépasser 255 caractères")
    @Column(name = "departure_address", length = 255)
    private String departureAddress;

    @NotBlank(message = "La ville d'arrivée est obligatoire")
    @Size(max = 150, message = "La ville d'arrivée ne doit pas dépasser 150 caractères")
    @Column(name = "arrival_city", nullable = false, length = 150)
    private String arrivalCity;

    @Size(max = 255, message = "L'adresse d'arrivée ne doit pas dépasser 255 caractères")
    @Column(name = "arrival_address", length = 255)
    private String arrivalAddress;

    @NotNull(message = "La date et l'heure de départ sont obligatoires")
    @Future(message = "La date de départ doit être dans le futur")
    @Column(name = "departure_datetime", nullable = false)
    private LocalDateTime departureDateTime;

    @Min(value = 0, message = "La durée doit être positive")
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @NotNull(message = "Le prix par place est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix doit être positif ou nul")
    @DecimalMax(value = "999.99", message = "Le prix ne doit pas dépasser 999.99")
    @Column(name = "price_per_seat", nullable = false, precision = 8, scale = 2)
    private BigDecimal pricePerSeat;

    @Min(value = 1, message = "Le nombre de places doit être au moins 1")
    @Max(value = 8, message = "Le nombre de places ne doit pas dépasser 8")
    @Column(name = "seats_total", nullable = false)
    private int seatsTotal;

    @Min(value = 0, message = "Le nombre de places disponibles doit être positif ou nul")
    @Column(name = "seats_available", nullable = false)
    private int seatsAvailable;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "music_enabled", nullable = false)
    private boolean musicEnabled = false;

    @Column(name = "pets_allowed", nullable = false)
    private boolean petsAllowed = false;

    @Column(name = "smoking_allowed", nullable = false)
    private boolean smokingAllowed = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RideStatus status = RideStatus.PUBLISHED;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "ride")
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "ride")
    private List<Message> messages = new ArrayList<>();

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

    public UserAccount getDriver() {
        return driver;
    }

    public void setDriver(UserAccount driver) {
        this.driver = driver;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getDepartureAddress() {
        return departureAddress;
    }

    public void setDepartureAddress(String departureAddress) {
        this.departureAddress = departureAddress;
    }

    public String getArrivalCity() {
        return arrivalCity;
    }

    public void setArrivalCity(String arrivalCity) {
        this.arrivalCity = arrivalCity;
    }

    public String getArrivalAddress() {
        return arrivalAddress;
    }

    public void setArrivalAddress(String arrivalAddress) {
        this.arrivalAddress = arrivalAddress;
    }

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public void setDepartureDateTime(LocalDateTime departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getPricePerSeat() {
        return pricePerSeat;
    }

    public void setPricePerSeat(BigDecimal pricePerSeat) {
        this.pricePerSeat = pricePerSeat;
    }

    public int getSeatsTotal() {
        return seatsTotal;
    }

    public void setSeatsTotal(int seatsTotal) {
        this.seatsTotal = seatsTotal;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(int seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
    }

    public boolean isPetsAllowed() {
        return petsAllowed;
    }

    public void setPetsAllowed(boolean petsAllowed) {
        this.petsAllowed = petsAllowed;
    }

    public boolean isSmokingAllowed() {
        return smokingAllowed;
    }

    public void setSmokingAllowed(boolean smokingAllowed) {
        this.smokingAllowed = smokingAllowed;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public List<Message> getMessages() {
        return messages;
    }
}

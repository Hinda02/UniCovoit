package com.unicovoit.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le propriétaire est obligatoire")
    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserAccount owner;

    @NotBlank(message = "La marque est obligatoire")
    @Size(max = 100, message = "La marque ne doit pas dépasser 100 caractères")
    @Column(nullable = false, length = 100)
    private String brand;

    @NotBlank(message = "Le modèle est obligatoire")
    @Size(max = 100, message = "Le modèle ne doit pas dépasser 100 caractères")
    @Column(nullable = false, length = 100)
    private String model;

    @Size(max = 50, message = "La couleur ne doit pas dépasser 50 caractères")
    @Column(length = 50)
    private String color;

    @Size(max = 50, message = "La plaque ne doit pas dépasser 50 caractères")
    @Column(name = "plate_number", length = 50)
    private String plateNumber;

    @Min(value = 1, message = "Le nombre de places doit être au moins 1")
    @Max(value = 8, message = "Le nombre de places ne doit pas dépasser 8")
    @Column(name = "seats_total", nullable = false)
    private int seatsTotal;

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

    public UserAccount getOwner() {
        return owner;
    }

    public void setOwner(UserAccount owner) {
        this.owner = owner;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public int getSeatsTotal() {
        return seatsTotal;
    }

    public void setSeatsTotal(int seatsTotal) {
        this.seatsTotal = seatsTotal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

package com.unicovoit.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateRideDto {

    @NotNull(message = "Le véhicule est obligatoire")
    private Long vehicleId;

    @NotBlank(message = "La ville de départ est obligatoire")
    @Size(max = 150, message = "La ville de départ ne doit pas dépasser 150 caractères")
    private String departureCity;

    @Size(max = 255, message = "L'adresse de départ ne doit pas dépasser 255 caractères")
    private String departureAddress;

    @NotBlank(message = "La ville d'arrivée est obligatoire")
    @Size(max = 150, message = "La ville d'arrivée ne doit pas dépasser 150 caractères")
    private String arrivalCity;

    @Size(max = 255, message = "L'adresse d'arrivée ne doit pas dépasser 255 caractères")
    private String arrivalAddress;

    @NotNull(message = "La date et l'heure de départ sont obligatoires")
    private LocalDateTime departureDateTime;

    @Min(value = 0, message = "La durée doit être positive")
    private Integer durationMinutes;

    @NotNull(message = "Le prix par place est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix doit être positif ou nul")
    @DecimalMax(value = "999.99", message = "Le prix ne doit pas dépasser 999.99")
    private BigDecimal pricePerSeat;

    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Le nombre de places doit être au moins 1")
    @Max(value = 8, message = "Le nombre de places ne doit pas dépasser 8")
    private Integer seatsTotal;

    @Size(max = 5000, message = "La description ne doit pas dépasser 5000 caractères")
    private String description;

    private boolean musicEnabled = false;
    private boolean petsAllowed = false;
    private boolean smokingAllowed = false;

    public CreateRideDto() {}

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
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

    public Integer getSeatsTotal() {
        return seatsTotal;
    }

    public void setSeatsTotal(Integer seatsTotal) {
        this.seatsTotal = seatsTotal;
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
}

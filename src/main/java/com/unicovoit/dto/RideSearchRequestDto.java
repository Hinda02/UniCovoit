package com.unicovoit.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class RideSearchRequestDto {

    @NotBlank(message = "La ville de départ est obligatoire")
    private String departureCity;

    @NotBlank(message = "La ville d'arrivée est obligatoire")
    private String arrivalCity;

    @NotNull(message = "La date est obligatoire")
    private LocalDate date;

    public String getDepartureCity() { return departureCity; }
    public void setDepartureCity(String departureCity) { this.departureCity = departureCity; }

    public String getArrivalCity() { return arrivalCity; }
    public void setArrivalCity(String arrivalCity) { this.arrivalCity = arrivalCity; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}

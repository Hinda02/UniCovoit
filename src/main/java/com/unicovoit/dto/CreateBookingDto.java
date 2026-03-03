package com.unicovoit.dto;

import jakarta.validation.constraints.*;

public class CreateBookingDto {

    @NotNull(message = "Le trajet est obligatoire")
    private Long rideId;

    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Le nombre de places doit être au moins 1")
    @Max(value = 8, message = "Le nombre de places ne doit pas dépasser 8")
    private Integer seatsBooked;

    public CreateBookingDto() {}

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Integer getSeatsBooked() {
        return seatsBooked;
    }

    public void setSeatsBooked(Integer seatsBooked) {
        this.seatsBooked = seatsBooked;
    }
}

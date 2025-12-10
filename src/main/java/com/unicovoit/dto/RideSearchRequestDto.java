package com.unicovoit.dto;

import java.time.LocalDate;

public class RideSearchRequestDto {

    private String departureCity;
    private String arrivalCity;
    private LocalDate date;

    public String getDepartureCity() { return departureCity; }
    public void setDepartureCity(String departureCity) { this.departureCity = departureCity; }

    public String getArrivalCity() { return arrivalCity; }
    public void setArrivalCity(String arrivalCity) { this.arrivalCity = arrivalCity; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}

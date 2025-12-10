package com.unicovoit.views.rides;

import java.math.BigDecimal;
import java.time.LocalTime;

public class RideSummary {

    private final String driverName;
    private final double driverRating;
    private final int reviewCount;
    private final String fromCity;
    private final String fromAddress;
    private final String toCity;
    private final String toAddress;
    private final LocalTime departureTime;
    private final int reservedSeats;
    private final int totalSeats;
    private final BigDecimal pricePerSeat;

    public RideSummary(String driverName, double driverRating, int reviewCount,
                       String fromCity, String fromAddress,
                       String toCity, String toAddress,
                       LocalTime departureTime,
                       int reservedSeats, int totalSeats,
                       BigDecimal pricePerSeat) {
        this.driverName = driverName;
        this.driverRating = driverRating;
        this.reviewCount = reviewCount;
        this.fromCity = fromCity;
        this.fromAddress = fromAddress;
        this.toCity = toCity;
        this.toAddress = toAddress;
        this.departureTime = departureTime;
        this.reservedSeats = reservedSeats;
        this.totalSeats = totalSeats;
        this.pricePerSeat = pricePerSeat;
    }

    public String getDriverName() { return driverName; }
    public double getDriverRating() { return driverRating; }
    public int getReviewCount() { return reviewCount; }
    public String getFromCity() { return fromCity; }
    public String getFromAddress() { return fromAddress; }
    public String getToCity() { return toCity; }
    public String getToAddress() { return toAddress; }
    public LocalTime getDepartureTime() { return departureTime; }
    public int getReservedSeats() { return reservedSeats; }
    public int getTotalSeats() { return totalSeats; }
    public BigDecimal getPricePerSeat() { return pricePerSeat; }
}

package com.unicovoit.dao;

import com.unicovoit.entity.Booking;
import com.unicovoit.entity.BookingStatus;
import com.unicovoit.entity.Ride;
import com.unicovoit.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingDao extends JpaRepository<Booking, Long> {

    /**
     * Find all bookings for a specific passenger
     */
    List<Booking> findByPassenger(UserAccount passenger);

    /**
     * Find all bookings for a specific passenger by ID
     */
    List<Booking> findByPassengerId(Long passengerId);

    /**
     * Find all bookings for a specific ride
     */
    List<Booking> findByRide(Ride ride);

    /**
     * Find all bookings for a specific ride by ID
     */
    List<Booking> findByRideId(Long rideId);

    /**
     * Find a booking by passenger and ride
     */
    Optional<Booking> findByPassengerAndRide(UserAccount passenger, Ride ride);

    /**
     * Find bookings by status
     */
    List<Booking> findByStatus(BookingStatus status);

    /**
     * Find all bookings for rides driven by a specific user
     */
    @Query("""
           SELECT b
           FROM Booking b
           WHERE b.ride.driver.id = :driverId
           """)
    List<Booking> findBookingsForDriver(@Param("driverId") Long driverId);

    /**
     * Check if a passenger has already booked a specific ride
     */
    boolean existsByPassengerIdAndRideId(Long passengerId, Long rideId);
}

package com.unicovoit.service;

import com.unicovoit.dao.BookingDao;
import com.unicovoit.dao.RideDao;
import com.unicovoit.dto.CreateBookingDto;
import com.unicovoit.entity.*;
import com.unicovoit.exception.BusinessException;
import com.unicovoit.exception.ResourceNotFoundException;
import com.unicovoit.exception.ValidationException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class BookingService {

    private final BookingDao bookingDao;
    private final RideDao rideDao;

    public BookingService(BookingDao bookingDao, RideDao rideDao) {
        this.bookingDao = bookingDao;
        this.rideDao = rideDao;
    }

    /**
     * Create a new booking
     */
    @Transactional
    public Booking createBooking(@Valid CreateBookingDto dto, UserAccount passenger) {
        if (passenger == null) {
            throw new ValidationException("Le passager est obligatoire.");
        }

        // Get ride
        Ride ride = rideDao.findById(dto.getRideId())
                .orElseThrow(() -> new ResourceNotFoundException("Trajet", dto.getRideId()));

        // Validate ride status
        if (ride.getStatus() != RideStatus.PUBLISHED) {
            throw new BusinessException("Ce trajet n'est plus disponible pour la réservation.");
        }

        // Check if passenger is trying to book their own ride
        if (ride.getDriver().getId().equals(passenger.getId())) {
            throw new ValidationException("Vous ne pouvez pas réserver votre propre trajet.");
        }

        // Check if passenger already has a booking for this ride
        if (bookingDao.existsByPassengerIdAndRideId(passenger.getId(), ride.getId())) {
            throw new BusinessException("Vous avez déjà une réservation pour ce trajet.");
        }

        // Check if enough seats are available
        if (ride.getSeatsAvailable() < dto.getSeatsBooked()) {
            throw new BusinessException(String.format(
                    "Pas assez de places disponibles. Seulement %d place(s) restante(s).",
                    ride.getSeatsAvailable()));
        }

        // Create booking
        Booking booking = new Booking();
        booking.setRide(ride);
        booking.setPassenger(passenger);
        booking.setSeatsBooked(dto.getSeatsBooked());
        booking.setStatus(BookingStatus.PENDING);

        // Update ride available seats
        ride.setSeatsAvailable(ride.getSeatsAvailable() - dto.getSeatsBooked());
        rideDao.save(ride);

        return bookingDao.save(booking);
    }

    /**
     * Confirm a booking (by driver)
     */
    @Transactional
    public Booking confirmBooking(Long bookingId, UserAccount driver) {
        Booking booking = bookingDao.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation", bookingId));

        // Check if user is the driver of the ride
        if (!booking.getRide().getDriver().getId().equals(driver.getId())) {
            throw new ValidationException("Vous n'êtes pas autorisé à confirmer cette réservation.");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessException("Cette réservation ne peut plus être confirmée.");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        return bookingDao.save(booking);
    }

    /**
     * Cancel a booking by passenger
     */
    @Transactional
    public void cancelBookingByPassenger(Long bookingId, UserAccount passenger) {
        Booking booking = bookingDao.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation", bookingId));

        // Check ownership
        if (!booking.getPassenger().getId().equals(passenger.getId())) {
            throw new ValidationException("Vous n'êtes pas autorisé à annuler cette réservation.");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED_BY_PASSENGER ||
            booking.getStatus() == BookingStatus.CANCELLED_BY_DRIVER) {
            throw new BusinessException("Cette réservation est déjà annulée.");
        }

        // Update booking status
        booking.setStatus(BookingStatus.CANCELLED_BY_PASSENGER);
        bookingDao.save(booking);

        // Restore seats to ride
        Ride ride = booking.getRide();
        ride.setSeatsAvailable(ride.getSeatsAvailable() + booking.getSeatsBooked());
        rideDao.save(ride);
    }

    /**
     * Cancel a booking by driver
     */
    @Transactional
    public void cancelBookingByDriver(Long bookingId, UserAccount driver) {
        Booking booking = bookingDao.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation", bookingId));

        // Check if user is the driver
        if (!booking.getRide().getDriver().getId().equals(driver.getId())) {
            throw new ValidationException("Vous n'êtes pas autorisé à annuler cette réservation.");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED_BY_PASSENGER ||
            booking.getStatus() == BookingStatus.CANCELLED_BY_DRIVER) {
            throw new BusinessException("Cette réservation est déjà annulée.");
        }

        // Update booking status
        booking.setStatus(BookingStatus.CANCELLED_BY_DRIVER);
        bookingDao.save(booking);

        // Restore seats to ride
        Ride ride = booking.getRide();
        ride.setSeatsAvailable(ride.getSeatsAvailable() + booking.getSeatsBooked());
        rideDao.save(ride);
    }

    /**
     * Get all bookings for a passenger
     */
    @Transactional(readOnly = true)
    public List<Booking> getPassengerBookings(Long passengerId) {
        return bookingDao.findByPassengerId(passengerId);
    }

    /**
     * Get all bookings for rides driven by a user
     */
    @Transactional(readOnly = true)
    public List<Booking> getDriverBookings(Long driverId) {
        return bookingDao.findBookingsForDriver(driverId);
    }

    /**
     * Get all bookings for a specific ride
     */
    @Transactional(readOnly = true)
    public List<Booking> getRideBookings(Long rideId) {
        return bookingDao.findByRideId(rideId);
    }

    /**
     * Get a booking by ID
     */
    @Transactional(readOnly = true)
    public Booking getBookingById(Long bookingId) {
        return bookingDao.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation", bookingId));
    }
}

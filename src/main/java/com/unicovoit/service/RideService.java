package com.unicovoit.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.unicovoit.dao.VehicleDao;
import com.unicovoit.dto.CreateRideDto;
import com.unicovoit.entity.*;
import com.unicovoit.exception.BusinessException;
import com.unicovoit.exception.ResourceNotFoundException;
import com.unicovoit.exception.ValidationException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import com.unicovoit.dao.RideDao;
import com.unicovoit.dto.RideSearchRequestDto;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class RideService {

    private final RideDao rideDao;
    private final VehicleDao vehicleDao;

    public RideService(RideDao rideDao, VehicleDao vehicleDao) {
        this.rideDao = rideDao;
        this.vehicleDao = vehicleDao;
    }

    /**
     * Create a new ride
     */
    @Transactional
    public Ride createRide(@Valid CreateRideDto dto, UserAccount driver) {
        if (driver == null) {
            throw new ValidationException("Le conducteur est obligatoire.");
        }

        // Validate departure date is in the future
        if (dto.getDepartureDateTime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("La date de départ doit être dans le futur.");
        }

        // Get and validate vehicle
        Vehicle vehicle = vehicleDao.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule", dto.getVehicleId()));

        // Check vehicle ownership
        if (!vehicle.getOwner().getId().equals(driver.getId())) {
            throw new ValidationException("Vous ne pouvez créer un trajet qu'avec vos propres véhicules.");
        }

        // Validate seats
        if (dto.getSeatsTotal() > vehicle.getSeatsTotal()) {
            throw new ValidationException("Le nombre de places proposées ne peut pas dépasser la capacité du véhicule.");
        }

        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setVehicle(vehicle);
        ride.setDepartureCity(dto.getDepartureCity());
        ride.setDepartureAddress(dto.getDepartureAddress());
        ride.setArrivalCity(dto.getArrivalCity());
        ride.setArrivalAddress(dto.getArrivalAddress());
        ride.setDepartureDateTime(dto.getDepartureDateTime());
        ride.setDurationMinutes(dto.getDurationMinutes());
        ride.setPricePerSeat(dto.getPricePerSeat());
        ride.setSeatsTotal(dto.getSeatsTotal());
        ride.setSeatsAvailable(dto.getSeatsTotal()); // Initially all seats are available
        ride.setDescription(dto.getDescription());
        ride.setMusicEnabled(dto.isMusicEnabled());
        ride.setPetsAllowed(dto.isPetsAllowed());
        ride.setSmokingAllowed(dto.isSmokingAllowed());
        ride.setStatus(RideStatus.PUBLISHED);

        return rideDao.save(ride);
    }

    /**
     * Update an existing ride
     */
    @Transactional
    public Ride updateRide(Long rideId, @Valid CreateRideDto dto, UserAccount driver) {
        Ride ride = rideDao.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Trajet", rideId));

        // Check ownership
        if (!ride.getDriver().getId().equals(driver.getId())) {
            throw new ValidationException("Vous n'êtes pas autorisé à modifier ce trajet.");
        }

        // Cannot modify cancelled or completed rides
        if (ride.getStatus() == RideStatus.CANCELLED || ride.getStatus() == RideStatus.COMPLETED) {
            throw new BusinessException("Impossible de modifier un trajet annulé ou terminé.");
        }

        // Validate departure date is in the future
        if (dto.getDepartureDateTime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("La date de départ doit être dans le futur.");
        }

        // Get and validate vehicle
        Vehicle vehicle = vehicleDao.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule", dto.getVehicleId()));

        // Check vehicle ownership
        if (!vehicle.getOwner().getId().equals(driver.getId())) {
            throw new ValidationException("Vous ne pouvez utiliser que vos propres véhicules.");
        }

        // Calculate how many seats are already booked
        int seatsBooked = ride.getSeatsTotal() - ride.getSeatsAvailable();

        // Validate new seats total is not less than already booked
        if (dto.getSeatsTotal() < seatsBooked) {
            throw new BusinessException(String.format(
                    "Impossible de réduire le nombre de places à %d car %d places sont déjà réservées.",
                    dto.getSeatsTotal(), seatsBooked));
        }

        ride.setVehicle(vehicle);
        ride.setDepartureCity(dto.getDepartureCity());
        ride.setDepartureAddress(dto.getDepartureAddress());
        ride.setArrivalCity(dto.getArrivalCity());
        ride.setArrivalAddress(dto.getArrivalAddress());
        ride.setDepartureDateTime(dto.getDepartureDateTime());
        ride.setDurationMinutes(dto.getDurationMinutes());
        ride.setPricePerSeat(dto.getPricePerSeat());
        ride.setSeatsTotal(dto.getSeatsTotal());
        ride.setSeatsAvailable(dto.getSeatsTotal() - seatsBooked);
        ride.setDescription(dto.getDescription());
        ride.setMusicEnabled(dto.isMusicEnabled());
        ride.setPetsAllowed(dto.isPetsAllowed());
        ride.setSmokingAllowed(dto.isSmokingAllowed());

        return rideDao.save(ride);
    }

    /**
     * Cancel a ride
     */
    @Transactional
    public void cancelRide(Long rideId, UserAccount driver) {
        Ride ride = rideDao.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Trajet", rideId));

        // Check ownership
        if (!ride.getDriver().getId().equals(driver.getId())) {
            throw new ValidationException("Vous n'êtes pas autorisé à annuler ce trajet.");
        }

        // Cannot cancel already cancelled or completed rides
        if (ride.getStatus() == RideStatus.CANCELLED) {
            throw new BusinessException("Ce trajet est déjà annulé.");
        }

        if (ride.getStatus() == RideStatus.COMPLETED) {
            throw new BusinessException("Impossible d'annuler un trajet terminé.");
        }

        ride.setStatus(RideStatus.CANCELLED);
        rideDao.save(ride);
    }

    /**
     * Mark a ride as completed
     */
    @Transactional
    public void completeRide(Long rideId, UserAccount driver) {
        Ride ride = rideDao.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Trajet", rideId));

        // Check ownership
        if (!ride.getDriver().getId().equals(driver.getId())) {
            throw new ValidationException("Vous n'êtes pas autorisé à marquer ce trajet comme terminé.");
        }

        if (ride.getStatus() == RideStatus.CANCELLED) {
            throw new BusinessException("Impossible de marquer un trajet annulé comme terminé.");
        }

        if (ride.getStatus() == RideStatus.COMPLETED) {
            throw new BusinessException("Ce trajet est déjà terminé.");
        }

        ride.setStatus(RideStatus.COMPLETED);
        rideDao.save(ride);
    }

    /**
     * Search for rides
     */
    @Transactional(readOnly = true)
    public List<Ride> searchRides(@Valid RideSearchRequestDto dto) {
        LocalDateTime start = dto.getDate().atStartOfDay();
        LocalDateTime end = dto.getDate().atTime(LocalTime.MAX);

        return rideDao.findRides(
                dto.getDepartureCity(),
                dto.getArrivalCity(),
                start,
                end
        );
    }

    /**
     * Get a ride by ID
     */
    @Transactional(readOnly = true)
    public Ride getRideById(Long rideId) {
        return rideDao.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Trajet", rideId));
    }

    /**
     * Get all rides created by a driver
     */
    @Transactional(readOnly = true)
    public List<Ride> getRidesByDriver(Long driverId) {
        return rideDao.findAll().stream()
                .filter(ride -> ride.getDriver().getId().equals(driverId))
                .toList();
    }
}

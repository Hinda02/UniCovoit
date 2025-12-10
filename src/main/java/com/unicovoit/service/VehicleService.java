package com.unicovoit.service;

import com.unicovoit.dao.VehicleDao;
import com.unicovoit.dto.VehicleDto;
import com.unicovoit.entity.UserAccount;
import com.unicovoit.entity.Vehicle;
import com.unicovoit.exception.ResourceNotFoundException;
import com.unicovoit.exception.ValidationException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class VehicleService {

    private final VehicleDao vehicleDao;

    public VehicleService(VehicleDao vehicleDao) {
        this.vehicleDao = vehicleDao;
    }

    /**
     * Create a new vehicle for a user
     */
    @Transactional
    public Vehicle createVehicle(@Valid VehicleDto dto, UserAccount owner) {
        if (owner == null) {
            throw new ValidationException("Le propriétaire du véhicule est obligatoire.");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setOwner(owner);
        vehicle.setBrand(dto.getBrand());
        vehicle.setModel(dto.getModel());
        vehicle.setColor(dto.getColor());
        vehicle.setPlateNumber(dto.getPlateNumber());
        vehicle.setSeatsTotal(dto.getSeatsTotal());

        return vehicleDao.save(vehicle);
    }

    /**
     * Update an existing vehicle
     */
    @Transactional
    public Vehicle updateVehicle(Long vehicleId, @Valid VehicleDto dto, UserAccount owner) {
        Vehicle vehicle = vehicleDao.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule", vehicleId));

        // Check ownership
        if (!vehicle.getOwner().getId().equals(owner.getId())) {
            throw new ValidationException("Vous n'êtes pas autorisé à modifier ce véhicule.");
        }

        vehicle.setBrand(dto.getBrand());
        vehicle.setModel(dto.getModel());
        vehicle.setColor(dto.getColor());
        vehicle.setPlateNumber(dto.getPlateNumber());
        vehicle.setSeatsTotal(dto.getSeatsTotal());

        return vehicleDao.save(vehicle);
    }

    /**
     * Delete a vehicle
     */
    @Transactional
    public void deleteVehicle(Long vehicleId, UserAccount owner) {
        Vehicle vehicle = vehicleDao.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule", vehicleId));

        // Check ownership
        if (!vehicle.getOwner().getId().equals(owner.getId())) {
            throw new ValidationException("Vous n'êtes pas autorisé à supprimer ce véhicule.");
        }

        vehicleDao.delete(vehicle);
    }

    /**
     * Get all vehicles for a user
     */
    @Transactional(readOnly = true)
    public List<Vehicle> getUserVehicles(Long userId) {
        return vehicleDao.findByOwnerId(userId);
    }

    /**
     * Get a vehicle by ID
     */
    @Transactional(readOnly = true)
    public Vehicle getVehicleById(Long vehicleId) {
        return vehicleDao.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule", vehicleId));
    }

    /**
     * Get a vehicle by ID and verify ownership
     */
    @Transactional(readOnly = true)
    public Vehicle getVehicleByIdAndOwner(Long vehicleId, UserAccount owner) {
        Vehicle vehicle = getVehicleById(vehicleId);

        if (!vehicle.getOwner().getId().equals(owner.getId())) {
            throw new ValidationException("Vous n'êtes pas autorisé à accéder à ce véhicule.");
        }

        return vehicle;
    }
}

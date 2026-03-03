package com.unicovoit.dao;

import com.unicovoit.entity.UserAccount;
import com.unicovoit.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleDao extends JpaRepository<Vehicle, Long> {

    /**
     * Find all vehicles owned by a specific user
     */
    List<Vehicle> findByOwner(UserAccount owner);

    /**
     * Find all vehicles by owner ID
     */
    List<Vehicle> findByOwnerId(Long ownerId);
}

package com.unicovoit.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.unicovoit.entity.Ride;

@Repository
public interface RideDao extends JpaRepository<Ride, Long> {

    @Query("""
           SELECT r
           FROM Ride r
           WHERE LOWER(r.departureCity) LIKE LOWER(CONCAT('%', :dep, '%'))
             AND LOWER(r.arrivalCity)   LIKE LOWER(CONCAT('%', :arr, '%'))
             AND r.departureDateTime BETWEEN :startDateTime AND :endDateTime
           """)
    List<Ride> findRides(
            @Param("dep") String departureCity,
            @Param("arr") String arrivalCity,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}

package com.unicovoit.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.unicovoit.dao.RideDao;
import com.unicovoit.dto.RideSearchRequestDto;
import com.unicovoit.entity.Ride;

@Service
public class RideService {

    private final RideDao rideDao;

    public RideService(RideDao rideDao) {
        this.rideDao = rideDao;
    }

    public List<Ride> searchRides(RideSearchRequestDto dto) {

        if (dto.getDepartureCity() == null || dto.getDepartureCity().isBlank() ||
            dto.getArrivalCity() == null   || dto.getArrivalCity().isBlank() ||
            dto.getDate() == null) {
            throw new IllegalArgumentException("Tous les champs de recherche sont obligatoires.");
        }

        LocalDateTime start = dto.getDate().atStartOfDay();
        LocalDateTime end   = dto.getDate().atTime(LocalTime.MAX);

        return rideDao.findRides(
                dto.getDepartureCity(),
                dto.getArrivalCity(),
                start,
                end
        );
    }
}

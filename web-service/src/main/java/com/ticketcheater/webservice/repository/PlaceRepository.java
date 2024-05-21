package com.ticketcheater.webservice.repository;

import com.ticketcheater.webservice.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findByName(String name);
    Optional<Place> findByNameAndDeletedAtIsNull(String name);
    Optional<Place> findByIdAndDeletedAtIsNull(Long placeId);
    Optional<Place> findByIdAndDeletedAtIsNotNull(Long placeId);
}

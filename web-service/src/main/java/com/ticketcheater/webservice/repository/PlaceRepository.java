package com.ticketcheater.webservice.repository;

import com.ticketcheater.webservice.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    @Query("SELECT p FROM place p WHERE p.name = :name")
    Optional<Place> findByName(@Param("name") String name);

    @Query("SELECT p FROM place p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Place> findByIdAndDeletedAtIsNull(@Param("id") Long id);

    @Query("SELECT p FROM place p WHERE p.id = :id AND p.deletedAt IS NOT NULL")
    Optional<Place> findByIdAndDeletedAtIsNotNull(@Param("id") Long id);
}

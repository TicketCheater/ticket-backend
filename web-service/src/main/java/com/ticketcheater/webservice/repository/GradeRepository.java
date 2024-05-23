package com.ticketcheater.webservice.repository;

import com.ticketcheater.webservice.entity.Grade;
import com.ticketcheater.webservice.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    @Query("SELECT g FROM grade g WHERE g.place = :place AND g.name = :name")
    Optional<Grade> findByPlaceAndName(@Param("place") Place place, @Param("name") String name);

    @Query("SELECT g FROM grade g WHERE g.id = :id AND g.deletedAt IS NULL")
    Optional<Grade> findByIdAndDeletedAtIsNull(@Param("id") Long id);

    @Query("SELECT g FROM grade g WHERE g.id = :id AND g.deletedAt IS NOT NULL")
    Optional<Grade> findByIdAndDeletedAtIsNotNull(@Param("id") Long id);
}

package com.ticketcheater.webservice.repository;

import com.ticketcheater.webservice.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findByPlaceIdAndName(Long placeId, String name);
    Optional<Grade> findByIdAndDeletedAtIsNull(Long gradeId);
    Optional<Grade> findByIdAndDeletedAtIsNotNull(Long gradeId);

}

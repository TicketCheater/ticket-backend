package com.ticketcheater.webservice.repository;

import com.ticketcheater.webservice.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {
}

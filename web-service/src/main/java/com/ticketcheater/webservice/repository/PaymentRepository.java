package com.ticketcheater.webservice.repository;

import com.ticketcheater.webservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM payment p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Payment> findByIdAndDeletedAtIsNull(@Param("id") Long id);
}

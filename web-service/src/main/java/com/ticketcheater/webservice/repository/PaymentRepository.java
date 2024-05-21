package com.ticketcheater.webservice.repository;

import com.ticketcheater.webservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}

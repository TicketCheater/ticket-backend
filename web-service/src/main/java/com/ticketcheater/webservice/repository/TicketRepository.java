package com.ticketcheater.webservice.repository;

import com.ticketcheater.webservice.entity.Ticket;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("SELECT t FROM ticket t WHERE t.id = :id AND t.deletedAt IS NULL")
    Optional<Ticket> findByIdAndDeletedAtIsNull(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT t FROM ticket t WHERE t.id = :id AND t.deletedAt IS NULL")
    Optional<Ticket> findByIdAndDeletedAtIsNullWithLock(@Param("id") Long id);
}

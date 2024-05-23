package com.ticketcheater.webservice.repository;

import com.ticketcheater.webservice.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    @Query("SELECT t FROM team t WHERE t.name = :name")
    Optional<Team> findByName(@Param("name") String name);

    @Query("SELECT t FROM team t WHERE t.id = :id AND t.deletedAt IS NULL")
    Optional<Team> findByIdAndDeletedAtIsNull(@Param("id") Long id);

    @Query("SELECT t FROM team t WHERE t.id = :id AND t.deletedAt IS NOT NULL")
    Optional<Team> findByIdAndDeletedAtIsNotNull(@Param("id") Long id);
}

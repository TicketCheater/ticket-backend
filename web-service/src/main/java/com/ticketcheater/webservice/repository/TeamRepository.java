package com.ticketcheater.webservice.repository;

import com.ticketcheater.webservice.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByNameAndDeletedAtIsNull(String name);
    Optional<Team> findByIdAndDeletedAtIsNull(Long teamId);
    Optional<Team> findByIdAndDeletedAtIsNotNull(Long teamId);
}

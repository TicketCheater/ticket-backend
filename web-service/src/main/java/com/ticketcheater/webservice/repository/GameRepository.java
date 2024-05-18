package com.ticketcheater.webservice.repository;

import com.ticketcheater.webservice.entity.Game;
import com.ticketcheater.webservice.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByIdAndDeletedAtIsNull(Long id);
    Optional<Game> findByIdAndDeletedAtIsNotNull(Long id);
    List<Game> findByHomeAndDeletedAtIsNull(Team home);
}

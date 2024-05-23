package com.ticketcheater.webservice.repository;

import com.ticketcheater.webservice.entity.Game;
import com.ticketcheater.webservice.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("SELECT g FROM game g WHERE g.id = :id AND g.deletedAt IS NULL")
    Optional<Game> findByIdAndDeletedAtIsNull(@Param("id") Long id);

    @Query("SELECT g FROM game g WHERE g.id = :id AND g.deletedAt IS NOT NULL")
    Optional<Game> findByIdAndDeletedAtIsNotNull(@Param("id") Long id);

    @Query("SELECT g FROM game g WHERE g.home = :home AND g.deletedAt IS NULL")
    List<Game> findByHomeAndDeletedAtIsNull(@Param("home") Team home);
}

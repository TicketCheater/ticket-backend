package com.ticketcheater.webservice.repository;

import com.ticketcheater.webservice.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}

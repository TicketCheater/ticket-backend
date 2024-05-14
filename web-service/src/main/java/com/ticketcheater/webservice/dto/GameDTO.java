package com.ticketcheater.webservice.dto;

import com.ticketcheater.webservice.entity.Game;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class GameDTO {

    private Long id;
    private String type;
    private String title;
    private String home;
    private String away;
    private String place;
    private Timestamp startedAt;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp removedAt;

    public static GameDTO of(String type, String title, String home, String away, String place, Timestamp startedAt) {
        return GameDTO.of(null, type, title, home, away, place, startedAt, null, null, null);
    }

    public static GameDTO of(Long id, String type, String title, String home, String away, String place, Timestamp startedAt, Timestamp createdAt, Timestamp updatedAt, Timestamp removedAt) {
        return new GameDTO(id, type, title, home, away, place, startedAt, createdAt, updatedAt, removedAt);
    }

    public static GameDTO toDTO(Game game) {
        return new GameDTO(
                game.getId(),
                game.getType().toString(),
                game.getTitle(),
                game.getHome(),
                game.getAway(),
                game.getPlace(),
                game.getStartedAt(),
                game.getCreatedAt(),
                game.getUpdatedAt(),
                game.getDeletedAt()
        );
    }

}

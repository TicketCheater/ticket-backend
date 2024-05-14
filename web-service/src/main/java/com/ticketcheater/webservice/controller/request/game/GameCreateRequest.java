package com.ticketcheater.webservice.controller.request.game;

import com.ticketcheater.webservice.dto.GameDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class GameCreateRequest {
    private String type;
    private String title;
    private String home;
    private String away;
    private String place;
    private Timestamp startedAt;

    public GameDTO toDTO() {
        return GameDTO.of(type, title, home, away, place, startedAt);
    }
}

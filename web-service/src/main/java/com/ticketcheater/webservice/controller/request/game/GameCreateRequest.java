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
    private Long homeId;
    private Long awayId;
    private Long placeId;
    private Timestamp startedAt;

    public GameDTO toDTO() {
        return GameDTO.of(type, title, homeId, awayId, placeId, startedAt);
    }
}

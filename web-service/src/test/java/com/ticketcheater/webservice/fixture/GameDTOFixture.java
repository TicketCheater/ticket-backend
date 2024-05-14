package com.ticketcheater.webservice.fixture;

import com.ticketcheater.webservice.dto.GameDTO;

import java.sql.Timestamp;

public class GameDTOFixture {

    public static GameDTO get(String type) {
        return GameDTO.of(
                type,
                "title",
                "home",
                "away",
                "place",
                new Timestamp(System.currentTimeMillis())
        );
    }

}

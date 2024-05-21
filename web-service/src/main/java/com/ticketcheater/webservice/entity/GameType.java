package com.ticketcheater.webservice.entity;

import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;

public enum GameType {

    BASEBALL,
    SOCCER,
    BASKETBALL,
    VOLLEYBALL,
    E_SPORTS;

    public static GameType fromString(String param) {
        try {
            return GameType.valueOf(param.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(ErrorCode.GAME_TYPE_NOT_FOUND);
        }
    }

}

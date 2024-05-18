package com.ticketcheater.webservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INVALID_PASSWORD(HttpStatus.FORBIDDEN, "Invalid password"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "Member not found"),
    MEMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "Duplicated member"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Expired refreshToken"),
    GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "Game not found"),
    GAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "Game already exists"),
    TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "Type not found"),
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "Team not found"),
    TEAM_ALREADY_EXISTS(HttpStatus.CONFLICT, "Team already exists"),
    INVALID_TEAM(HttpStatus.FORBIDDEN, "Invalid team"),
    ;

    private final HttpStatus status;
    private final String message;

}

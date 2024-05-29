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
    GAME_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "Type not found"),

    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "Team not found"),
    TEAM_ALREADY_EXISTS(HttpStatus.CONFLICT, "Team already exists"),
    INVALID_TEAM(HttpStatus.FORBIDDEN, "Invalid team"),

    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND,  "Place not found"),
    PLACE_ALREADY_EXISTS(HttpStatus.CONFLICT, "Place already exists"),
    INVALID_PLACE(HttpStatus.FORBIDDEN, "Invalid place"),

    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Payment not found"),
    PAYMENT_METHOD_NOT_FOUND(HttpStatus.NOT_FOUND, "Payment method not found"),

    GRADE_NOT_FOUND(HttpStatus.NOT_FOUND, "Grade not found"),
    GRADE_ALREADY_EXISTS(HttpStatus.CONFLICT, "Grade already exists"),

    INVALID_TICKET_QUANTITY(HttpStatus.FORBIDDEN, "Invalid ticket quantity"),
    INVALID_TICKET_PRICE(HttpStatus.FORBIDDEN, "Invalid ticket price"),
    TICKET_NOT_FOUND(HttpStatus.NOT_FOUND, "Ticket not found"),
    TICKET_ALREADY_BOOKED(HttpStatus.FORBIDDEN, "Ticket already booked"),

    NO_SUCH_ALGORITHM(HttpStatus.INTERNAL_SERVER_ERROR, "No such algorithm"),
    ;

    private final HttpStatus status;
    private final String message;

}

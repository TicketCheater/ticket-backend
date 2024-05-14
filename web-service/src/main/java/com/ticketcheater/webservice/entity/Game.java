package com.ticketcheater.webservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Table
@Entity(name = "game")
public class Game extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private GameType type;

    @Column(name = "title")
    private String title;

    @Column(name = "home")
    private String home;

    @Column(name = "away")
    private String away;

    @Column(name = "place")
    private String place;

    @Column(name = "started_at")
    private Timestamp startedAt;

    public static Game of(GameType type, String title, String home, String away, String place, Timestamp startedAt) {
        Game game = new Game();
        game.setType(type);
        game.setTitle(title);
        game.setHome(home);
        game.setAway(away);
        game.setPlace(place);
        game.setStartedAt(startedAt);
        return game;
    }

}

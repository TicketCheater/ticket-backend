package com.ticketcheater.webservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Table(indexes = {
        @Index(name = "idx_game_id_deletedAt", columnList = "id, deleted_at"),
        @Index(name = "idx_game_home_deletedAt", columnList = "home, deleted_at")
})
@Entity(name = "game")
public class Game extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private GameType type;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id")
    private Team home;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_id")
    private Team away;

    @Column(name = "place")
    private String place;

    @Column(name = "started_at")
    private Timestamp startedAt;

    public static Game of(GameType type, String title, Team home, Team away, String place, Timestamp startedAt) {
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

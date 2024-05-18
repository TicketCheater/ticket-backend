package com.ticketcheater.webservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(indexes = {
        @Index(name = "idx_team_id_deletedAt", columnList = "id, deleted_at"),
        @Index(name = "idx_team_name", columnList = "name", unique = true),
        @Index(name = "idx_team_name_deletedAt", columnList = "name, deleted_at")
})
@Entity(name = "team")
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    public static Team of(String name) {
        Team team = new Team();
        team.setName(name);
        return team;
    }

}

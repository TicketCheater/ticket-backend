package com.ticketcheater.webservice.dto;

import com.ticketcheater.webservice.entity.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class TeamDTO {

    private Long id;
    private String name;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static TeamDTO toDTO(Team team) {
        return new TeamDTO(
                team.getId(),
                team.getName(),
                team.getCreatedAt(),
                team.getUpdatedAt(),
                team.getDeletedAt()
        );
    }

}

package com.ticketcheater.webservice.controller.response.team;

import com.ticketcheater.webservice.dto.TeamDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamCreateResponse {
    private Long id;
    private String name;

    public static TeamCreateResponse from(TeamDTO dto) {
        return new TeamCreateResponse(dto.getId(), dto.getName());
    }
}

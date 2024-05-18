package com.ticketcheater.webservice.controller.response.team;

import com.ticketcheater.webservice.dto.TeamDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamUpdateResponse {
    private Long id;
    private String name;

    public static TeamUpdateResponse from(TeamDTO dto) {
        return new TeamUpdateResponse(dto.getId(), dto.getName());
    }
}

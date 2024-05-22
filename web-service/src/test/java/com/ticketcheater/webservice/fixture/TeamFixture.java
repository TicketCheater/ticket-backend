package com.ticketcheater.webservice.fixture;

import com.ticketcheater.webservice.entity.Team;

public class TeamFixture {

    public static Team get(Long id) {
        Team team = new Team();
        team.setId(id);
        return team;
    }

    public static Team get(String name) {
        Team team = new Team();
        team.setName(name);
        return team;
    }
}

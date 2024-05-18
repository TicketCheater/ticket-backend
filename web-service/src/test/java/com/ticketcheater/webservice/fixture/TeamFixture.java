package com.ticketcheater.webservice.fixture;

import com.ticketcheater.webservice.entity.Team;

public class TeamFixture {

    public static Team get(String name) {
        Team team = new Team();
        team.setName(name);
        return team;
    }
}

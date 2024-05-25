package com.ticketcheater.webservice.controller;

import com.ticketcheater.webservice.aop.RequireAdmin;
import com.ticketcheater.webservice.controller.request.team.TeamCreateRequest;
import com.ticketcheater.webservice.controller.request.team.TeamUpdateRequest;
import com.ticketcheater.webservice.controller.response.Response;
import com.ticketcheater.webservice.controller.response.team.TeamCreateResponse;
import com.ticketcheater.webservice.controller.response.team.TeamUpdateResponse;
import com.ticketcheater.webservice.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/web/teams/")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @RequireAdmin
    @PostMapping("/create")
    public Response<TeamCreateResponse> createTeam(@RequestBody TeamCreateRequest request) {
        return Response.success(TeamCreateResponse.from(teamService.createTeam(request.getName())));
    }

    @RequireAdmin
    @PatchMapping("/update/{teamId}")
    public Response<TeamUpdateResponse> updateTeam(
            @PathVariable Long teamId,
            @RequestBody TeamUpdateRequest request
    ) {
        return Response.success(TeamUpdateResponse.from(teamService.updateTeam(teamId, request.getName())));
    }

    @RequireAdmin
    @PatchMapping("/delete/{teamId}")
    public Response<Void> deleteTeam(@PathVariable Long teamId) {
        teamService.deleteTeam(teamId);
        return Response.success();
    }

    @RequireAdmin
    @PatchMapping("/restore/{teamId}")
    public Response<Void> restoreTeam(@PathVariable Long teamId) {
        teamService.restoreTeam(teamId);
        return Response.success();
    }

}

package com.ticketcheater.webservice.controller;

import com.ticketcheater.webservice.controller.request.team.TeamCreateRequest;
import com.ticketcheater.webservice.controller.request.team.TeamUpdateRequest;
import com.ticketcheater.webservice.controller.response.Response;
import com.ticketcheater.webservice.controller.response.team.TeamCreateResponse;
import com.ticketcheater.webservice.controller.response.team.TeamUpdateResponse;
import com.ticketcheater.webservice.jwt.JwtTokenProvider;
import com.ticketcheater.webservice.service.MemberService;
import com.ticketcheater.webservice.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/web/teams/")
@RequiredArgsConstructor
public class TeamController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TeamService teamService;

    @PostMapping("/create")
    public Response<TeamCreateResponse> createTeam(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header,
            @RequestBody TeamCreateRequest request
    ) {
        memberService.isAdmin(jwtTokenProvider.getName(header));
        return Response.success(TeamCreateResponse.from(teamService.createTeam(request.getName())));
    }

    @PatchMapping("/update/{teamId}")
    public Response<TeamUpdateResponse> updateTeam(
            @PathVariable Long teamId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header,
            @RequestBody TeamUpdateRequest request
    ) {
        memberService.isAdmin(jwtTokenProvider.getName(header));
        return Response.success(TeamUpdateResponse.from(teamService.updateTeam(teamId, request.getName())));
    }

    @PatchMapping("/delete/{teamId}")
    public Response<Void> deleteTeam(
            @PathVariable Long teamId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header
    ) {
        memberService.isAdmin(jwtTokenProvider.getName(header));
        teamService.deleteTeam(teamId);
        return Response.success();
    }

    @PatchMapping("/restore/{teamId}")
    public Response<Void> restoreTeam(
            @PathVariable Long teamId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header)
    {
        memberService.isAdmin(jwtTokenProvider.getName(header));
        teamService.restoreTeam(teamId);
        return Response.success();
    }

}

package com.ticketcheater.webservice.controller;

import com.ticketcheater.webservice.controller.request.game.GameCreateRequest;
import com.ticketcheater.webservice.controller.request.game.GameUpdateRequest;
import com.ticketcheater.webservice.controller.response.Response;
import com.ticketcheater.webservice.controller.response.game.GameCreateResponse;
import com.ticketcheater.webservice.controller.response.game.GameReadByHomeResponse;
import com.ticketcheater.webservice.controller.response.game.GameUpdateResponse;
import com.ticketcheater.webservice.jwt.JwtTokenProvider;
import com.ticketcheater.webservice.service.GameService;
import com.ticketcheater.webservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/web/games/")
@RequiredArgsConstructor
public class GameController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final GameService gameService;

    @PostMapping("/create")
    public Response<GameCreateResponse> createGame(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header,
            @RequestBody GameCreateRequest request
    ) {
        memberService.isAdmin(jwtTokenProvider.getName(header));
        return Response.success(GameCreateResponse.from(
                gameService.createGame(request.toDTO())
        ));
    }

    @GetMapping("/{teamId}")
    public Response<GameReadByHomeResponse> getGamesByHome(
            @PathVariable Long teamId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header
    ) {
        memberService.isAdmin(jwtTokenProvider.getName(header));
        return Response.success(GameReadByHomeResponse.from(teamId, gameService.getGamesByHome(teamId)));
    }

    @PatchMapping("/update/{gameId}")
    public Response<GameUpdateResponse> updateGame(
            @PathVariable Long gameId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header,
            @RequestBody GameUpdateRequest request
    ) {
        memberService.isAdmin(jwtTokenProvider.getName(header));
        return Response.success(GameUpdateResponse.from(
                gameService.updateGame(gameId, request.toDTO())
        ));
    }

    @PatchMapping("/delete/{gameId}")
    public Response<Void> deleteGame(
            @PathVariable Long gameId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header
    ) {
        memberService.isAdmin(jwtTokenProvider.getName(header));
        gameService.deleteGame(gameId);
        return Response.success();
    }

    @PatchMapping("/restore/{gameId}")
    public Response<Void> restoreGame(
            @PathVariable Long gameId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header
    ) {
        memberService.isAdmin(jwtTokenProvider.getName(header));
        gameService.restoreGame(gameId);
        return Response.success();
    }

}

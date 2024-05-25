package com.ticketcheater.webservice.controller;

import com.ticketcheater.webservice.aop.RequireAdmin;
import com.ticketcheater.webservice.controller.request.game.GameCreateRequest;
import com.ticketcheater.webservice.controller.request.game.GameUpdateRequest;
import com.ticketcheater.webservice.controller.response.Response;
import com.ticketcheater.webservice.controller.response.game.GameCreateResponse;
import com.ticketcheater.webservice.controller.response.game.GameReadByHomeResponse;
import com.ticketcheater.webservice.controller.response.game.GameUpdateResponse;
import com.ticketcheater.webservice.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/web/games/")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @RequireAdmin
    @PostMapping("/create")
    public Response<GameCreateResponse> createGame(@RequestBody GameCreateRequest request) {
        return Response.success(GameCreateResponse.from(
                gameService.createGame(request.toDTO())
        ));
    }

    @GetMapping("/{teamId}")
    public Response<GameReadByHomeResponse> getGamesByHome(@PathVariable Long teamId) {
        return Response.success(GameReadByHomeResponse.from(teamId, gameService.getGamesByHome(teamId)));
    }

    @RequireAdmin
    @PatchMapping("/update/{gameId}")
    public Response<GameUpdateResponse> updateGame(
            @PathVariable Long gameId,
            @RequestBody GameUpdateRequest request
    ) {
        return Response.success(GameUpdateResponse.from(
                gameService.updateGame(gameId, request.toDTO())
        ));
    }

    @RequireAdmin
    @PatchMapping("/delete/{gameId}")
    public Response<Void> deleteGame(@PathVariable Long gameId) {
        gameService.deleteGame(gameId);
        return Response.success();
    }

    @RequireAdmin
    @PatchMapping("/restore/{gameId}")
    public Response<Void> restoreGame(@PathVariable Long gameId) {
        gameService.restoreGame(gameId);
        return Response.success();
    }

}

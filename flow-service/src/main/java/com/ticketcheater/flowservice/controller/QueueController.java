package com.ticketcheater.flowservice.controller;

import com.ticketcheater.flowservice.controller.response.RankResponse;
import com.ticketcheater.flowservice.controller.response.Response;
import com.ticketcheater.flowservice.controller.response.TokenResponse;
import com.ticketcheater.flowservice.service.QueueService;
import com.ticketcheater.flowservice.util.JwtTokenParser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/v1/flow/")
@RequiredArgsConstructor
public class QueueController {

    private final JwtTokenParser tokenParser;
    private final QueueService queueService;

    @PostMapping("register/{gameId}")
    public Mono<Response<RankResponse>> registerQueue(
            @PathVariable("gameId") String gameId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header
    ) {
        String name = tokenParser.getName(header);
        return queueService.registerWaitQueue(gameId, name)
                .flatMap(rank -> Response.success(new RankResponse(rank)));
    }

    @GetMapping("/{gameId}")
    public Mono<Response<?>> getInfo(
            @PathVariable("gameId") String gameId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header,
            ServerWebExchange exchange
    ) {
        String name = tokenParser.getName(header);
        return queueService.processMember(gameId, name)
                .flatMap(token -> {
                    if (!token.isEmpty()) {
                        exchange.getResponse().addCookie(
                                ResponseCookie.from("member-queue-%s-token".formatted(gameId), token)
                                        .maxAge(Duration.ofSeconds(300))
                                        .path("/")
                                        .build()
                        );
                        return Response.success(new TokenResponse(token));
                    } else {
                        return queueService.getRank(gameId, name)
                                .flatMap(rank -> Response.success(new RankResponse(rank)));
                    }
                });
    }

}

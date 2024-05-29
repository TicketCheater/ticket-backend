package com.ticketcheater.flowservice.controller;

import com.ticketcheater.flowservice.controller.response.RankResponse;
import com.ticketcheater.flowservice.controller.response.TokenResponse;
import com.ticketcheater.flowservice.exception.ErrorCode;
import com.ticketcheater.flowservice.exception.FlowApplicationException;
import com.ticketcheater.flowservice.service.QueueService;
import com.ticketcheater.flowservice.util.JwtTokenParser;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(QueueController.class)
@DisplayName("Controller - 대기열")
class QueueControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    JwtTokenParser tokenParser;

    @MockBean
    QueueService queueService;

    @DisplayName("대기열 등록 정상 동작")
    @Test
    void givenGameIdAndName_whenRegister_thenRegisterQueue() {
        String gameId = "1";
        String token = "token";

        when(tokenParser.getName(anyString())).thenReturn("name");
        when(queueService.registerWaitQueue(anyString(), anyString())).thenReturn(Mono.just(1L));

        webTestClient.post().uri("/v1/flow/register/" + gameId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @DisplayName("이미 등록된 이름으로 대기열 등록 시 오류 발생")
    @Test
    void givenAlreadyRegisteredName_whenRegister_thenThrowsError() {
        String gameId = "1";
        String token = "token";

        when(tokenParser.getName(anyString())).thenReturn("name");
        when(queueService.registerWaitQueue(anyString(), anyString())).thenThrow(new FlowApplicationException(ErrorCode.ALREADY_REGISTERED_MEMBER));

        webTestClient.post().uri("/v1/flow/register/" + gameId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(ErrorCode.ALREADY_REGISTERED_MEMBER.getStatus().value());
    }

    @DisplayName("대기열 정보 조회 - 토큰 발급")
    @Test
    void givenGameIdAndName_whenGetInfo_thenTokenIssued() {
        String gameId = "1";
        String token = "token";
        String name = "name";

        when(tokenParser.getName(anyString())).thenReturn(name);
        when(queueService.processMember(anyString(), anyString())).thenReturn(Mono.just(token));

        webTestClient.get().uri("/v1/flow/" + gameId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenResponse.class)
                .consumeWith(response -> {
                    TokenResponse tokenResponse = response.getResponseBody();
                    assertThat(tokenResponse).isNotNull();
                });
    }

    @DisplayName("대기열 정보 조회 - 랭킹 조회")
    @Test
    void givenGameIdAndName_whenGetInfo_thenRankingRetrieved() {
        String gameId = "1";
        String token = "token";
        String name = "name";
        long rank = 1L;

        when(tokenParser.getName(anyString())).thenReturn(name);
        when(queueService.processMember(anyString(), anyString())).thenReturn(Mono.just(""));
        when(queueService.getRank(anyString(), anyString())).thenReturn(Mono.just(rank));

        webTestClient.get().uri("/v1/flow/" + gameId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RankResponse.class)
                .consumeWith(response -> {
                    RankResponse rankResponse = response.getResponseBody();
                    assertThat(rankResponse).isNotNull();
                });
    }

}

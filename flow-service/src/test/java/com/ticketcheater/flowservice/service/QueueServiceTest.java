package com.ticketcheater.flowservice.service;

import com.ticketcheater.flowservice.config.TestContainerConfig;
import com.ticketcheater.flowservice.exception.FlowApplicationException;
import com.ticketcheater.flowservice.util.TokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.test.StepVerifier;

@Disabled("Redis 연결 여부에 따라 테스트가 실패할 수 있으므로 필요 시마다 따로 테스트한다.")
@DisplayName("Business Logic - 대기열")
@ExtendWith(TestContainerConfig.class)
@SpringBootTest
class QueueServiceTest {

    @Autowired
    QueueService sut;

    @Autowired
    ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @BeforeEach
    void beforeEach() {
        ReactiveRedisConnection redisConnection = reactiveRedisTemplate.getConnectionFactory().getReactiveConnection();
        redisConnection.serverCommands().flushAll().subscribe();
    }

    @DisplayName("대기열 등록 정상 동작")
    @Test
    void givenGameIdAndName_whenRegister_thenRegisterQueue() {
        StepVerifier.create(sut.registerWaitQueue("1", "name"))
                .expectNext(1L)
                .verifyComplete();
    }

    @DisplayName("이미 등록된 이름으로 대기열 등록 시 오류 발생")
    @Test
    void givenAlreadyRegisteredName_whenRegister_thenThrowsError() {
        StepVerifier.create(sut.registerWaitQueue("1", "name"))
                .expectNext(1L)
                .verifyComplete();

        StepVerifier.create(sut.registerWaitQueue("1", "name"))
                .expectError(FlowApplicationException.class)
                .verify();
    }

    @DisplayName("대기열 순위 조회 정상 동작")
    @Test
    void givenGameIdAndName_whenGetRank_thenReturnRank() {
        StepVerifier.create(sut.registerWaitQueue("1", "name"))
                .expectNext(1L)
                .verifyComplete();

        StepVerifier.create(sut.getRank("1", "name"))
                .expectNext(1L)
                .verifyComplete();
    }

    @DisplayName("등록되지 않은 이름으로 대기열 순위 조회 시 -1 반환")
    @Test
    void givenNotRegisteredName_whenGetRank_thenReturnsMinusOne() {
        StepVerifier.create(sut.getRank("1", "name"))
                .expectNext(-1L)
                .verifyComplete();
    }

    @DisplayName("대기열 허용 정상 동작")
    @Test
    void givenGameId_whenAllow_thenAllowMember() {
        StepVerifier.create(sut.registerWaitQueue("1", "name"))
                .expectNext(1L)
                .verifyComplete();

        StepVerifier.create(sut.allowMember("1"))
                .expectNext(1L)
                .verifyComplete();
    }

    @DisplayName("빈 대기열을 허용할 경우 0 반환")
    @Test
    void givenEmptyQueue_whenAllow_thenReturnsZero() {
        StepVerifier.create(sut.allowMember("1"))
                .expectNext(0L)
                .verifyComplete();
    }

    @DisplayName("토큰 지급 정상 동작")
    @Test
    void givenGameIdAndName_whenProcess_thenGenerateToken() {
        StepVerifier.create(sut.registerWaitQueue("1", "name"))
                .expectNext(1L)
                .verifyComplete();

        StepVerifier.create(sut.allowMember("1"))
                .expectNext(1L)
                .verifyComplete();

        StepVerifier.create(sut.processMember("1", "name"))
                .expectNext(TokenGenerator.generateToken("1", "name"))
                .verifyComplete();
    }

    @DisplayName("등록되지 않은 이름으로 처리 시 빈 값 반환")
    @Test
    void givenNotRegisteredName_whenProcess_thenReturnsEmpty() {
        StepVerifier.create(sut.processMember("1", "name"))
                .expectNext()
                .verifyComplete();
    }

}

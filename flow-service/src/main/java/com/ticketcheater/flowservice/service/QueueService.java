package com.ticketcheater.flowservice.service;

import com.ticketcheater.flowservice.exception.ErrorCode;
import com.ticketcheater.flowservice.exception.FlowApplicationException;
import com.ticketcheater.flowservice.util.TokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    private static final String MEMBERS_QUEUE_WAIT_KEY = "members:queue:%s:wait";
    private static final String MEMBERS_QUEUE_SCAN = "members:queue:*:wait";
    private static final String MEMBER_QUEUE_PROCEED_KEY = "members:queue:%s:proceed";
    private static final Long ALLOW_COUNT = 100L;

    @Value("${scheduler.enabled}")
    private Boolean scheduling;

    public Mono<Long> registerWaitQueue(String gameId, String name) {
        long timestamp = System.currentTimeMillis();
        return reactiveRedisTemplate.opsForZSet().add(MEMBERS_QUEUE_WAIT_KEY.formatted(gameId), name, timestamp)
                .filter(i -> i)
                .switchIfEmpty(Mono.error(() -> new FlowApplicationException(ErrorCode.ALREADY_REGISTERED_MEMBER, String.format("%s is already registered", name))))
                .flatMap(i -> reactiveRedisTemplate.opsForZSet().rank(MEMBERS_QUEUE_WAIT_KEY.formatted(gameId), name))
                .map(i -> i>=0 ? i+1 : i);
    }

    public Mono<Long> getRank(String gameId, String name) {
        return reactiveRedisTemplate.opsForZSet().rank(MEMBERS_QUEUE_WAIT_KEY.formatted(gameId), name)
                .defaultIfEmpty(-1L)
                .map(i -> i>=0 ? i+1 : i);
    }

    public Mono<Long> allowMember(String gameId) {
        return reactiveRedisTemplate.opsForZSet().popMin(MEMBERS_QUEUE_WAIT_KEY.formatted(gameId), ALLOW_COUNT)
                .flatMap(member -> {
                    if (member == null || member.getValue() == null) {
                        log.warn("Member or its value is null.");
                        return Mono.just(0L);
                    }
                    return reactiveRedisTemplate.opsForSet().add(MEMBER_QUEUE_PROCEED_KEY.formatted(gameId), member.getValue());
                })
                .count();
    }

    public Mono<String> processMember(String gameId, String name) {
        return reactiveRedisTemplate.opsForSet().remove(MEMBER_QUEUE_PROCEED_KEY.formatted(gameId), name)
                .flatMap(count -> {
                    if(count == 1) {
                        return Mono.just(TokenGenerator.generateToken(gameId, name));
                    } else {
                        return Mono.just("");
                    }
                });
    }

    @Scheduled(initialDelay = 5000, fixedDelay = 10000)
    public void scheduleAllowMember() {
        if(!scheduling) {
            log.info("passed scheduling...");
            return;
        }
        log.info("called scheduling...");

        reactiveRedisTemplate.scan(ScanOptions.scanOptions()
                .match(MEMBERS_QUEUE_SCAN).count(ALLOW_COUNT).build())
                .flatMap(this::extractGameIdAndAllowMember)
                .subscribe();
    }

    private Mono<Long> extractGameIdAndAllowMember(String key) {
        String gameId = key.split(":")[2];
        log.info("Allowed members of %s queue".formatted(gameId));
        return allowMember(gameId);
    }

}

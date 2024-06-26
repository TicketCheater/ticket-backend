package com.ticketcheater.webservice.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class TestContainerConfig implements BeforeAllCallback {

    private static final String REDIS_IMAGE = "redis";
    private static final int REDIS_PORT = 6379;
    private static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE))
            .withExposedPorts(REDIS_PORT);

    @Override
    public void beforeAll(ExtensionContext context) {
        redis.start();
        System.setProperty("spring.data.redis.host", redis.getHost());
        System.setProperty("spring.data.redis.port", String.valueOf(redis.getMappedPort(REDIS_PORT)));
    }

}

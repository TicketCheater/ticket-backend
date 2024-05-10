package com.ticketcheater.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Objects;

@Slf4j
@Component
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            logRequest(exchange);

            return chain.filter(exchange).then(Mono.fromRunnable(() -> logResponse(exchange)));
        };
    }

    private void logRequest(ServerWebExchange exchange) {
        String httpMethod = exchange.getRequest().getMethod().name();
        String requestPath = exchange.getRequest().getPath().toString();
        log.info("### Incoming request {} {} {}", new Date(), httpMethod, requestPath);
    }

    private void logResponse(ServerWebExchange exchange) {
        Integer statusCode = Objects.requireNonNull(exchange.getResponse().getStatusCode()).value();
        log.info("### Response with status code {}", statusCode);
    }

    public static class Config {}

}

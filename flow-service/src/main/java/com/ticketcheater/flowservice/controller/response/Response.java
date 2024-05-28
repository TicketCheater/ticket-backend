package com.ticketcheater.flowservice.controller.response;

import reactor.core.publisher.Mono;

public record Response<T>(String resultCode, T result) {

    public static <T> Mono<Response<T>> of(String resultCode, T result) {
        return Mono.just(new Response<>(resultCode, result));
    }

    public static <T> Mono<Response<T>> success(T result) {
        return Response.of("Success", result);
    }

    public static Mono<Response<Void>> error(String resultCode) {
        return Response.of(resultCode, null);
    }

}

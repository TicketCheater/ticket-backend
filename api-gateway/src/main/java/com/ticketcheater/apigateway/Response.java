package com.ticketcheater.apigateway;

public record Response<T>(String resultCode, T result) {

    public static <T> Response<T> of(String resultCode, T result) {
        return new Response<>(resultCode, result);
    }

    public static Response<Void> error(String resultCode) {
        return Response.of(resultCode, null);
    }

}

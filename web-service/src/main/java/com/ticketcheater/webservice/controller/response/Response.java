package com.ticketcheater.webservice.controller.response;

public record Response<T>(String resultCode, T result) {

    public static <T> Response<T> of(String resultCode, T result) {
        return new Response<>(resultCode, result);
    }

    public static <T> Response<T> success() {
        return Response.of("SUCCESS", null);
    }

    public static <T> Response<T> success(T result) {
        return Response.of("SUCCESS", result);
    }

    public static Response<Void> error(String resultCode) {
        return Response.of(resultCode, null);
    }

    public String toStream() {
        if (result == null) {
            return "{" +
                    "\"resultCode\":" + "\"" + resultCode + "\"," +
                    "\"result\":" + null +
                    "}";
        }
        return "{" +
                "\"resultCode\":" + "\"" + resultCode + "\"," +
                "\"result\":" + "\"" + result + "\"," +
                "}";
    }

}

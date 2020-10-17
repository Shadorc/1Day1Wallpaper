package com.shadorc.onedayonewallpaper.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.shadorc.onedayonewallpaper.data.Config;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufMono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;

import java.io.IOException;

public final class NetUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .enable(SerializationFeature.INDENT_OUTPUT);

    private static final HttpClient HTTP_CLIENT = HttpClient.create();

    private NetUtils() {
    }

    public static <T> Mono<T> get(final String url, final Class<? extends T> type) {
        return HTTP_CLIENT
                .request(HttpMethod.GET)
                .uri(url)
                .<T>responseSingle((resp, body) -> NetUtils.handleResponse(resp, body, type))
                .timeout(Config.DEFAULT_TIMEOUT);
    }

    private static <T> Mono<T> handleResponse(final HttpClientResponse resp, final ByteBufMono body, final Class<? extends T> type) {
        final int statusCode = resp.status().code();
        if (statusCode / 100 != 2) {
            return body.asString()
                    .flatMap(err -> Mono.error(new IOException(String.format("%s %s failed (%d) %s",
                            resp.method().asciiName(), resp.uri(), statusCode, err))));
        }
        if (!resp.responseHeaders().get(HttpHeaderNames.CONTENT_TYPE).startsWith(HttpHeaderValues.APPLICATION_JSON.toString())) {
            return body.asString()
                    .flatMap(err -> Mono.error(new IOException(String.format("%s %s wrong header (%s) %s",
                            resp.method().asciiName(), resp.uri(), resp.responseHeaders().get(HttpHeaderNames.CONTENT_TYPE), err))));
        }

        return body.asInputStream()
                .flatMap(input -> Mono.fromCallable(() -> MAPPER.readValue(input, type)));
    }
}


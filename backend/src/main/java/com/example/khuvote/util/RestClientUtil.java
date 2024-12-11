package com.example.khuvote.util;

import com.example.khuvote.config.RestClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class RestClientUtil {

    private final RestClientConfig restClientConfig;

    public <V> V get(String url, MediaType requestContentType, Class<V> responseDtoClass){
        return getRequest(url)
                .contentType(requestContentType)
                .retrieve()
                .body(responseDtoClass);
    }

    public <V> V get(String url, Class<V> responseDtoClass) {
        return getRequest(url)
                .retrieve()
                .body(responseDtoClass);
    }

    public <T, V> T post(String url, MediaType requestContentType, V requestDto, Class<T> responseDtoClass){
        return postRequest(url, requestDto)
                .contentType(requestContentType)
                .retrieve()
                .body(responseDtoClass);
    }

    public <T, V> T post(String url, V requestDto, Class<T> responseDtoClass) {
        return postRequest(url, requestDto)
                .retrieve()
                .body(responseDtoClass);
    }

    private RestClient.RequestBodySpec getRequest(String url) {
        return restClientConfig.restClient().method(HttpMethod.GET)
                .uri(url)
                .accept(MediaType.APPLICATION_JSON);
    }

    private <V> RestClient.RequestBodySpec postRequest(String url, V requestDto) {
        return restClientConfig.restClient().method(HttpMethod.POST)
                .uri(url)
                .body(requestDto)
                .accept(MediaType.APPLICATION_JSON);
    }
}
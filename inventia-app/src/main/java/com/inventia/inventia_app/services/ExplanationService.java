package com.inventia.inventia_app.services;


import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;

/**
 * ExplanationService
 */
@Service
public class ExplanationService {

    @Value("${third.party.model.server.url}")
    private static String URL_BASE;

    private static final String URL_ROUTE = URL_BASE + "/explain";

    private WebClient webClient = WebClient.create(URL_ROUTE);

    @Autowired
    public ExplanationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(URL_ROUTE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Flux<String> explain() {
        return webClient.post().uri("/explain").retrieve().bodyToFlux(String.class);
    }
}

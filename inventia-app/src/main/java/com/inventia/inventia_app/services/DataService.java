package com.inventia.inventia_app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * DataService
 */
@Service
public class DataService {

    // @Value("${third.party.model.server.url}")
    private static String URL_BASE = "http://localhost:8000";

    private static String URL_ROUTE = URL_BASE + "/upload";

    private WebClient webClient = WebClient.create(URL_ROUTE);

    @Autowired
    public DataService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(URL_ROUTE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<String> upload(MultipartFile file) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", file.getResource()).filename(file.getOriginalFilename());
        return webClient.post().uri("/").body(publisher, elementClass).retrieve().bodyToMono(String.class);
    }

}

package com.inventia.inventia_app.services;

import com.inventia.inventia_app.entities.PredictionGroup;
import com.inventia.inventia_app.entities.PredictionSingle;
import com.inventia.inventia_app.entities.Product;

import reactor.core.publisher.Flux;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * PredictionService
 */
@Service
public class PredictionService {

    private static String URL_BASE = "http://localhost:8000";

    private static String URL_ROUTE = URL_BASE + "/predict";

    private WebClient webClient = WebClient.create(URL_ROUTE);

    @Autowired
    public PredictionService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl(URL_ROUTE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    public Flux<String> predict() {
        return webClient.get().uri("/run").retrieve().bodyToFlux(String.class);
    }

    public Flux<PredictionSingle> predictSingle(Product product)  {
        return webClient.post().uri("/by-product") .bodyValue(product).retrieve()
            .bodyToFlux(PredictionSingle.class);
    }

    public Flux<PredictionGroup> predictGroup(Product product) {
        return webClient.post().uri("/all-products").bodyValue(product).retrieve()
            .bodyToFlux(PredictionGroup.class);
    }
}

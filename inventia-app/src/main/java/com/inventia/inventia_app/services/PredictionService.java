package com.inventia.inventia_app.services;

import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventia.inventia_app.entities.PredictionResponse;
import com.inventia.inventia_app.entities.Product;

import reactor.core.publisher.Flux;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * PredictionService
 */
@Service
public class PredictionService {

    private static final String URL_BASE = "http://localhost:8000/predict";
    private WebClient webClient = WebClient.create(URL_BASE);

    @Autowired
    public PredictionService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(URL_BASE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Flux<String> predict(){
        return webClient.get().uri("/run").retrieve().bodyToFlux(String.class);
    }

    public Flux<PredictionResponse> predictSingle(Product product){
        return webClient.post().uri("/by-product").bodyValue(product).retrieve().bodyToFlux(PredictionResponse.class);
    }
}

package com.inventia.inventia_app.services;

import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public Flux<String> predictSingle(Product product){
        String jsonString = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(product);
            System.out.println("Json creado manualmente: " + jsonString);
        } catch (Exception e) {
            // TODO: handle exception
            System.err.println("Error processing JSON: " + e.getMessage());
            e.printStackTrace();
        }
        //return webClient.post().uri("/by-product").bodyValue("{ \"product_id\": 8, \"fecha\": \"2024-07-01\"}").retrieve().bodyToFlux(String.class);
        return webClient.post().uri("/by-product").bodyValue(jsonString).retrieve().bodyToFlux(String.class);
    }
}

package com.inventia.inventia_app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

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
                .build();
    }

    public Mono<String> upload(MultipartFile file) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", file.getResource()).filename(file.getOriginalFilename());
        //System.out.println("Llamando al servicio para entrenar el modelo: " + URL_ROUTE);
        return webClient.post()
            .uri("/data")
            .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
            .retrieve()
            .bodyToMono(String.class)
            .onErrorResume(e -> {
                System.err.println("Error al reenviar el archivo a FastAPI: " + e.getMessage());
                return Mono.error(new RuntimeException("No se pudo reenviar el archivo al servicio de destino.", e));
            }
        );
    }

}

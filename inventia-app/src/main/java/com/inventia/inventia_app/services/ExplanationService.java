package com.inventia.inventia_app.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class ExplanationService {

    private String URL_BASE = "https://api.openai.com/v1/chat/completions";

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private WebClient webClient;

    @Autowired
    public ExplanationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(URL_BASE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<String> askOpenAI(String userMessage) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "You are an assistant for InventIA."));
        messages.add(Map.of("role", "user", "content", userMessage));
        requestBody.put("messages", messages);

        return webClient
                .post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openAiApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(responseMap -> {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, String> message = (Map<String, String>) firstChoice.get("message");
                    return message.get("content");
                });
    }
}

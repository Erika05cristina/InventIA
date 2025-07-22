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

import com.inventia.inventia_app.entities.PredictionSingle; 

import reactor.core.publisher.Mono; 

@Service
public class ExplanationService {

    @Value("${third.party.model.server.url}")
    private String URL_BASE;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private WebClient webClient;

    @Autowired
    public ExplanationService(WebClient.Builder webClientBuilder, @Value("${third.party.model.server.url}") String urlBase) {
        this.webClient = webClientBuilder
                .baseUrl(urlBase)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

public Mono<Map<String, Object>> generarExplicacionEnriquecida(PredictionSingle prediction) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("product_id", prediction.getPrediccion().getProductId());
    payload.put("fecha_prediccion", prediction.getPrediccion().getFecha());

    return webClient.post()
        .uri("/predict/by-product")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(payload)
        .retrieve()
        .bodyToMono(Map.class)
        .flatMap(explicacionPython -> {
            String explicacionSimple = (String) explicacionPython.get("explicacion_simple");
            Map<String, Object> avanzada = (Map<String, Object>) explicacionPython.get("explicacion_avanzada");
            Integer productId = (Integer) avanzada.get("producto_id");
            Double prediccionValue = (Double) avanzada.get("prediccion");
            List<List<Object>> variablesImportantes = (List<List<Object>>) avanzada.get("variables_importantes");
            String graficaBase64 = (String) avanzada.get("grafica_explicabilidad_base64");

            // 🔔 Prompt mejorado para que OpenAI responda de manera profesional y directa
            StringBuilder sb = new StringBuilder();
            sb.append("El sistema ha generado una predicción con la siguiente información:\n");
            sb.append("- Producto ID: ").append(productId).append("\n");
            sb.append("- Predicción esperada: ").append(prediccionValue).append(" unidades\n");
            sb.append("- Explicación simple: ").append(explicacionSimple).append("\n");
            sb.append("- Variables más influyentes:\n");
            for (List<Object> var : variablesImportantes) {
                sb.append("   • ").append(var.get(0)).append(" con peso ").append(var.get(1)).append("\n");
            }
            sb.append("\nEscribe un resumen profesional y conciso para un usuario de negocio, presentando directamente las conclusiones, sin frases como 'puedo explicar' o 'el sistema ha generado'.");

            String prompt = sb.toString();

            return askOpenAI(prompt).map(explicacionOpenAI -> {
                Map<String, Object> response = new HashMap<>();
                response.put("explicacionSimple", explicacionSimple);
                response.put("variablesImportantes", variablesImportantes);
                response.put("graficaBase64", graficaBase64);
                response.put("explicacionOpenAI", explicacionOpenAI);
                response.put("prediccion", prediccionValue);  // 🔥 Incluye predicción real en la respuesta
                return response;
            });
        });
}


    public Mono<String> askOpenAI(String userMessage) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "You are an assistant for InventIA."));
        messages.add(Map.of("role", "user", "content", userMessage));
        requestBody.put("messages", messages);

        return WebClient.create("https://api.openai.com/v1/chat/completions")
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

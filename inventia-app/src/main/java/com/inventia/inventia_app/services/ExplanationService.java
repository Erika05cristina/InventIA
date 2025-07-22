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

import com.inventia.inventia_app.entities.ExplicacionAvanzada;
import com.inventia.inventia_app.entities.PredictionSingle; 

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono; 

@Service
public class ExplanationService {

    @Value("${third.party.model.server.url}")
    private String URL_BASE;

    private final String URL_ROUTE = URL_BASE + "/explain";

    // private WebClient webClient = WebClient.create(URL_ROUTE);

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.api.url}")
    private String openAiApiUrl;
    private WebClient webClient;
    private String urlRoute;

    @Autowired
    public ExplanationService(WebClient.Builder webClientBuilder, @Value("${third.party.model.server.url}") String urlBase) {
        this.urlRoute = urlBase + "/explain";
        this.webClient = webClientBuilder
                .baseUrl(this.urlRoute)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Flux<String> explain() {
        return webClient.post().uri("/explain").retrieve().bodyToFlux(String.class);
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


    public Mono<String> askOpenAIFromPrediction(PredictionSingle pred) {
        String prompt = buildPromptFromPrediction(pred);
        return askOpenAI(prompt);
    }
    public Mono<String> askOpenAIFromExplanation(Map<String, Object> explicabilidad) {
        // 🔍 Extraer la explicación avanzada
        Map<String, Object> avanzada = (Map<String, Object>) explicabilidad.get("explicacion_avanzada");
        String explicacionSimple = (String) explicabilidad.get("explicacion_simple");

        Integer productId = (Integer) avanzada.get("producto_id");
        Double prediccion = (Double) avanzada.get("prediccion");

        List<List<Object>> variables = (List<List<Object>>) avanzada.get("variables_importantes");

        // 🧠 Construir el prompt
        StringBuilder sb = new StringBuilder();
        sb.append("El sistema ha generado una predicción con la siguiente información:\n");
        sb.append("- Producto ID: ").append(productId).append("\n");
        sb.append("- Predicción esperada: ").append(prediccion).append(" unidades\n");
        sb.append("- Explicación simple: ").append(explicacionSimple).append("\n");
        sb.append("- Variables más influyentes:\n");
        for (List<Object> var : variables) {
            sb.append("   • ").append(var.get(0)).append(" con peso ").append(var.get(1)).append("\n");
        }
        sb.append("\nPregunta: ¿Explica esta predicción de manera comprensible para un usuario?");

        // ✉️ Enviar prompt a OpenAI
        return askOpenAI(sb.toString());
    }

    private String buildPromptFromPrediction(PredictionSingle pred) {
        StringBuilder sb = new StringBuilder();
        sb.append("Explicación de predicción de inventario:\n");
        sb.append("- Estado del modelo: ").append(pred.getStatus()).append("\n");
        sb.append("- Explicación simple: ").append(pred.getSimple()).append("\n");
                ExplicacionAvanzada avanzada = pred.getExplicacionAvanzada();
        if (avanzada != null) {
            sb.append("- Producto ID: ").append(avanzada.getProductId()).append("\n");
            sb.append("- Predicción esperada: ").append(avanzada.getPrediccion()).append(" unidades\n");
            sb.append("- Variables más influyentes:\n");
            if (avanzada.getImportantes() != null) {
                for (List<Object> var : avanzada.getImportantes()) {
                    sb.append("   • ").append(var.get(0)).append(" con peso ").append(var.get(1)).append("\n");
                }
            }
        }

        sb.append("\nPregunta: ¿Explica esta predicción a un usuario de negocio de forma comprensible?");
        return sb.toString();

    }
    public Mono<Map<String, Object>> generarExplicacionEnriquecida(PredictionSingle prediction) {
        String explicacionSimple = prediction.getSimple();

        ExplicacionAvanzada avanzada = prediction.getExplicacionAvanzada();
        List<List<Object>> variablesImportantes = avanzada != null ? avanzada.getImportantes() : null;

        String graficaBase64 = generarGraficaBase64(prediction);

        String prompt = buildPromptFromPrediction(prediction);

        return askOpenAI(prompt).map(explicacionOpenAI -> {
            Map<String, Object> response = new HashMap<>();
            response.put("explicacionSimple", explicacionSimple);
            response.put("variablesImportantes", variablesImportantes);
            response.put("graficaBase64", graficaBase64);
            response.put("explicacionOpenAI", explicacionOpenAI);
            return response;
        });
    }

    private String generarGraficaBase64(PredictionSingle prediction) {
        return "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA...";
    }

}
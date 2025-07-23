package com.inventia.inventia_app.services;

import com.inventia.inventia_app.entities.PrediccionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.inventia.inventia_app.entities.PredictionGroup;
import com.inventia.inventia_app.entities.PredictionSingle;
import com.inventia.inventia_app.entities.Product;
import com.inventia.inventia_app.repositories.PredictionRepository;

import reactor.core.publisher.Flux;
import java.util.Date;
import java.util.List;

/**
 * PredictionService
 */
@Service
public class PredictionService {

    private static String URL_BASE = "http://localhost:8000";

    private static String URL_ROUTE = URL_BASE + "/predict";

    private WebClient webClient = WebClient.create(URL_ROUTE);

    @Autowired
    private PredictionRepository predictionRepository;

    @Autowired
    private ExplanationService explanationService;

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

    public Flux<PredictionSingle> predictSingle(int product_id, String fecha_prediccion)  {
        Product product = new Product(product_id, fecha_prediccion);
        Flux<PredictionSingle> prediccion =  webClient.post().uri("/by-product").bodyValue(product).retrieve()
            .bodyToFlux(PredictionSingle.class)
            .flatMap(prediction -> {
                PrediccionDTO pred = new PrediccionDTO(new Date(), fecha_prediccion, "individual" , prediction.toString());
                System.out.println("Prediccion guardada en la base de datos: " + pred);
                predictionRepository.save(pred);
                System.out.println("Prediccion guardada en la base de datos: " + pred);

                String explicacionSimple = prediction.getSimple();
                Double prediccionValue = prediction.getPrediccionValue();
                List<List<Object>> variablesImportantes = prediction.getVariablesImportantes();

                Long prediccionRedondeada = Math.round(prediccionValue);
                StringBuilder sb = new StringBuilder();
                sb.append("El sistema ha generado una predicción con la siguiente información:\n");
                sb.append("- Producto ID: ").append(product_id).append("\n");
                sb.append("- Predicción esperada: ").append(prediccionRedondeada).append(" unidades\n");
                sb.append("- Explicación simple: ").append(explicacionSimple).append("\n");
                sb.append("- Variables más influyentes:\n");
                for (List<Object> var : variablesImportantes) {
                    sb.append("   • ").append(var.get(0)).append(" con peso ").append(var.get(1)).append("\n");
                }
                sb.append("\nEscribe un resumen profesional y conciso para un usuario de negocio, presentando directamente las conclusiones, sin frases como 'puedo explicar' o 'el sistema ha generado'.");

                String prompt = sb.toString();
                return explanationService.askOpenAI(prompt)
                    .map(explicacionOpenAI -> {
                        prediction.setExplicacionOpenAi(explicacionOpenAI);
                        return prediction;
                    })
                    .doOnNext(updatedPrediction -> {
                        // Guardar después de obtener la explicación
                        predictionRepository.save(pred);
                        System.out.println("Predicción guardada con explicación");
                    });
            })
            .doOnError(error -> {
                System.out.println("Error al predecir: " + error);
            })
            .doOnComplete(() -> {
                System.out.println("Finalizado");
            }
        );
        return prediccion;
    }

    public Flux<PredictionGroup> predictGroup(String fecha_prediccion) {
        Product product = new Product(0, fecha_prediccion);
        Flux<PredictionGroup> prediccion =  webClient.post().uri("/by-product").bodyValue(product).retrieve()
            .bodyToFlux(PredictionGroup.class);
        prediccion.subscribe(
            prediction -> {
                PrediccionDTO pred = new PrediccionDTO(new Date(), fecha_prediccion, "grupo" , prediction.toString());
                pred.setPrediccionId(0);
                predictionRepository.save(pred);
                System.out.println("Prediccion guardada en la base de datos: " + pred);
            },
            error -> {
                System.out.println("Error al predecir: " + error);
            },
            () -> {
                System.out.println("Finalizado");
            }
        );
        return prediccion;
    }
}

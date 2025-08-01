package com.inventia.inventia_app.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventia.inventia_app.entities.PrediccionDTO;
import com.inventia.inventia_app.entities.PredictionGroup;
import com.inventia.inventia_app.entities.PredictionSingle;
import com.inventia.inventia_app.entities.PredictionRequest;
import com.inventia.inventia_app.entities.ProductosDTO;
import com.inventia.inventia_app.repositories.PredictionRepository;
import com.inventia.inventia_app.repositories.ProductoRepository;
import com.inventia.inventia_app.repositories.ProductoRepository.ProductProjection;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    private ProductoRepository productoRepository;

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
        PredictionRequest request = new PredictionRequest(product_id, fecha_prediccion);
        Flux<PredictionSingle> prediccion =  webClient.post().uri("/by-product").bodyValue(request).retrieve()
            .bodyToFlux(PredictionSingle.class)
            .flatMap(prediction -> {
            try {


                String explicacionSimple = prediction.getSimple();
                Double prediccionValue = prediction.getPrediccionValue();
                List<List<Object>> variablesImportantes = prediction.getVariablesImportantes();

                Long prediccionRedondeada = Math.round(prediccionValue);
                prediction.setPrediccionValue((double) prediccionRedondeada);

                ProductosDTO producto = productoRepository.findById(prediction.getPrediccion().getProductId()).get();

                prediction.getPrediccion().setNombre(producto.getNombre());
                prediction.getPrediccion().setCategoria(producto.getCategoria());
                prediction.getPrediccion().setPrecio(producto.getPrecio());

                Double inversion = producto.getPrecio() * prediction.getPrediccionValue();
                prediction.setInversion(inversion);

                //Generar la el prompt para el OpenAI
                StringBuilder sb = new StringBuilder();
                sb.append("Analiza la siguiente predicción de demanda para un producto y redacta un resumen profesional, claro y entendible para una persona sin conocimientos técnicos.\n\n");

                sb.append("- Nombre del Producto: ").append(producto.getNombre()).append("\n");
                sb.append("- Demanda estimada: ").append(prediccionRedondeada).append(" unidades\n");
                sb.append("- Explicación simple: ").append(explicacionSimple).append("\n");
                sb.append("- Variables más influyentes:\n");
                for (List<Object> var : variablesImportantes) {
                    sb.append("   • ").append(var.get(0)).append(" con peso ").append(var.get(1)).append("\n");
                }

                sb.append("\nCon base en esta información, redacta una explicación clara para un cliente o gerente comercial, donde señales de forma sencilla ");
                sb.append("por qué cada una de estas variables influye en la demanda de este producto.\n\n");

                sb.append("Usa un lenguaje cotidiano, evita tecnicismos y no menciones frases como 'el sistema dice' ni 'el modelo genera'. ");
                sb.append("La explicación debe ayudar a tomar decisiones prácticas como ajustar precios, planificar compras o entender estacionalidades.");
                String prompt = sb.toString();

                //Persisitir en la base de datos
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(prediction);
                PrediccionDTO pred = new PrediccionDTO(new Date(), fecha_prediccion, "individual", json);
                predictionRepository.save(pred);
                System.out.println("Predicción guardada para la fecha " + fecha_prediccion);

                //realizar el request y devolver
                Mono<PredictionSingle> result = explanationService.askOpenAI(prompt)
                .map(explicacionOpenAI -> {
                    prediction.setExplicacionOpenAi(explicacionOpenAI);
                    return prediction;
                });

                return result;

            } catch (Exception e) {
                System.err.println("Error al serializar o guardar la predicción: " + e.getMessage());
                return null;
            }
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
        PredictionRequest request = new PredictionRequest(0, fecha_prediccion);
        Flux<PredictionGroup> prediccion =  webClient.post().uri("/all-products").bodyValue(request).retrieve()
        .bodyToFlux(PredictionGroup.class)
        .flatMap(predictionGroup -> {
            try {

                List<Integer> productIds = predictionGroup.getPredicciones().stream()
                .map(PredictionGroup.IndividualPrediction::getProduct_id)
                .distinct()
                .collect(Collectors.toList());

                for (PredictionGroup.IndividualPrediction pred : predictionGroup.getPredicciones()) {
                    Double predValue = pred.getPredicted_stock();
                    Long redondeado = Math.round(predValue);
                    pred.setPredicted_stock((double) redondeado);
                }

                List<ProductProjection> productos = productoRepository.findByIdIn(productIds);

                Map<Integer, ProductProjection> productsNames = productos.stream()
                .collect(Collectors.toMap(ProductProjection::getId, Function.identity()));

                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(predictionGroup);
                PrediccionDTO pred = new PrediccionDTO(new Date(), fecha_prediccion, "grupo", json);
                predictionRepository.save(pred);
                System.out.println("Predicción guardada para la fecha " + fecha_prediccion);
                //System.out.println("Prediccion guardada en la base de datos: " + pred);

                Double inversion = predictionGroup.getPredicciones().stream()
                    .mapToDouble(prediction -> {
                        ProductProjection producto = productsNames.get(prediction.getProduct_id());
                        if (producto != null) {
                            prediction.setName(producto.getNombre());
                            prediction.setCategoria(producto.getCategoria());
                            prediction.setPrecio(producto.getPrecio());
                        } else {
                            prediction.setName("Nombre Desconocido");
                            prediction.setCategoria("Categoria Desconocida");
                            prediction.setPrecio(0.0);
                        }
                        return prediction.getPrecio()*prediction.getPredicted_stock();
                    })
                    .sum();

                predictionGroup.setInversion(inversion);

                return Flux.just(predictionGroup);
            } catch (Exception e) {
                System.err.println("Error al serializar o guardar la predicción: " + e.getMessage());
                return null;
            }
        })
        .doOnError(error -> {
            System.out.println("Error al predecir: " + error);
        })
        .doOnComplete(() -> {
            System.out.println("Finalizado");
        });

        return prediccion;
    }

}

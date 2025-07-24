package com.inventia.inventia_app.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import com.inventia.inventia_app.repositories.ProductoRepository.ProductIdAndNombre;

import reactor.core.publisher.Flux;

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
                PrediccionDTO pred = new PrediccionDTO(new Date(), fecha_prediccion, "individual" , prediction.toString());
                predictionRepository.save(pred);

                String explicacionSimple = prediction.getSimple();
                Double prediccionValue = prediction.getPrediccionValue();
                List<List<Object>> variablesImportantes = prediction.getVariablesImportantes();

                Long prediccionRedondeada = Math.round(prediccionValue);
                prediction.setPrediccionValue((double) prediccionRedondeada);

                ProductosDTO producto = productoRepository.findById(prediction.getPrediccion().getProductId()).get();

                prediction.getPrediccion().setNombre(producto.getNombre());

                Double inversion = producto.getPrecio() * prediction.getPrediccionValue();
                prediction.setInversion(inversion);
                //Generar la el prompt para el OpenAI
                StringBuilder sb = new StringBuilder();
                sb.append("Analiza la siguiente predicción de demanda para un producto y redacta un resumen profesional, claro y entendible para una persona sin conocimientos técnicos.\n\n");

                sb.append("- Producto ID: ").append(product_id).append("\n");
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

                //realizar el request y devolver
                return explanationService.askOpenAI(prompt)
                    .map(explicacionOpenAI -> {
                        prediction.setExplicacionOpenAi(explicacionOpenAI);
                        return prediction;
                    })
                    .doOnNext(updatedPrediction -> {
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

/*
public Flux<PredictionGroup> predictGroup(String fecha_prediccion) {
    PredictionRequest product = new PredictionRequest(0, fecha_prediccion);
    Flux<PredictionGroup> prediccion = webClient.post()
        .uri("/all-products")
        .bodyValue(product)
        .retrieve()
        .bodyToFlux(PredictionGroup.class);

    prediccion.subscribe(
        prediction -> {
            try {
                // Verificar si ya existe una predicción para esa fecha y tipo
                Optional<PrediccionDTO> existente = predictionRepository
                    .findFirstByFechaPrediccionAndTipo(fecha_prediccion, "grupo");

                if (existente.isPresent()) {
                    System.out.println("Ya existe una predicción para la fecha " + fecha_prediccion + ". No se guardará nuevamente.");
                    return; // Salir sin guardar
                }

                // Si no existe, guardar
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(prediction);
                PrediccionDTO pred = new PrediccionDTO(new Date(), fecha_prediccion, "grupo", json);
                predictionRepository.save(pred);
                System.out.println("Predicción guardada para la fecha " + fecha_prediccion);

            } catch (Exception e) {
                System.err.println("Error al serializar o guardar la predicción: " + e.getMessage());

        },
        error -> {
            System.err.println("❌ Error al predecir: " + error.getMessage());
        },
        () -> {
            System.out.println("✅ Proceso de predicción completado.");
        }
                */
    public Flux<PredictionGroup> predictGroup(String fecha_prediccion) {
        PredictionRequest request = new PredictionRequest(0, fecha_prediccion);
        Flux<PredictionGroup> prediccion =  webClient.post().uri("/all-products").bodyValue(request).retrieve()
            .bodyToFlux(PredictionGroup.class)
            .flatMap(prediction -> {
                Double inversion = 0.0;
                List<Integer> productIds = prediction.getPredicciones().stream()
                    .map(PredictionGroup.IndividualPrediction::getProduct_id)
                    .distinct()
                    .collect(Collectors.toList());
                for (PredictionGroup.IndividualPrediction pred : prediction.getPredicciones()) {
                    //ProductosDTO producto = productoRepository.findById(productId).get();
                    //pred.setName(producto.getNombre());
                    Double predValue = pred.getPredicted_stock();
                    Long redondeado = Math.round(predValue);
                    pred.setPredicted_stock((double) redondeado);
                    //inversion += producto.getPrecio()*pred.getPredicted_stock();
                }
                List<ProductIdAndNombre> productos = productoRepository.findByIdIn(productIds);
                Map<Integer, String> productsNames = productos.stream()
                    .collect(Collectors.toMap(ProductIdAndNombre::getId, ProductIdAndNombre::getNombre));
                System.out.println(productos);
                PrediccionDTO pred = new PrediccionDTO(new Date(), fecha_prediccion, "grupo" , prediction.toString());
                predictionRepository.save(pred);
                System.out.println("Prediccion guardada en la base de datos: " + pred);

                return Flux.just(prediction);
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

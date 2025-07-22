package com.inventia.inventia_app.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventia.inventia_app.entities.PredictionGroup;
import com.inventia.inventia_app.entities.Product;
import com.inventia.inventia_app.services.ExplanationService;
import com.inventia.inventia_app.services.PredictionService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/predict")
@CrossOrigin(origins = "*")
public class PredictionController {

    private final PredictionService predictionService;
    private final ExplanationService explanationService;

    @Autowired
    public PredictionController(PredictionService predictionService, ExplanationService explanationService) {
        this.predictionService = predictionService;
        this.explanationService = explanationService;
    }

    @GetMapping("/single")
    public Mono<Map<String, Object>> predecirUnico(@RequestParam Integer product_id, @RequestParam String fecha_prediccion) {
        System.out.println("Prediciendo producto: " + product_id + ", " + fecha_prediccion);
        Product product = new Product(product_id, fecha_prediccion);

        return predictionService.predictSingle(product)
            .next()
            .flatMap(predictionSingle -> explanationService.generarExplicacionEnriquecida(predictionSingle));
    }

    @GetMapping("/group")
    public Flux<PredictionGroup> predecirGrupo(@RequestParam String fecha_prediccion) {
        System.out.println("Prediciendo de todos los productos: " + fecha_prediccion);
        Product product = new Product(0, fecha_prediccion);
        return predictionService.predictGroup(product);
    }
}

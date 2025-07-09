package com.inventia.inventia_app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.inventia.inventia_app.entities.Product;
import com.inventia.inventia_app.services.PredictionService;

import reactor.core.publisher.Flux;

/**
 * PredictionController
 */
@RestController
@RequestMapping("/predict")
public class PredictionController {

    private PredictionService predictionService;

    @Autowired
    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    public PredictionController() {
        //predictionService = new PredictionService(webClientBuilder);
    }

    @PostMapping("/single")
    public Flux<String> predecirUnico(@RequestBody Product product){
        System.out.println(product.getProduct_id());
        return predictionService.predictSingle(product);
    }

    @PostMapping("/group")
    public String predecirGrupo(@RequestBody String body){
        return "prediccion";
    }
}

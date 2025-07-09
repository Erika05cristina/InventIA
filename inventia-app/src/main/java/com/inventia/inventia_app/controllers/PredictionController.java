package com.inventia.inventia_app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.inventia.inventia_app.entities.PredictionResponseGroup;
import com.inventia.inventia_app.entities.PredictionResponseSingle;
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

    @GetMapping("/single")
    public Flux<PredictionResponseSingle> predecirUnico(@RequestParam Integer product_id, @RequestParam String fecha){
        System.out.println("Prediciendo de un solo producto: " + product_id + ", " + fecha);
        Product product = new Product(product_id, fecha);
        return predictionService.predictSingle(product);
    }

    @GetMapping("/group")
    public Flux<PredictionResponseGroup> predecirGrupo(@RequestParam String fecha){
        System.out.println("Prediciendo de todos los productos: " + fecha);
        Product product = new Product(0, fecha);
        return predictionService.predictGroup(product);
    }
}

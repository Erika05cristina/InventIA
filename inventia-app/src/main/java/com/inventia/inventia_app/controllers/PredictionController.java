package com.inventia.inventia_app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventia.inventia_app.entities.PredictionGroup;
import com.inventia.inventia_app.entities.PredictionSingle;
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

    @GetMapping("/single")
    @CrossOrigin(origins = "*")
    public Flux<PredictionSingle> predecirUnico(@RequestParam Integer product_id, @RequestParam String fecha_prediccion) {
        System.out.println("Prediciendo de un solo producto: " + product_id + ", " + fecha_prediccion);
        Flux<PredictionSingle> prediction = predictionService.predictSingle(product_id, fecha_prediccion);
        System.out.println(prediction.cast(PredictionSingle.class).toString());
        return prediction;
    }

    @GetMapping("/group")
    @CrossOrigin(origins = "*")
    public Flux<PredictionGroup> predecirGrupo(@RequestParam String fecha_prediccion) {
        System.out.println("Prediciendo de todos los productos: " + fecha_prediccion);
        return predictionService.predictGroup(fecha_prediccion);
    }

    /*
    @GetMapping("/statistics")
    @CrossOrigin(origins = "*")
    public Flux<DashboardDTO> estadisticasPredicciones() {
        System.out.println("Generando estadisticas de las predicciones");
        //return predictionService.predictGroup(product);
    }
    */
}

package com.inventia.inventia_app.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * PredictionController
 */
@RestController
public class PredictionController {

    public PredictionController() {
    }

    @GetMapping("/predict-uno")
    public String predecirUnico(@RequestBody String body){
        return "prediccion";
    }

    @GetMapping("/predict-grupo")
    public String predecirGrupo(@RequestBody String body){
        return "prediccion";
    }
}

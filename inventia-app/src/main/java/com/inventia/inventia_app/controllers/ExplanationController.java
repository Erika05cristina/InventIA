package com.inventia.inventia_app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventia.inventia_app.entities.PredictionSingle;
import com.inventia.inventia_app.services.ExplanationService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/explanation")
@CrossOrigin(origins = "*")
public class ExplanationController {

    private final ExplanationService explanationService;

    @Autowired
    public ExplanationController(ExplanationService explanationService) {
        this.explanationService = explanationService;
    }

    @PostMapping("/openai")
    public Mono<String> askOpenAI(@RequestBody String userMessage) {
        return explanationService.askOpenAI(userMessage);
    }

//    @PostMapping("/openai/from-explanation")
//         public Mono<String> askOpenAIFromExplanation(@RequestBody Map<String, Object> explicabilidad) {
//         return explanationService.askOpenAIFromExplanation(explicabilidad);
//     }
    @PostMapping("/openai/from-prediction")
        public Mono<String> explicarConOpenAI(@RequestBody PredictionSingle explicacionCompleta) {
        return explanationService.askOpenAIFromPrediction(explicacionCompleta);
    }

}

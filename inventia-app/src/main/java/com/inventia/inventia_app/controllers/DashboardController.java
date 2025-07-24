package com.inventia.inventia_app.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventia.inventia_app.entities.DashboardStatisticsResponse;
import com.inventia.inventia_app.entities.PrediccionDTO;
import com.inventia.inventia_app.entities.PredictionGroup;
import com.inventia.inventia_app.services.StatisticsService;
import com.inventia.inventia_app.repositories.PredictionRepository;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final StatisticsService statisticsService;
    private final PredictionRepository predictionRepository;
    private final ObjectMapper objectMapper;

    public DashboardController(StatisticsService statisticsService, PredictionRepository predictionRepository, ObjectMapper objectMapper) {
        this.statisticsService = statisticsService;
        this.predictionRepository = predictionRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/statistics")
    @CrossOrigin(origins = "*")
    public ResponseEntity<DashboardStatisticsResponse> calcularDesdeFecha(
            @RequestParam String fecha,
            @RequestParam(defaultValue = "grupo") String tipo) {

        List<PrediccionDTO> predicciones = predictionRepository.findAllByFechaPrediccionAndTipo(fecha, tipo);

        if (predicciones.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        PrediccionDTO registro = predicciones.get(0);

        try {
            PredictionGroup group = objectMapper.readValue(registro.getJsonPrediccion(), PredictionGroup.class);
            DashboardStatisticsResponse response = statisticsService.calcularEstadisticas(group);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir JSON de predicción", e);
        }
    }
}

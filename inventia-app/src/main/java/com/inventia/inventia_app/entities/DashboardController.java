package com.inventia.inventia_app.entities;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventia.inventia_app.dto.DashboardStatisticsResponse;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final StatisticsService statisticsService;

    public DashboardController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @PostMapping("/statistics")
    public ResponseEntity<DashboardStatisticsResponse> calcular(@RequestBody PredictionGroup group) {
        DashboardStatisticsResponse response = statisticsService.calcularEstadisticas(group);
        return ResponseEntity.ok(response);
    }
}

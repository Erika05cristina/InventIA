package com.inventia.inventia_app.entities;


import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.inventia.inventia_app.dto.DashboardStatisticsResponse;

@Service
public class StatisticsService {

    public DashboardStatisticsResponse calcularEstadisticas(PredictionGroup group) {
        List<IndividualPrediction> predicciones = group.getPredicciones();

        if (predicciones == null || predicciones.isEmpty()) {
            return new DashboardStatisticsResponse(0, 0, null, null, null, null, 0, 0, 0, group.getFecha());
        }

        int total = predicciones.stream()
                .mapToInt(p -> (int) Math.round(p.getPredicted_stock()))
                .sum();

        double promedio = total / (double) predicciones.size();

        IndividualPrediction mayor = predicciones.stream()
                .max(Comparator.comparing(IndividualPrediction::getPredicted_stock))
                .orElse(null);

        IndividualPrediction menor = predicciones.stream()
                .min(Comparator.comparing(IndividualPrediction::getPredicted_stock))
                .orElse(null);

        int alta = (int) predicciones.stream()
                .filter(p -> p.getPredicted_stock() > 200).count();

        int media = (int) predicciones.stream()
                .filter(p -> p.getPredicted_stock() >= 50 && p.getPredicted_stock() <= 200).count();

        int baja = (int) predicciones.stream()
                .filter(p -> p.getPredicted_stock() < 50).count();

        return new DashboardStatisticsResponse(
                total,
                promedio,
                mayor != null ? mayor.getProduct_id() : null,
                mayor != null ? mayor.getPredicted_stock() : null,
                menor != null ? menor.getProduct_id() : null,
                menor != null ? menor.getPredicted_stock() : null,
                alta,
                media,
                baja,
                group.getFecha()
        );
    }
}

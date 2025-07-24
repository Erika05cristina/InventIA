package com.inventia.inventia_app.entities;

import java.util.ArrayList;
import java.util.List;


/**
 * PredictionGroup
 */
public class PredictionGroup {

    public static class IndividualPrediction {

        private Integer product_id;
        private String name;
        private String categoria;
        private Double predicted_stock;

        public IndividualPrediction(Integer product_id, String name, String categoria, Double predicted_stock) {
            this.product_id = product_id;
            this.name = name;
            this.categoria = categoria;
            this.predicted_stock = predicted_stock;
        }

        public void setProduct_id(Integer product_id) {
            this.product_id = product_id;
        }

        public Integer getProduct_id() {
            return this.product_id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void setCategoria(String categoria) {
            this.categoria = categoria;
        }

        public String getCategoria() {
            return this.categoria;
        }

        public void setPredicted_stock(Double predicted_stock) {
            this.predicted_stock = predicted_stock;
        }

        public Double getPredicted_stock() {
            return this.predicted_stock;
        }

        @Override
        public String toString() {
            return "IndividualPrediction{"
            + "product_id=" + product_id
            + ", predicted_stock=" + predicted_stock
            + '}';
        }
    }

    private String status;
    private String fecha;
    private List<IndividualPrediction> predicciones;
    private Double inversion;

    public PredictionGroup(String status, String fecha) {
        this.status = status;
        this.fecha = fecha;
    }

    public PredictionGroup() {
        this.predicciones = new ArrayList<>();
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFecha() {
        return this.fecha;
    }

    public void setPredicciones(ArrayList<IndividualPrediction> predicciones) {
        this.predicciones = predicciones;
    }

    public List<IndividualPrediction> getPredicciones(){
        return this.predicciones;
    }

    public void setInversion(Double inversion) {
        this.inversion = inversion;
    }

    public Double getInversion() {
        return this.inversion;
    }

    @Override
    public String toString() {
        return "PredictionGroup{"
            + "status='" + status + '\''
            + ", fecha='" + fecha + '\''
            + ", predicciones=" + predicciones.toString()
            + ", inversion=" + inversion
            + '}';
    }
}

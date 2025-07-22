package com.inventia.inventia_app.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PredictionSingle {

    private String status;
    private Prediccion prediccion;

    @JsonProperty("explicacion_simple")
    private String simple;

    @JsonProperty("explicacion_avanzada")
    private ExplicacionAvanzada explicacionAvanzada;

    public PredictionSingle() {}

    public PredictionSingle(String status, String simple) {
        this.status = status;
        this.simple = simple;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Prediccion getPrediccion() {
        return this.prediccion;
    }

    public void setPrediccion(Prediccion prediccion) {
        this.prediccion = prediccion;
    }

    public String getSimple() {
        return this.simple;
    }

    public void setSimple(String simple) {
        this.simple = simple;
    }

    public ExplicacionAvanzada getExplicacionAvanzada() {
        return this.explicacionAvanzada;
    }

    public void setExplicacionAvanzada(ExplicacionAvanzada explicacionAvanzada) {
        this.explicacionAvanzada = explicacionAvanzada;
    }

    @Override
    public String toString() {
        return "PredictionSingle{" +
                "status='" + status + '\'' +
                ", prediccion=" + prediccion +
                ", explicacion_simple='" + simple + '\'' +
                ", explicacion_avanzada=" + explicacionAvanzada +
                '}';
    }

    // 🔧 🔔 Corrección mínima: hacemos Prediccion pública y estática
    public static class Prediccion {

        @JsonProperty("product_id")
        private Integer productId;

        @JsonProperty("fecha_prediccion")
        private String fecha;

        private Double prediccion;

        public Prediccion() {}

        public Prediccion(Integer productId, String fecha, Double prediccion){
            this.productId = productId;
            this.fecha = fecha;
            this.prediccion = prediccion;
        }

        public Integer getProductId() {
            return this.productId;
        }

        public String getFecha() {
            return this.fecha;
        }

        public Double getPrediccion() {
            return this.prediccion;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }

        public void setPrediccion(Double prediccion) {
            this.prediccion = prediccion;
        }

        @Override
        public String toString() {
            return "Prediccion{" +
                    "productId=" + productId +
                    ", fecha_prediccion='" + fecha + '\'' +
                    ", prediccion=" + prediccion +
                    '}';
        }
    }
}

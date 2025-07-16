package com.inventia.inventia_app.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
/*
* "explicacion_avanzada": {
    "producto_id": 38,
    "prediccion": 0.04732787609100342,
    "variables_importantes": [
        [
            "sale_amount",
            0.8985185196251297
        ],
        [
            "avg_temperature",
            0.17775082988437793
        ],
        [
            "holiday_flag",
            0.13028467096889373
        ],
        [
            "discount",
            0.07526980119801568
        ],
        [
            "avg_humidity",
            0.07071108022976644
        ]
    ],
    "grafica_explicabilidad_base64":
*/


class Prediccion {

    @JsonProperty("product_id")
    private Integer productId;
    @JsonProperty("fecha_prediccion")
    private String fecha;
    private Double prediccion;

    public Prediccion(Integer productId, String fecha, Double prediccion){
        this.productId = productId;
        this.fecha = fecha;
        this.prediccion = prediccion;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getProductId() {
        return this.productId;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFecha() {
        return this.fecha;
    }

    public void setPrediccion(Double prediccion) {
        this.prediccion = prediccion;
    }

    public Double getPrediccion() {
        return this.prediccion;
    }

    @Override
    public String toString() {
        return "Prediccion{"
                + "productId='"
                + productId
                + '\''
                + "fecha_prediccion='"
                + fecha
                + '\''
                + ", prediccion='"
                + prediccion
                + '\''
                + '}';
    }
}

/**
 * PredictionSingle
 */
public class PredictionSingle {

    private String status;
    private Prediccion prediccion;
    @JsonProperty("explicacion_simple")
    private String simple;
    @JsonProperty("explicacion_avanzada")
    private ExplicacionAvanzada explicacionAvanzada;

    public PredictionSingle(String status, String simple) {
        this.status = status;
        this.simple = simple;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setPrediccion(Prediccion prediccion) {
        this.prediccion = prediccion;
    }

    public Prediccion getPrediccion() {
        return this.prediccion;
    }

    public void setSimple(String simple) {
        this.simple = simple;
    }

    public String getSimple() {
        return this.simple;
    }

    public void setExplicacionAvanzada(ExplicacionAvanzada explicacionAvanzada) {
        this.explicacionAvanzada = explicacionAvanzada;
    }

    public ExplicacionAvanzada getExplicacionAvanzada() {
        return this.explicacionAvanzada;
    }

    @Override
    public String toString() {
        return "PredictionSingle{"
                + "status='"
                + status
                + '\''
                + "prediccion="
                + prediccion.toString()
                + ", explicacion_simple='"
                + simple
                + '\''
                + ", explicacion_avanzada="
                + explicacionAvanzada.toString()
                + '}';
    }
}

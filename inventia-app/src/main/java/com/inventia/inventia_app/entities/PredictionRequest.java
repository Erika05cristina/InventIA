package com.inventia.inventia_app.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * PredictionRequest
 */
public class PredictionRequest {

    private Integer product_id;
    @JsonProperty("fecha_prediccion")
    private String fecha;

    public PredictionRequest(Integer product_id, String fecha){
        this.product_id = product_id;
        this.fecha = fecha;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public Integer getProduct_id() {
        return this.product_id;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFecha() {
        return this.fecha;
    }

    @Override
    public String toString() {
        return "PredictionRequest{"
        + "product_id='"
        + product_id
        + '\''
        + ", fecha="
        + fecha
        + '}';
    }
}

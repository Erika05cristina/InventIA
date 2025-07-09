package com.inventia.inventia_app.entities;

/**
 * PredictionResponseSingle
 */
public class PredictionResponseSingle {

    private String status;
    private Integer product_id;
    private String fecha;
    private Double prediccion;

    public PredictionResponseSingle(String status, Integer product_id, String fecha, Double prediccion){
        this.status = status;
        this.product_id = product_id;
        this.fecha = fecha;
        this.prediccion = prediccion;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
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

    public void setPrediccion(Double prediccion) {
        this.prediccion = prediccion;
    }

    public Double getPrediccion() {
        return this.prediccion;
    }

    @Override
    public String toString() {
        return "PredictionResponseSingle{"
        + "status='"
        + status
        + '\''
        + "product_id='"
        + product_id
        + '\''
        + ", fecha="
        + fecha
        + '\''
        + ", prediccion="
        + prediccion
        + '}';
    }
}

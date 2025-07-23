package com.inventia.inventia_app.entities;

import java.util.ArrayList;

class IndividualPrediction {

    private Integer product_id;
    private Double predicted_stock;

    public IndividualPrediction(Integer product_id, Double predicted_stock) {
        this.product_id = product_id;
        this.predicted_stock = predicted_stock;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public Integer getProduct_id() {
        return this.product_id;
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

/**
 * PredictionGroup
 */
public class PredictionGroup {

    private String status;
    private String fecha;
    private ArrayList<IndividualPrediction> predicciones;
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

    public ArrayList<IndividualPrediction> getPredicciones(){
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

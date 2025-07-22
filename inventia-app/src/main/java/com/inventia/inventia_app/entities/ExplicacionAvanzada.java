package com.inventia.inventia_app.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExplicacionAvanzada {

    @JsonProperty("producto_id")
    private Integer productId;

    private Double prediccion;

    @JsonProperty("variables_importantes")
    private List<List<Object>> importantes;

    @JsonProperty("grafica_explicabilidad_base64")
    private String graphBase64;

    public ExplicacionAvanzada() {}

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Double getPrediccion() {
        return prediccion;
    }

    public void setPrediccion(Double prediccion) {
        this.prediccion = prediccion;
    }

    public List<List<Object>> getImportantes() {
        return importantes;
    }

    public void setImportantes(List<List<Object>> importantes) {
        this.importantes = importantes;
    }

    public String getGraphBase64() {
        return graphBase64;
    }

    public void setGraphBase64(String graphBase64) {
        this.graphBase64 = graphBase64;
    }

    @Override
    public String toString() {
        return "ExplicacionAvanzada{"
                + "productId='"
                + productId
                + '\''
                + ", prediccion='"
                + prediccion
                + '\''
                + ", importantes='"
                + importantes
                + '\''
                + ", graphBase64='"
                + graphBase64
                + '\''
                + '}';
    }
}

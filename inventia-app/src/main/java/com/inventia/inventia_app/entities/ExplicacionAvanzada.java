package com.inventia.inventia_app.entities;

import java.util.List;

public class ExplicacionAvanzada {
private Integer productId;
private Double prediccion;
private List<List<Object>> importantes;
private String graphBase64;
public Integer getProductId() {
    return productId;
}

public Double getPrediccion() {
    return prediccion;
}

public List<List<Object>> getImportantes() {
    return importantes;
}

public String getGraphBase64() {
    return graphBase64;
}

 
public ExplicacionAvanzada() {}
}
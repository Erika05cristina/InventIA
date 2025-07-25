package com.inventia.inventia_app.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * PredictionSingle
 */
public class PredictionSingle {

    public static class ExplicacionAvanzada {

        @JsonProperty("producto_id")
        private Integer productId;

        private Double prediccion;


        @JsonProperty("variables_importantes")
        private List<List<Object>> importantes;

        @JsonProperty("grafica_explicabilidad_base64")
        private String graphBase64;

        public ExplicacionAvanzada() {}

        public ExplicacionAvanzada(Integer productId, Double prediccion) {
            this.productId = productId;
            this.prediccion = prediccion;
        }

        public Integer getProductId() {
            return this.productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public void setPrediccion(Double prediccion) {
            this.prediccion = prediccion;
        }

        public Double getPrediccion() {
            return this.prediccion;
        }

        public List<List<Object>> getImportantes() {
            return this.importantes;
        }

        public String getGraphBase64() {
            return this.graphBase64;
        }

        @Override
        public String toString() {
            return "ExplicacionAvanzada{"
            + "productId=" + productId
            + ", prediccion=" + prediccion
            + ", importantes=" + importantes
            + ", graphBase64=" + graphBase64
            + '}';
        }
    }

    public static class Prediccion {

        @JsonProperty("product_id")
        private Integer productId;
        private String nombre;
        private String categoria;
        private Double precio;
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

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getNombre() {
            return this.nombre;
        }

        public void setCategoria(String categoria) {
            this.categoria = categoria;
        }

        public String getCategoria() {
            return this.categoria;
        }

        public void setPrecio(Double precio) {
            this.precio = precio;
        }

        public Double getPrecio() {
            return this.precio;
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
            return "Prediccion{" +
            "productId=" + productId +
            ", nombre='" + nombre + '\'' +
            ", fecha='" + fecha + '\'' +
            ", prediccion=" + prediccion +
            '}';
        }

    }

    private String status;
    private Prediccion prediccion;
    @JsonProperty("explicacion_simple")
    private String simple;
    @JsonProperty("explicacion_avanzada")
    private ExplicacionAvanzada explicacionAvanzada;
    private Double inversion;
    private String explicacionOpenAI;

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

    public void setInversion(Double inversion) {
        this.inversion = inversion;
    }

    public Double getInversion() {
        return this.inversion;
    }

    public void setExplicacionOpenAi(String explicacionOpenAI) {
        this.explicacionOpenAI = explicacionOpenAI;
    }

    public String getExplicacionOpenAi() {
        return this.explicacionOpenAI;
    }

    public void setPrediccionValue(Double prediccion) {
        this.prediccion.setPrediccion(prediccion);
    }

    @JsonIgnore
    public Double getPrediccionValue() {
        return this.prediccion.getPrediccion();
    }

    @JsonIgnore
    public List<List<Object>> getVariablesImportantes() {
        return this.explicacionAvanzada.getImportantes();
    }

    @JsonIgnore
    public String getGraphBase64() {
        return this.explicacionAvanzada.getGraphBase64();
    }

    @Override
    public String toString(){
        return "PredictionSingle{" +
            "status='" + status + '\'' +
            ", prediccion=" + prediccion.toString() +
            ", explicacion_simple='" + simple + '\'' +
            ", explicacion_avanzada=" + explicacionAvanzada.toString() +
            ", inversion=" + inversion +
            ", explicacionOpenAI='" + explicacionOpenAI + '\'' +
            '}';
    }
}

package com.inventia.inventia_app.entities;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;

/**
 * PredictionDTO
 */
@Entity(name = "prediccion")
public class PrediccionDTO {

    @Id
    @Column(name = "prediccion_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer prediccionId;
    @Column(name = "prediccion_fecha_creacion")
    private Date fechaCreacion;
    @Column(name = "prediccion_fecha_prediccion")
    private String fechaPrediccion;
    @Column(name = "prediccion_tipo")
    private String tipo;
    @Column(name = "prediccion_contenido")
    private String jsonPrediccion;

    public PrediccionDTO() {}

    public PrediccionDTO(Date fechaCreacion, String fechaPrediccion, String tipo, String contenido) {
        this.fechaCreacion = fechaCreacion;
        this.fechaPrediccion = fechaPrediccion;
        this.tipo = tipo;
        this.jsonPrediccion = contenido;
    }

    public Integer getPrediccionId() {
        return prediccionId;
    }

    public void setPrediccionId(Integer prediccionId) {
        this.prediccionId = prediccionId;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaPrediccion() {
        return fechaPrediccion;
    }

    public void setFechaPrediccion(String fechaPrediccion) {
        this.fechaPrediccion = fechaPrediccion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getJsonPrediccion() {
        return jsonPrediccion;
    }

    public void setJsonPrediccion(String jsonPrediccion) {
        this.jsonPrediccion = jsonPrediccion;
    }

    @Override
    public String toString() {
        return "PredictionDTO{" +
                "prediccionId=" + prediccionId +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaPrediccion=" + fechaPrediccion +
                ", jsonPrediccion='" + jsonPrediccion + '\'' +
                '}';
    }

}

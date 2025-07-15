package com.inventia.inventia_app.entities;

/**
 * ProductRecords
 */
public class ProductRecord {

    private Integer product_id;
    private String dt;
    private Integer ventas;
    private Integer dia;
    private Integer dia_semana;
    private Integer es_fin_semana;
    private Integer es_feriado;
    private Integer es_inicio_mes;
    private Double media_7d;
    private Double std_7d;

    public ProductRecord (
        Integer product_id,
        String dt,
        Integer ventas,
        Integer dia,
        Integer dia_semana,
        Integer es_fin_semana,
        Integer es_feriado,
        Integer es_inicio_mes,
        Double media_7d,
        Double std_7d
    ){
        this.product_id = product_id;
        this.dt = dt;
        this.ventas = ventas;
        this.dia = dia;
        this.dia_semana = dia_semana;
        this.es_fin_semana = es_fin_semana;
        this.es_feriado = es_feriado;
        this.es_inicio_mes = es_inicio_mes;
        this.media_7d = media_7d;
        this.std_7d = std_7d;
    }

    public void setProduct_id(Integer product_id){
        this.product_id = product_id;
    }

    public Integer getProduct_id(){
        return this.product_id;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getDt() {
        return this.dt;
    }

    public void setventas(Integer ventas){
        this.ventas = ventas;
    }

    public Integer getVentas(){
        return this.ventas;
    }
    public void setDia(Integer dia){
        this.dia = dia;
    }

    public Integer getDia(){
        return this.dia;
    }
    public void setDia_semana(Integer dia_semana){
        this.dia_semana = dia_semana;
    }

    public Integer getDia_semana() {
        return this.dia_semana;
    }

    public Integer getEs_fin_semana(){
        return this.es_fin_semana;
    }
    public void setEs_fin_semana(Integer es_fin_semana){
        this.es_fin_semana = es_fin_semana;
    }

    public Integer getEs_feriado(){
        return this.es_feriado;
    }
    public void setEs_feriado(Integer es_feriado){
        this.es_feriado = es_feriado;
    }

    public Integer getEs_inicio_mes(){
        return this.es_inicio_mes;
    }
    public void setEs_inico_mes(Integer es_inicio_mes){
        this.es_inicio_mes = es_inicio_mes;
    }

    public void setMedia_7d(Double media_7d) {
        this.media_7d = media_7d;
    }

    public Double getMedia_7d() {
        return this.media_7d;
    }
    public void setStd_7d(Double std_7d) {
        this.std_7d = std_7d;
    }

    public Double getStd_7d() {
        return this.std_7d;
    }

    @Override
    public String toString() {
        return "ProductRecord{"
        + "product_id='"
        + product_id
        + '\''
        + "dt='"
        + dt
        + '\''
        + "ventas='"
        + ventas
        + '\''
        + "dia='"
        + dia
        + '\''
        + "dia_semana='"
        + dia_semana
        + '\''
        + "es_fin_semana='"
        + es_fin_semana
        + '\''
        + "es_feriado='"
        + es_feriado
        + '\''
        + "es_inicio_mes='"
        + es_inicio_mes
        + '\''
        + "media_7d='"
        + media_7d
        + '\''
        + ", std_7d="
        + std_7d
        + '}';
    }
}

package com.inventia.inventia_app.dto;

public class DashboardStatisticsResponse {

    private int totalUnidades;
    private double promedioPorProducto;
    private Integer productoMayorDemandaId;
    private Double productoMayorDemandaStock;
    private Integer productoMenorDemandaId;
    private Double productoMenorDemandaStock;
    private int cantidadAltaDemanda;
    private int cantidadMediaDemanda;
    private int cantidadBajaDemanda;
    private String fecha;

    public DashboardStatisticsResponse() {
    }

    public DashboardStatisticsResponse(int totalUnidades, double promedioPorProducto,
            Integer productoMayorDemandaId, Double productoMayorDemandaStock,
            Integer productoMenorDemandaId, Double productoMenorDemandaStock,
            int cantidadAltaDemanda, int cantidadMediaDemanda, int cantidadBajaDemanda,
            String fecha) {
        this.totalUnidades = totalUnidades;
        this.promedioPorProducto = promedioPorProducto;
        this.productoMayorDemandaId = productoMayorDemandaId;
        this.productoMayorDemandaStock = productoMayorDemandaStock;
        this.productoMenorDemandaId = productoMenorDemandaId;
        this.productoMenorDemandaStock = productoMenorDemandaStock;
        this.cantidadAltaDemanda = cantidadAltaDemanda;
        this.cantidadMediaDemanda = cantidadMediaDemanda;
        this.cantidadBajaDemanda = cantidadBajaDemanda;
        this.fecha = fecha;
    }

    // Getters y setters
    public int getTotalUnidades() {
        return totalUnidades;
    }

    public void setTotalUnidades(int totalUnidades) {
        this.totalUnidades = totalUnidades;
    }

    public double getPromedioPorProducto() {
        return promedioPorProducto;
    }

    public void setPromedioPorProducto(double promedioPorProducto) {
        this.promedioPorProducto = promedioPorProducto;
    }

    public Integer getProductoMayorDemandaId() {
        return productoMayorDemandaId;
    }

    public void setProductoMayorDemandaId(Integer productoMayorDemandaId) {
        this.productoMayorDemandaId = productoMayorDemandaId;
    }

    public Double getProductoMayorDemandaStock() {
        return productoMayorDemandaStock;
    }

    public void setProductoMayorDemandaStock(Double productoMayorDemandaStock) {
        this.productoMayorDemandaStock = productoMayorDemandaStock;
    }

    public Integer getProductoMenorDemandaId() {
        return productoMenorDemandaId;
    }

    public void setProductoMenorDemandaId(Integer productoMenorDemandaId) {
        this.productoMenorDemandaId = productoMenorDemandaId;
    }

    public Double getProductoMenorDemandaStock() {
        return productoMenorDemandaStock;
    }

    public void setProductoMenorDemandaStock(Double productoMenorDemandaStock) {
        this.productoMenorDemandaStock = productoMenorDemandaStock;
    }

    public int getCantidadAltaDemanda() {
        return cantidadAltaDemanda;
    }

    public void setCantidadAltaDemanda(int cantidadAltaDemanda) {
        this.cantidadAltaDemanda = cantidadAltaDemanda;
    }

    public int getCantidadMediaDemanda() {
        return cantidadMediaDemanda;
    }

    public void setCantidadMediaDemanda(int cantidadMediaDemanda) {
        this.cantidadMediaDemanda = cantidadMediaDemanda;
    }

    public int getCantidadBajaDemanda() {
        return cantidadBajaDemanda;
    }

    public void setCantidadBajaDemanda(int cantidadBajaDemanda) {
        this.cantidadBajaDemanda = cantidadBajaDemanda;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}

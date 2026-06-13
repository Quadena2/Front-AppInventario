package com.laboratorio.inventarioapp.model;

public class StockUpdateMessage {
    private Long productoId;
    private String nombreProducto;
    private int nuevoStock;

    public Long getProductoId() {
        return productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public int getNuevoStock() {
        return nuevoStock;
    }
}

package com.laboratorio.inventarioapp.model;

public class ProductoRequest {

    private String nombre;
    private Long categoriaId;
    private int stock;
    private double precio;

    public ProductoRequest(String nombre, Long categoriaId, int stock, double precio){
        this.nombre = nombre;
        this.categoriaId = categoriaId;
        this.stock = stock;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public int getStock() {
        return stock;
    }

    public double getPrecio() {
        return precio;
    }
}

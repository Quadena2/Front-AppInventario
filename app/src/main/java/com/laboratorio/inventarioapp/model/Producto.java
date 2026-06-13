package com.laboratorio.inventarioapp.model;

public class Producto {

    private Long id;
    private String nombre;
    private int stock;
    private double precio;
    private Categoria categoria;

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getStock() {
        return stock;
    }

    public double getPrecio() {
        return precio;
    }

    public Categoria getCategoria() {
        return categoria;
    }
}

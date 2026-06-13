package com.laboratorio.inventarioapp.model;

public class CategoriaRequest {
    private String nombre;
    private String descripcion;

    public CategoriaRequest(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
}

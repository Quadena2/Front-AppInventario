package com.laboratorio.inventarioapp.network;

import com.laboratorio.inventarioapp.model.Categoria;
import com.laboratorio.inventarioapp.model.Producto;
import com.laboratorio.inventarioapp.model.ProductoRequest;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductoApiService {

    @GET("api/productos")
    Call<List<Producto>> listarProductos();


    @POST("api/productos")
    Call<Producto> crearProducto(@Body ProductoRequest request);

    @PUT("api/productos/{id}/stock")
    Call<ResponseBody> actualizarStock(
            @Path("id") Long id,
            @Query("nuevoStock")int nuevoStock
    );

    @DELETE("api/productos/{id}")
    Call<ResponseBody> eliminarProducto(@Path("id") Long id);

    @GET("api/categorias")
    Call<List<Categoria>> listarCategorias();
}

package com.laboratorio.inventarioapp.network;

import com.laboratorio.inventarioapp.model.Categoria;
import com.laboratorio.inventarioapp.model.CategoriaRequest;

import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CategoriaApiService {

    @GET("api/categorias")
    Call<List<Categoria>> listarCategorias();

    // Cambia Categoria por CategoriaRequest
    @POST("api/categorias")
    Call<Categoria> crearCategoria(@Body CategoriaRequest request);

    @DELETE("api/categorias/{id}")
    Call<ResponseBody> eliminarCategoria(@Path("id") Long id);
}
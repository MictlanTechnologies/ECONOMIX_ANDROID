package com.example.economix_android.network.api;

import com.example.economix_android.network.dto.CategoriaGastoDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CategoriaGastoApi {
    @GET("/economix/api/categorias-gasto")
    Call<List<CategoriaGastoDto>> getAll();

    @GET("/economix/api/categorias-gasto/{id}")
    Call<CategoriaGastoDto> getById(@Path("id") Integer id);

    @POST("/economix/api/categorias-gasto")
    Call<CategoriaGastoDto> create(@Body CategoriaGastoDto dto);

    @PUT("/economix/api/categorias-gasto/{id}")
    Call<CategoriaGastoDto> update(@Path("id") Integer id, @Body CategoriaGastoDto dto);

    @DELETE("/economix/api/categorias-gasto/{id}")
    Call<Void> delete(@Path("id") Integer id);
}

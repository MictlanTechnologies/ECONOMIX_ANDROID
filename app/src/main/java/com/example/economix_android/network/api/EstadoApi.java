package com.example.economix_android.network.api;

import com.example.economix_android.network.dto.EstadoDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EstadoApi {
    @GET("/economix/api/estados")
    Call<List<EstadoDto>> getAll();

    @GET("/economix/api/estados/{id}")
    Call<EstadoDto> getById(@Path("id") Integer id);

    @POST("/economix/api/estados")
    Call<EstadoDto> create(@Body EstadoDto dto);

    @PUT("/economix/api/estados/{id}")
    Call<EstadoDto> update(@Path("id") Integer id, @Body EstadoDto dto);

    @DELETE("/economix/api/estados/{id}")
    Call<Void> delete(@Path("id") Integer id);
}

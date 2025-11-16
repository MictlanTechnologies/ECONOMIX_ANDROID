package com.example.economix_android.network.api;

import com.example.economix_android.network.dto.SesionDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SesionApi {
    @GET("/economix/api/sesiones")
    Call<List<SesionDto>> getAll();

    @GET("/economix/api/sesiones/{id}")
    Call<SesionDto> getById(@Path("id") Integer id);

    @POST("/economix/api/sesiones")
    Call<SesionDto> create(@Body SesionDto dto);

    @PUT("/economix/api/sesiones/{id}")
    Call<SesionDto> update(@Path("id") Integer id, @Body SesionDto dto);

    @DELETE("/economix/api/sesiones/{id}")
    Call<Void> delete(@Path("id") Integer id);
}

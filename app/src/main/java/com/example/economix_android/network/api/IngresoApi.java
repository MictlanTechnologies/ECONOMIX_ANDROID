package com.example.economix_android.network.api;

import com.example.economix_android.network.dto.IngresoDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface IngresoApi {
    @GET("/economix/api/ingresos")
    Call<List<IngresoDto>> getAll();

    @GET("/economix/api/ingresos/{id}")
    Call<IngresoDto> getById(@Path("id") Integer id);

    @POST("/economix/api/ingresos")
    Call<IngresoDto> create(@Body IngresoDto dto);

    @PUT("/economix/api/ingresos/{id}")
    Call<IngresoDto> update(@Path("id") Integer id, @Body IngresoDto dto);

    @DELETE("/economix/api/ingresos/{id}")
    Call<Void> delete(@Path("id") Integer id);
}

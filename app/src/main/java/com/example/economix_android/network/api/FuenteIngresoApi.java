package com.example.economix_android.network.api;

import com.example.economix_android.network.dto.FuenteIngresoDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface FuenteIngresoApi {
    @GET("/economix/api/fuentes-ingreso")
    Call<List<FuenteIngresoDto>> getAll();

    @GET("/economix/api/fuentes-ingreso/{id}")
    Call<FuenteIngresoDto> getById(@Path("id") Integer id);

    @POST("/economix/api/fuentes-ingreso")
    Call<FuenteIngresoDto> create(@Body FuenteIngresoDto dto);

    @PUT("/economix/api/fuentes-ingreso/{id}")
    Call<FuenteIngresoDto> update(@Path("id") Integer id, @Body FuenteIngresoDto dto);

    @DELETE("/economix/api/fuentes-ingreso/{id}")
    Call<Void> delete(@Path("id") Integer id);
}

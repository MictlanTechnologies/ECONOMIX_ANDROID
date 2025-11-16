package com.example.economix_android.network.api;

import com.example.economix_android.network.dto.PresupuestoDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PresupuestoApi {
    @GET("/economix/api/presupuestos")
    Call<List<PresupuestoDto>> getAll();

    @GET("/economix/api/presupuestos/{id}")
    Call<PresupuestoDto> getById(@Path("id") Integer id);

    @POST("/economix/api/presupuestos")
    Call<PresupuestoDto> create(@Body PresupuestoDto dto);

    @PUT("/economix/api/presupuestos/{id}")
    Call<PresupuestoDto> update(@Path("id") Integer id, @Body PresupuestoDto dto);

    @DELETE("/economix/api/presupuestos/{id}")
    Call<Void> delete(@Path("id") Integer id);
}

package com.example.economix_android.network.api;

import com.example.economix_android.network.dto.GastoDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GastoApi {
    @GET("/economix/api/gastos")
    Call<List<GastoDto>> getAll();

    @GET("/economix/api/gastos/{id}")
    Call<GastoDto> getById(@Path("id") Integer id);

    @POST("/economix/api/gastos")
    Call<GastoDto> create(@Body GastoDto dto);

    @PUT("/economix/api/gastos/{id}")
    Call<GastoDto> update(@Path("id") Integer id, @Body GastoDto dto);

    @DELETE("/economix/api/gastos/{id}")
    Call<Void> delete(@Path("id") Integer id);
}

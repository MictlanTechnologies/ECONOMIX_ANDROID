package com.example.economix_android.network.api;

import com.example.economix_android.network.dto.AhorroDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AhorroApi {
    @GET("/economix/api/ahorros")
    Call<List<AhorroDto>> getAll();

    @GET("/economix/api/ahorros/{id}")
    Call<AhorroDto> getById(@Path("id") Integer id);

    @POST("/economix/api/ahorros")
    Call<AhorroDto> create(@Body AhorroDto dto);

    @PUT("/economix/api/ahorros/{id}")
    Call<AhorroDto> update(@Path("id") Integer id, @Body AhorroDto dto);

    @DELETE("/economix/api/ahorros/{id}")
    Call<Void> delete(@Path("id") Integer id);
}

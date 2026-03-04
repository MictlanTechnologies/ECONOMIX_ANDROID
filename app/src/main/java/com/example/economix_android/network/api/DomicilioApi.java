package com.example.economix_android.network.api;

import com.example.economix_android.network.dto.DomicilioDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface DomicilioApi {
    @GET("/economix/api/domicilios")
    Call<List<DomicilioDto>> getAll();

    @GET("/economix/api/domicilios/{id}")
    Call<DomicilioDto> getById(@Path("id") Integer id);

    @POST("/economix/api/domicilios")
    Call<DomicilioDto> create(@Body DomicilioDto dto);

    @PUT("/economix/api/domicilios/{id}")
    Call<DomicilioDto> update(@Path("id") Integer id, @Body DomicilioDto dto);

    @DELETE("/economix/api/domicilios/{id}")
    Call<Void> delete(@Path("id") Integer id);
}

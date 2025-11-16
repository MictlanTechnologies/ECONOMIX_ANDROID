package com.example.economix_android.network.api;

import com.example.economix_android.network.dto.PersonaDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PersonaApi {
    @GET("/economix/api/personas")
    Call<List<PersonaDto>> getAll();

    @GET("/economix/api/personas/{id}")
    Call<PersonaDto> getById(@Path("id") Integer id);

    @POST("/economix/api/personas")
    Call<PersonaDto> create(@Body PersonaDto dto);

    @PUT("/economix/api/personas/{id}")
    Call<PersonaDto> update(@Path("id") Integer id, @Body PersonaDto dto);

    @DELETE("/economix/api/personas/{id}")
    Call<Void> delete(@Path("id") Integer id);
}

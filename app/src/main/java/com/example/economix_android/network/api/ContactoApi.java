package com.example.economix_android.network.api;

import com.example.economix_android.network.dto.ContactoDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ContactoApi {
    @GET("/economix/api/contactos")
    Call<List<ContactoDto>> getAll();

    @GET("/economix/api/contactos/{id}")
    Call<ContactoDto> getById(@Path("id") Integer id);

    @POST("/economix/api/contactos")
    Call<ContactoDto> create(@Body ContactoDto dto);

    @PUT("/economix/api/contactos/{id}")
    Call<ContactoDto> update(@Path("id") Integer id, @Body ContactoDto dto);

    @DELETE("/economix/api/contactos/{id}")
    Call<Void> delete(@Path("id") Integer id);
}

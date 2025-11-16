package com.example.economix_android.network.api;

import com.example.economix_android.network.dto.UsuarioDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsuarioApi {
    @GET("/economix/api/usuarios")
    Call<List<UsuarioDto>> getAll();

    @GET("/economix/api/usuarios/{id}")
    Call<UsuarioDto> getById(@Path("id") Integer id);

    @POST("/economix/api/usuarios")
    Call<UsuarioDto> create(@Body UsuarioDto dto);

    @PUT("/economix/api/usuarios/{id}")
    Call<UsuarioDto> update(@Path("id") Integer id, @Body UsuarioDto dto);

    @DELETE("/economix/api/usuarios/{id}")
    Call<Void> delete(@Path("id") Integer id);
}

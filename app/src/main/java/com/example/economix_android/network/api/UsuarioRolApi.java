package com.example.economix_android.network.api;

import com.example.economix_android.network.dto.UsuarioRolDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsuarioRolApi {
    @GET("/economix/api/usuario-roles")
    Call<List<UsuarioRolDto>> getAll();

    @GET("/economix/api/usuario-roles/{id}")
    Call<UsuarioRolDto> getById(@Path("id") Integer id);

    @POST("/economix/api/usuario-roles")
    Call<UsuarioRolDto> create(@Body UsuarioRolDto dto);

    @PUT("/economix/api/usuario-roles/{id}")
    Call<UsuarioRolDto> update(@Path("id") Integer id, @Body UsuarioRolDto dto);

    @DELETE("/economix/api/usuario-roles/{id}")
    Call<Void> delete(@Path("id") Integer id);
}

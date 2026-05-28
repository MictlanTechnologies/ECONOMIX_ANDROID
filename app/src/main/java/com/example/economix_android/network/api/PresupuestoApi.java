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
import retrofit2.http.Query;

public interface PresupuestoApi {
    @GET("/economix/api/presupuestos")
    Call<List<PresupuestoDto>> getAll();

    @GET("/economix/api/presupuestos/{id}")
    Call<PresupuestoDto> getById(@Path("id") Integer id);

    @GET("/economix/api/presupuestos/usuario/{idUsuario}")
    Call<List<PresupuestoDto>> getByUsuario(@Path("idUsuario") Integer idUsuario);

    @GET("/economix/api/presupuestos/usuario/{idUsuario}/periodo")
    Call<List<PresupuestoDto>> getByUsuarioPeriodo(@Path("idUsuario") Integer idUsuario,
                                                   @Query("mes") Integer mes,
                                                   @Query("anio") Integer anio);

    @GET("/economix/api/presupuestos/usuario/{idUsuario}/categoria")
    Call<PresupuestoDto> getByUsuarioCategoriaPeriodo(@Path("idUsuario") Integer idUsuario,
                                                      @Query("categoria") String categoria,
                                                      @Query("mes") Integer mes,
                                                      @Query("anio") Integer anio);

    @POST("/economix/api/presupuestos")
    Call<PresupuestoDto> create(@Body PresupuestoDto dto);

    @PUT("/economix/api/presupuestos/{id}")
    Call<PresupuestoDto> update(@Path("id") Integer id, @Body PresupuestoDto dto);

    @DELETE("/economix/api/presupuestos/{id}")
    Call<Void> delete(@Path("id") Integer id);
}

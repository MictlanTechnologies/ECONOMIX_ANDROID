package com.example.economix_android.network.api;

import com.example.economix_android.network.dto.AsignacionRequestDto;
import com.example.economix_android.network.dto.CategoriaPresupuestoDto;
import com.example.economix_android.network.dto.IngresoDisponibleDto;
import com.example.economix_android.network.dto.PresupuestoResumenDto;

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

    @GET("/api/presupuestos/resumen")
    Call<List<PresupuestoResumenDto>> getResumen(@Query("mes") int mes, @Query("anio") int anio);

    @GET("/api/presupuestos/categorias")
    Call<List<CategoriaPresupuestoDto>> getCategorias();

    @POST("/api/presupuestos/categorias")
    Call<CategoriaPresupuestoDto> createCategoria(@Body CategoriaPresupuestoDto dto);

    @PUT("/api/presupuestos/categorias/{id}")
    Call<CategoriaPresupuestoDto> updateCategoria(@Path("id") Integer id, @Body CategoriaPresupuestoDto dto);

    @DELETE("/api/presupuestos/categorias/{id}")
    Call<Void> deleteCategoria(@Path("id") Integer id);

    @POST("/api/presupuestos/asignaciones")
    Call<Void> addAsignacion(@Body AsignacionRequestDto requestDto);

    @GET("/api/presupuestos/ingresos-disponibles")
    Call<List<IngresoDisponibleDto>> getIngresosDisponibles(@Query("mes") int mes, @Query("anio") int anio);
}

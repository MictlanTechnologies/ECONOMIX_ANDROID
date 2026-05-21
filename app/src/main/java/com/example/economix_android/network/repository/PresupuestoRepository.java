package com.example.economix_android.network.repository;

import com.example.economix_android.network.ApiClient;
import com.example.economix_android.network.api.PresupuestoApi;
import com.example.economix_android.network.dto.PresupuestoDto;

import java.util.List;

import retrofit2.Callback;

public class PresupuestoRepository {

    private final PresupuestoApi presupuestoApi = ApiClient.getPresupuestoApi();

    public void obtenerPresupuestos(Callback<List<PresupuestoDto>> callback) {
        presupuestoApi.getAll().enqueue(callback);
    }

    public void obtenerPresupuestosPorUsuario(Integer idUsuario, Callback<List<PresupuestoDto>> callback) {
        presupuestoApi.getByUsuario(idUsuario).enqueue(callback);
    }

    public void obtenerPresupuestosPorPeriodo(Integer idUsuario, Integer mes, Integer anio, Callback<List<PresupuestoDto>> callback) {
        presupuestoApi.getByUsuarioPeriodo(idUsuario, mes, anio).enqueue(callback);
    }

    public void obtenerPresupuestoCategoria(Integer idUsuario, String categoria, Integer mes, Integer anio, Callback<PresupuestoDto> callback) {
        presupuestoApi.getByUsuarioCategoriaPeriodo(idUsuario, categoria, mes, anio).enqueue(callback);
    }

    public void guardarPresupuesto(PresupuestoDto dto, Callback<PresupuestoDto> callback) {
        presupuestoApi.create(dto).enqueue(callback);
    }

    public void actualizarPresupuesto(Integer id, PresupuestoDto dto, Callback<PresupuestoDto> callback) {
        presupuestoApi.update(id, dto).enqueue(callback);
    }

    public void eliminarPresupuesto(Integer id, Callback<Void> callback) {
        presupuestoApi.delete(id).enqueue(callback);
    }
}

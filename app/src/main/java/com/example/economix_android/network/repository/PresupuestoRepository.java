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

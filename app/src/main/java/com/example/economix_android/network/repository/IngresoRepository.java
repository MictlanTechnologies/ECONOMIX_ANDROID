package com.example.economix_android.network.repository;

import com.example.economix_android.network.ApiClient;
import com.example.economix_android.network.api.IngresoApi;
import com.example.economix_android.network.dto.IngresoDto;

import java.util.List;

import retrofit2.Callback;

public class IngresoRepository {

    private final IngresoApi ingresoApi = ApiClient.getIngresoApi();

    public void obtenerIngresos(Callback<List<IngresoDto>> callback) {
        ingresoApi.getAll().enqueue(callback);
    }

    public void guardarIngreso(IngresoDto dto, Callback<IngresoDto> callback) {
        ingresoApi.create(dto).enqueue(callback);
    }

    public void actualizarIngreso(Integer id, IngresoDto dto, Callback<IngresoDto> callback) {
        ingresoApi.update(id, dto).enqueue(callback);
    }

    public void eliminarIngreso(Integer id, Callback<Void> callback) {
        ingresoApi.delete(id).enqueue(callback);
    }
}

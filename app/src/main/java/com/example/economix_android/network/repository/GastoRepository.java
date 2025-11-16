package com.example.economix_android.network.repository;

import com.example.economix_android.network.ApiClient;
import com.example.economix_android.network.api.GastoApi;
import com.example.economix_android.network.dto.GastoDto;

import java.util.List;

import retrofit2.Callback;

public class GastoRepository {

    private final GastoApi gastoApi = ApiClient.getGastoApi();

    public void obtenerGastos(Callback<List<GastoDto>> callback) {
        gastoApi.getAll().enqueue(callback);
    }

    public void guardarGasto(GastoDto dto, Callback<GastoDto> callback) {
        gastoApi.create(dto).enqueue(callback);
    }

    public void actualizarGasto(Integer id, GastoDto dto, Callback<GastoDto> callback) {
        gastoApi.update(id, dto).enqueue(callback);
    }

    public void eliminarGasto(Integer id, Callback<Void> callback) {
        gastoApi.delete(id).enqueue(callback);
    }
}

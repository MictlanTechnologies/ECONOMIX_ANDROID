package com.example.economix_android.network.repository;

import com.example.economix_android.network.ApiClient;
import com.example.economix_android.network.api.AhorroApi;
import com.example.economix_android.network.dto.AhorroDto;

import java.util.List;

import retrofit2.Callback;

public class AhorroRepository {

    private final AhorroApi ahorroApi = ApiClient.getAhorroApi();

    public void obtenerAhorros(Callback<List<AhorroDto>> callback) {
        ahorroApi.getAll().enqueue(callback);
    }

    public void guardarAhorro(AhorroDto dto, Callback<AhorroDto> callback) {
        ahorroApi.create(dto).enqueue(callback);
    }

    public void eliminarAhorro(Integer id, Callback<Void> callback) {
        ahorroApi.delete(id).enqueue(callback);
    }
}

package com.example.economix_android.network.repository;

import com.example.economix_android.network.ApiClient;
import com.example.economix_android.network.api.EstadoApi;
import com.example.economix_android.network.dto.EstadoDto;

import java.util.List;

import retrofit2.Callback;

public class EstadoRepository {

    private final EstadoApi estadoApi = ApiClient.getEstadoApi();

    public void obtenerEstados(Callback<List<EstadoDto>> callback) {
        estadoApi.getAll().enqueue(callback);
    }
}

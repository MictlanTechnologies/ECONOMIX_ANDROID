package com.example.economix_android.network.repository;

import com.example.economix_android.network.ApiClient;
import com.example.economix_android.network.api.DomicilioApi;
import com.example.economix_android.network.dto.DomicilioDto;

import java.util.List;

import retrofit2.Callback;

public class DomicilioRepository {

    private final DomicilioApi domicilioApi = ApiClient.getDomicilioApi();

    public void obtenerDomicilios(Callback<List<DomicilioDto>> callback) {
        domicilioApi.getAll().enqueue(callback);
    }

    public void crearDomicilio(DomicilioDto dto, Callback<DomicilioDto> callback) {
        domicilioApi.create(dto).enqueue(callback);
    }

    public void actualizarDomicilio(Integer id, DomicilioDto dto, Callback<DomicilioDto> callback) {
        domicilioApi.update(id, dto).enqueue(callback);
    }

    public void eliminarDomicilio(Integer id, Callback<Void> callback) {
        domicilioApi.delete(id).enqueue(callback);
    }
}

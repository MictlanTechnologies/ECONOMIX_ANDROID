package com.example.economix_android.network.repository;

import com.example.economix_android.network.ApiClient;
import com.example.economix_android.network.api.ConceptoGastoApi;
import com.example.economix_android.network.dto.ConceptoGastoDto;

import java.util.List;

import retrofit2.Callback;

public class ConceptoGastoRepository {

    private final ConceptoGastoApi conceptoGastoApi = ApiClient.getConceptoGastoApi();

    public void obtenerConceptos(Callback<List<ConceptoGastoDto>> callback) {
        conceptoGastoApi.getAll().enqueue(callback);
    }

    public void guardarConcepto(ConceptoGastoDto dto, Callback<ConceptoGastoDto> callback) {
        conceptoGastoApi.create(dto).enqueue(callback);
    }

    public void actualizarConcepto(Integer id, ConceptoGastoDto dto, Callback<ConceptoGastoDto> callback) {
        conceptoGastoApi.update(id, dto).enqueue(callback);
    }

    public void eliminarConcepto(Integer id, Callback<Void> callback) {
        conceptoGastoApi.delete(id).enqueue(callback);
    }
}

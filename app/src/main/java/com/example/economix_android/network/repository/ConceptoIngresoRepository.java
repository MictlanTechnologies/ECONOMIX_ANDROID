package com.example.economix_android.network.repository;

import com.example.economix_android.network.ApiClient;
import com.example.economix_android.network.api.ConceptoIngresoApi;
import com.example.economix_android.network.dto.ConceptoIngresoDto;

import java.util.List;

import retrofit2.Callback;

public class ConceptoIngresoRepository {

    private final ConceptoIngresoApi conceptoIngresoApi = ApiClient.getConceptoIngresoApi();

    public void obtenerConceptos(Callback<List<ConceptoIngresoDto>> callback) {
        conceptoIngresoApi.getAll().enqueue(callback);
    }

    public void guardarConcepto(ConceptoIngresoDto dto, Callback<ConceptoIngresoDto> callback) {
        conceptoIngresoApi.create(dto).enqueue(callback);
    }

    public void actualizarConcepto(Integer id, ConceptoIngresoDto dto, Callback<ConceptoIngresoDto> callback) {
        conceptoIngresoApi.update(id, dto).enqueue(callback);
    }

    public void eliminarConcepto(Integer id, Callback<Void> callback) {
        conceptoIngresoApi.delete(id).enqueue(callback);
    }
}

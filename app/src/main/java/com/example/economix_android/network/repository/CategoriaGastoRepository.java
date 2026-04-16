package com.example.economix_android.network.repository;

import com.example.economix_android.network.ApiClient;
import com.example.economix_android.network.api.CategoriaGastoApi;
import com.example.economix_android.network.dto.CategoriaGastoDto;

import java.util.List;

import retrofit2.Callback;

public class CategoriaGastoRepository {

    private final CategoriaGastoApi categoriaGastoApi = ApiClient.getCategoriaGastoApi();

    public void obtenerCategorias(Callback<List<CategoriaGastoDto>> callback) {
        categoriaGastoApi.getAll().enqueue(callback);
    }

    public void guardarCategoria(CategoriaGastoDto dto, Callback<CategoriaGastoDto> callback) {
        categoriaGastoApi.create(dto).enqueue(callback);
    }

    public void actualizarCategoria(Integer id, CategoriaGastoDto dto, Callback<CategoriaGastoDto> callback) {
        categoriaGastoApi.update(id, dto).enqueue(callback);
    }

    public void eliminarCategoria(Integer id, Callback<Void> callback) {
        categoriaGastoApi.delete(id).enqueue(callback);
    }
}

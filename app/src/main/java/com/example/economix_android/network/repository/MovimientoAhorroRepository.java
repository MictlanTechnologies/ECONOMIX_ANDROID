package com.example.economix_android.network.repository;

import com.example.economix_android.network.ApiClient;
import com.example.economix_android.network.api.MovimientoAhorroApi;
import com.example.economix_android.network.dto.MovimientoAhorroDto;

import java.util.List;

import retrofit2.Callback;

public class MovimientoAhorroRepository {

    private final MovimientoAhorroApi movimientoAhorroApi = ApiClient.getMovimientoAhorroApi();

    public void obtenerMovimientos(Callback<List<MovimientoAhorroDto>> callback) {
        movimientoAhorroApi.getAll().enqueue(callback);
    }

    public void guardarMovimiento(MovimientoAhorroDto dto, Callback<MovimientoAhorroDto> callback) {
        movimientoAhorroApi.create(dto).enqueue(callback);
    }

    public void actualizarMovimiento(Integer id, MovimientoAhorroDto dto, Callback<MovimientoAhorroDto> callback) {
        movimientoAhorroApi.update(id, dto).enqueue(callback);
    }

    public void eliminarMovimiento(Integer id, Callback<Void> callback) {
        movimientoAhorroApi.delete(id).enqueue(callback);
    }
}

package com.example.economix_android.network.repository;

import com.example.economix_android.network.ApiClient;
import com.example.economix_android.network.api.PresupuestoApi;
import com.example.economix_android.network.dto.AsignacionRequestDto;
import com.example.economix_android.network.dto.CategoriaPresupuestoDto;
import com.example.economix_android.network.dto.IngresoDisponibleDto;
import com.example.economix_android.network.dto.PresupuestoResumenDto;

import java.util.List;

import retrofit2.Callback;

public class PresupuestoRepository {

    private final PresupuestoApi presupuestoApi = ApiClient.getPresupuestoApi();

    public void obtenerResumen(int mes, int anio, Callback<List<PresupuestoResumenDto>> callback) {
        presupuestoApi.getResumen(mes, anio).enqueue(callback);
    }

    public void obtenerCategorias(Callback<List<CategoriaPresupuestoDto>> callback) {
        presupuestoApi.getCategorias().enqueue(callback);
    }

    public void crearCategoria(CategoriaPresupuestoDto dto, Callback<CategoriaPresupuestoDto> callback) {
        presupuestoApi.createCategoria(dto).enqueue(callback);
    }

    public void editarCategoria(Integer id, CategoriaPresupuestoDto dto, Callback<CategoriaPresupuestoDto> callback) {
        presupuestoApi.updateCategoria(id, dto).enqueue(callback);
    }

    public void eliminarCategoria(Integer id, Callback<Void> callback) {
        presupuestoApi.deleteCategoria(id).enqueue(callback);
    }

    public void obtenerIngresosDisponibles(int mes, int anio, Callback<List<IngresoDisponibleDto>> callback) {
        presupuestoApi.getIngresosDisponibles(mes, anio).enqueue(callback);
    }

    public void asignar(AsignacionRequestDto dto, Callback<Void> callback) {
        presupuestoApi.addAsignacion(dto).enqueue(callback);
    }
}

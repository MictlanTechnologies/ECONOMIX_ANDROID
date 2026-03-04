package com.example.economix_android.Model.ingresos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.economix_android.network.dto.IngresoDto;
import com.example.economix_android.network.repository.IngresoRepository;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ingresosViewModel extends ViewModel {

    private final MutableLiveData<List<IngresoDto>> ingresos = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);
    private final IngresoRepository ingresoRepository = new IngresoRepository();

    public ingresosViewModel() {
        cargarIngresos();
    }

    public LiveData<List<IngresoDto>> getIngresos() {
        return ingresos;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void cargarIngresos() {
        loading.setValue(true);
        ingresoRepository.obtenerIngresos(new Callback<List<IngresoDto>>() {
            @Override
            public void onResponse(Call<List<IngresoDto>> call, Response<List<IngresoDto>> response) {
                loading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ingresos.postValue(response.body());
                    error.postValue(null);
                } else {
                    error.postValue("Error al obtener ingresos: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<IngresoDto>> call, Throwable t) {
                loading.postValue(false);
                error.postValue("No se pudo conectar al backend: " + t.getMessage());
            }
        });
    }

    public void guardarIngreso(IngresoDto dto) {
        loading.setValue(true);
        ingresoRepository.guardarIngreso(dto, new Callback<IngresoDto>() {
            @Override
            public void onResponse(Call<IngresoDto> call, Response<IngresoDto> response) {
                loading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    cargarIngresos();
                } else {
                    error.postValue("Error al guardar ingreso: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<IngresoDto> call, Throwable t) {
                loading.postValue(false);
                error.postValue("No se pudo guardar el ingreso: " + t.getMessage());
            }
        });
    }
}
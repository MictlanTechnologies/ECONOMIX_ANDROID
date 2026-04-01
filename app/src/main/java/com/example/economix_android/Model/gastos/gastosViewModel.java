package com.example.economix_android.Model.gastos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.economix_android.network.dto.GastoDto;
import com.example.economix_android.network.repository.GastoRepository;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class gastosViewModel extends ViewModel {

    private final MutableLiveData<List<GastoDto>> gastos = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);
    private final GastoRepository gastoRepository = new GastoRepository();

    public gastosViewModel() {
        cargarGastos();
    }

    public LiveData<List<GastoDto>> getGastos() {
        return gastos;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void cargarGastos() {
        loading.setValue(true);
        gastoRepository.obtenerGastos(new Callback<List<GastoDto>>() {
            @Override
            public void onResponse(Call<List<GastoDto>> call, Response<List<GastoDto>> response) {
                loading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    gastos.postValue(response.body());
                    error.postValue(null);
                } else {
                    error.postValue("Error al obtener gastos: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<GastoDto>> call, Throwable t) {
                loading.postValue(false);
                error.postValue("No se pudo conectar al backend: " + t.getMessage());
            }
        });
    }

    public void guardarGasto(GastoDto dto) {
        loading.setValue(true);
        gastoRepository.guardarGasto(dto, new Callback<GastoDto>() {
            @Override
            public void onResponse(Call<GastoDto> call, Response<GastoDto> response) {
                loading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    cargarGastos();
                } else {
                    error.postValue("Error al guardar gasto: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GastoDto> call, Throwable t) {
                loading.postValue(false);
                error.postValue("No se pudo guardar el gasto: " + t.getMessage());
            }
        });
    }
}
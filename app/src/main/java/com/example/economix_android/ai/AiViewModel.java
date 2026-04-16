package com.example.economix_android.ai;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiViewModel extends ViewModel {

    private final AiRepository repository = new AiRepository();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);
    private final MutableLiveData<AnalysisData> data = new MutableLiveData<>(null);

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<AnalysisData> getData() {
        return data;
    }

    public void analyze(Integer userId,
                        int horizonDays,
                        LocalDate from,
                        LocalDate to,
                        LocalDate fromA,
                        LocalDate toA,
                        LocalDate fromB,
                        LocalDate toB) {
        loading.setValue(true);
        error.setValue(null);

        AnalysisData result = new AnalysisData();
        AtomicInteger pending = new AtomicInteger(5);

        Runnable done = () -> {
            if (pending.decrementAndGet() == 0) {
                loading.postValue(false);
                data.postValue(result);
            }
        };

        repository.getSpendPrediction(userId, horizonDays, new Callback<AiModels.SpendForecastResponse>() {
            @Override
            public void onResponse(Call<AiModels.SpendForecastResponse> call, Response<AiModels.SpendForecastResponse> response) {
                if (response.isSuccessful()) result.spendForecast = response.body();
                else error.postValue("Error en predicción de gasto: " + response.code());
                done.run();
            }

            @Override
            public void onFailure(Call<AiModels.SpendForecastResponse> call, Throwable t) {
                error.postValue("Sin conexión para predicción de gasto.");
                done.run();
            }
        });

        LocalDate now = LocalDate.now();
        repository.getBudgetRisk(userId, now.getMonthValue(), now.getYear(), new Callback<AiModels.BudgetRiskResponse>() {
            @Override
            public void onResponse(Call<AiModels.BudgetRiskResponse> call, Response<AiModels.BudgetRiskResponse> response) {
                if (response.isSuccessful()) result.budgetRisk = response.body();
                else error.postValue("Error en riesgo de presupuesto: " + response.code());
                done.run();
            }

            @Override
            public void onFailure(Call<AiModels.BudgetRiskResponse> call, Throwable t) {
                error.postValue("Sin conexión para riesgo de presupuesto.");
                done.run();
            }
        });

        repository.getAnomalies(userId, from, to, new Callback<AiModels.AnomalyResponse>() {
            @Override
            public void onResponse(Call<AiModels.AnomalyResponse> call, Response<AiModels.AnomalyResponse> response) {
                if (response.isSuccessful()) result.anomalies = response.body();
                else error.postValue("Error en anomalías: " + response.code());
                done.run();
            }

            @Override
            public void onFailure(Call<AiModels.AnomalyResponse> call, Throwable t) {
                error.postValue("Sin conexión para anomalías.");
                done.run();
            }
        });

        repository.getConfidenceInterval(userId, from, to, new Callback<AiModels.ConfidenceIntervalResponse>() {
            @Override
            public void onResponse(Call<AiModels.ConfidenceIntervalResponse> call, Response<AiModels.ConfidenceIntervalResponse> response) {
                if (response.isSuccessful()) result.confidenceInterval = response.body();
                else error.postValue("Error en intervalo de confianza: " + response.code());
                done.run();
            }

            @Override
            public void onFailure(Call<AiModels.ConfidenceIntervalResponse> call, Throwable t) {
                error.postValue("Sin conexión para IC de media.");
                done.run();
            }
        });

        AiModels.CompareMeansRequest request = new AiModels.CompareMeansRequest();
        request.setUserId(userId);
        request.setFromA(fromA);
        request.setToA(toA);
        request.setFromB(fromB);
        request.setToB(toB);
        request.setAlpha(new java.math.BigDecimal("0.05"));

        repository.compareMeans(request, new Callback<AiModels.HypothesisTestResponse>() {
            @Override
            public void onResponse(Call<AiModels.HypothesisTestResponse> call, Response<AiModels.HypothesisTestResponse> response) {
                if (response.isSuccessful()) result.hypothesisTest = response.body();
                else error.postValue("Error en prueba de hipótesis: " + response.code());
                done.run();
            }

            @Override
            public void onFailure(Call<AiModels.HypothesisTestResponse> call, Throwable t) {
                error.postValue("Sin conexión para prueba de hipótesis.");
                done.run();
            }
        });
    }

    public static class AnalysisData {
        public AiModels.SpendForecastResponse spendForecast;
        public AiModels.BudgetRiskResponse budgetRisk;
        public AiModels.AnomalyResponse anomalies;
        public AiModels.ConfidenceIntervalResponse confidenceInterval;
        public AiModels.HypothesisTestResponse hypothesisTest;
    }
}

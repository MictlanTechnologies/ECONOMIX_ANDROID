package com.example.economix_android.ai;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiViewModel extends ViewModel {

    public enum AnalysisStatus {
        IDLE,
        LOADING,
        SUCCESS,
        INSUFFICIENT_DATA,
        UNAUTHORIZED,
        PROVIDER_UNAVAILABLE,
        NETWORK_ERROR,
        PARTIAL_ERROR
    }

    private final AiRepository repository = new AiRepository();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);
    private final MutableLiveData<AnalysisData> data = new MutableLiveData<>(null);
    private final MutableLiveData<AnalysisStatus> status = new MutableLiveData<>(AnalysisStatus.IDLE);

    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }
    public LiveData<AnalysisData> getData() { return data; }
    public LiveData<AnalysisStatus> getStatus() { return status; }

    public void analyze(int horizonDays,
                        LocalDate from,
                        LocalDate to,
                        LocalDate fromA,
                        LocalDate toA,
                        LocalDate fromB,
                        LocalDate toB) {
        loading.setValue(true);
        error.setValue(null);
        status.setValue(AnalysisStatus.LOADING);

        AnalysisData result = new AnalysisData();
        AtomicInteger pending = new AtomicInteger(5);
        RequestFlags flags = new RequestFlags();

        Runnable done = () -> {
            if (pending.decrementAndGet() == 0) {
                loading.postValue(false);
                data.postValue(result);
                status.postValue(flags.resolveStatus());
            }
        };

        repository.getSpendPrediction(horizonDays, new Callback<AiModels.SpendForecastResponse>() {
            @Override
            public void onResponse(Call<AiModels.SpendForecastResponse> call, Response<AiModels.SpendForecastResponse> response) {
                if (response.isSuccessful()) {
                    result.spendForecast = response.body();
                    flags.captureBusinessStatus(response.body() != null ? response.body().getStatus() : null);
                } else {
                    flags.captureHttpError(response.code());
                }
                done.run();
            }

            @Override
            public void onFailure(Call<AiModels.SpendForecastResponse> call, Throwable t) {
                flags.networkError = true;
                done.run();
            }
        });

        LocalDate now = LocalDate.now();
        repository.getBudgetRisk(now.getMonthValue(), now.getYear(), new Callback<AiModels.BudgetRiskResponse>() {
            @Override
            public void onResponse(Call<AiModels.BudgetRiskResponse> call, Response<AiModels.BudgetRiskResponse> response) {
                if (response.isSuccessful()) {
                    result.budgetRisk = response.body();
                    flags.captureBusinessStatus(response.body() != null ? response.body().getStatus() : null);
                } else {
                    flags.captureHttpError(response.code());
                }
                done.run();
            }

            @Override
            public void onFailure(Call<AiModels.BudgetRiskResponse> call, Throwable t) {
                flags.networkError = true;
                done.run();
            }
        });

        repository.getAnomalies(from, to, new Callback<AiModels.AnomalyResponse>() {
            @Override
            public void onResponse(Call<AiModels.AnomalyResponse> call, Response<AiModels.AnomalyResponse> response) {
                if (response.isSuccessful()) {
                    result.anomalies = response.body();
                    flags.captureBusinessStatus(response.body() != null ? response.body().getStatus() : null);
                } else {
                    flags.captureHttpError(response.code());
                }
                done.run();
            }

            @Override
            public void onFailure(Call<AiModels.AnomalyResponse> call, Throwable t) {
                flags.networkError = true;
                done.run();
            }
        });

        repository.getConfidenceInterval(from, to, new Callback<AiModels.ConfidenceIntervalResponse>() {
            @Override
            public void onResponse(Call<AiModels.ConfidenceIntervalResponse> call, Response<AiModels.ConfidenceIntervalResponse> response) {
                if (response.isSuccessful()) {
                    result.confidenceInterval = response.body();
                    flags.captureBusinessStatus(response.body() != null ? response.body().getStatus() : null);
                } else {
                    flags.captureHttpError(response.code());
                }
                done.run();
            }

            @Override
            public void onFailure(Call<AiModels.ConfidenceIntervalResponse> call, Throwable t) {
                flags.networkError = true;
                done.run();
            }
        });

        AiModels.CompareMeansRequest request = new AiModels.CompareMeansRequest();
        request.setFromA(fromA);
        request.setToA(toA);
        request.setFromB(fromB);
        request.setToB(toB);
        request.setAlpha(new java.math.BigDecimal("0.05"));

        repository.compareMeans(request, new Callback<AiModels.HypothesisTestResponse>() {
            @Override
            public void onResponse(Call<AiModels.HypothesisTestResponse> call, Response<AiModels.HypothesisTestResponse> response) {
                if (response.isSuccessful()) {
                    result.hypothesisTest = response.body();
                    flags.captureBusinessStatus(response.body() != null ? response.body().getStatus() : null);
                } else {
                    flags.captureHttpError(response.code());
                }
                done.run();
            }

            @Override
            public void onFailure(Call<AiModels.HypothesisTestResponse> call, Throwable t) {
                flags.networkError = true;
                done.run();
            }
        });
    }

    private final class RequestFlags {
        boolean unauthorized;
        boolean providerUnavailable;
        boolean networkError;
        boolean partialError;
        boolean insufficientData;

        void captureHttpError(int code) {
            partialError = true;
            if (code == 401) unauthorized = true;
            if (code == 503) providerUnavailable = true;
        }

        void captureBusinessStatus(String businessStatus) {
            if ("INSUFFICIENT_DATA".equalsIgnoreCase(businessStatus)) {
                insufficientData = true;
            }
        }

        AnalysisStatus resolveStatus() {
            if (unauthorized) return AnalysisStatus.UNAUTHORIZED;
            if (providerUnavailable) return AnalysisStatus.PROVIDER_UNAVAILABLE;
            if (networkError) return AnalysisStatus.NETWORK_ERROR;
            if (insufficientData) return AnalysisStatus.INSUFFICIENT_DATA;
            if (partialError) return AnalysisStatus.PARTIAL_ERROR;
            return AnalysisStatus.SUCCESS;
        }
    }

    public static class AnalysisData {
        public AiModels.SpendForecastResponse spendForecast;
        public AiModels.BudgetRiskResponse budgetRisk;
        public AiModels.AnomalyResponse anomalies;
        public AiModels.ConfidenceIntervalResponse confidenceInterval;
        public AiModels.HypothesisTestResponse hypothesisTest;

        public List<String> recommendations() {
            List<String> all = new ArrayList<>();
            collect(all, spendForecast != null ? spendForecast.getRecommendations() : null);
            collect(all, budgetRisk != null ? budgetRisk.getRecommendations() : null);
            collect(all, anomalies != null ? anomalies.getRecommendations() : null);
            collect(all, confidenceInterval != null ? confidenceInterval.getRecommendations() : null);
            collect(all, hypothesisTest != null ? hypothesisTest.getRecommendations() : null);
            return all;
        }

        private void collect(List<String> all, List<String> source) {
            if (source == null) return;
            for (String item : source) {
                if (item != null && !item.trim().isEmpty() && !all.contains(item.trim())) {
                    all.add(item.trim());
                }
            }
        }
    }
}

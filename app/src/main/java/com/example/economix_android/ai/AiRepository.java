package com.example.economix_android.ai;

import com.example.economix_android.network.ApiClient;

import java.time.LocalDate;

import retrofit2.Callback;

public class AiRepository {

    private final AiApi aiApi = ApiClient.getAiApi();

    public void getSummary(Integer userId, LocalDate from, LocalDate to, Callback<AiModels.AISummaryResponse> callback) {
        aiApi.getSummary(userId, from, to).enqueue(callback);
    }

    public void getSpendPrediction(Integer userId, Integer horizonDays, Callback<AiModels.SpendForecastResponse> callback) {
        aiApi.getSpendPrediction(userId, null, horizonDays).enqueue(callback);
    }

    public void getBudgetRisk(Integer userId, Integer month, Integer year, Callback<AiModels.BudgetRiskResponse> callback) {
        aiApi.getBudgetRisk(userId, month, year).enqueue(callback);
    }

    public void getAnomalies(Integer userId, LocalDate from, LocalDate to, Callback<AiModels.AnomalyResponse> callback) {
        aiApi.getAnomalies(userId, from, to).enqueue(callback);
    }

    public void getConfidenceInterval(Integer userId, LocalDate from, LocalDate to, Callback<AiModels.ConfidenceIntervalResponse> callback) {
        aiApi.getConfidenceInterval(userId, from, to, null, 0.95d).enqueue(callback);
    }

    public void compareMeans(AiModels.CompareMeansRequest request, Callback<AiModels.HypothesisTestResponse> callback) {
        aiApi.compareMeans(request).enqueue(callback);
    }
}

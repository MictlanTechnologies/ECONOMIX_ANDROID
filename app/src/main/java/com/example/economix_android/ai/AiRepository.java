package com.example.economix_android.ai;

import com.example.economix_android.network.ApiClient;

import java.time.LocalDate;

import retrofit2.Callback;

public class AiRepository {

    private final AiApi aiApi = ApiClient.getAiApi();

    public void getSummary(LocalDate from, LocalDate to, Callback<AiModels.AISummaryResponse> callback) {
        aiApi.getSummary(from, to).enqueue(callback);
    }

    public void getSpendPrediction(Integer horizonDays, Callback<AiModels.SpendForecastResponse> callback) {
        aiApi.getSpendPrediction(null, horizonDays).enqueue(callback);
    }

    public void getBudgetRisk(Integer month, Integer year, Callback<AiModels.BudgetRiskResponse> callback) {
        aiApi.getBudgetRisk(month, year).enqueue(callback);
    }

    public void getAnomalies(LocalDate from, LocalDate to, Callback<AiModels.AnomalyResponse> callback) {
        aiApi.getAnomalies(from, to).enqueue(callback);
    }

    public void getConfidenceInterval(LocalDate from, LocalDate to, Callback<AiModels.ConfidenceIntervalResponse> callback) {
        aiApi.getConfidenceInterval(from, to, null, 0.95d).enqueue(callback);
    }

    public void compareMeans(AiModels.CompareMeansRequest request, Callback<AiModels.HypothesisTestResponse> callback) {
        aiApi.compareMeans(request).enqueue(callback);
    }

    public void chat(AiModels.AiChatRequest request, Callback<AiModels.AiChatResponse> callback) {
        aiApi.chat(request).enqueue(callback);
    }
}

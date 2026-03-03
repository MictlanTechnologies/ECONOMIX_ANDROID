package com.example.economix_android.ai;

import java.time.LocalDate;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AiApi {

    @GET("/economix/api/ai/summary")
    Call<AiModels.AISummaryResponse> getSummary(
            @Query("userId") Integer userId,
            @Query("from") LocalDate from,
            @Query("to") LocalDate to
    );

    @GET("/economix/api/ai/predict/spend")
    Call<AiModels.SpendForecastResponse> getSpendPrediction(
            @Query("userId") Integer userId,
            @Query("categoryId") Integer categoryId,
            @Query("horizonDays") Integer horizonDays
    );

    @GET("/economix/api/ai/predict/budget-risk")
    Call<AiModels.BudgetRiskResponse> getBudgetRisk(
            @Query("userId") Integer userId,
            @Query("month") Integer month,
            @Query("year") Integer year
    );

    @GET("/economix/api/ai/anomalies")
    Call<AiModels.AnomalyResponse> getAnomalies(
            @Query("userId") Integer userId,
            @Query("from") LocalDate from,
            @Query("to") LocalDate to
    );

    @GET("/economix/api/ai/infer/ci-mean")
    Call<AiModels.ConfidenceIntervalResponse> getConfidenceInterval(
            @Query("userId") Integer userId,
            @Query("from") LocalDate from,
            @Query("to") LocalDate to,
            @Query("categoryId") Integer categoryId,
            @Query("confidence") Double confidence
    );

    @POST("/economix/api/ai/infer/compare-means")
    Call<AiModels.HypothesisTestResponse> compareMeans(@Body AiModels.CompareMeansRequest request);
}

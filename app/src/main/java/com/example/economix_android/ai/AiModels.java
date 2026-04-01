package com.example.economix_android.ai;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;

public class AiModels {

    @Data
    public static class AISummaryResponse {
        private String status;
        private List<String> recommendations;
        private BigDecimal totalGastos;
        private BigDecimal totalIngresos;
        private BigDecimal totalAhorros;
        private BigDecimal promedioDiarioGasto;
        private BigDecimal promedioDiarioIngreso;
        private BigDecimal burnRateMes;
        private List<TopEntry> topArticulos;
        private List<TopEntry> topCategorias;
        private List<SavingGoalProgress> progresoMetasAhorro;
    }

    @Data
    public static class TopEntry {
        private String label;
        private BigDecimal total;
    }

    @Data
    public static class SavingGoalProgress {
        private Integer idAhorro;
        private String nombreObjetivo;
        private BigDecimal meta;
        private BigDecimal montoAhorrado;
        private BigDecimal porcentajeProgreso;
    }

    @Data
    public static class SpendForecastResponse {
        private String status;
        private List<String> recommendations;
        private Integer horizonDays;
        private LocalDate trainedUntil;
        private BigDecimal expectedSpend;
        private BigDecimal lower95;
        private BigDecimal upper95;
        private String explanation;
    }

    @Data
    public static class BudgetRiskResponse {
        private String status;
        private List<String> recommendations;
        private List<BudgetRiskItem> items;
    }

    @Data
    public static class BudgetRiskItem {
        private Integer idPresupuesto;
        private Integer idCategoria;
        private String categoria;
        private BigDecimal montoMaximo;
        private BigDecimal montoConsumido;
        private BigDecimal porcentajeConsumido;
        private BigDecimal proyeccionFinMes;
        private String riesgo;
    }

    @Data
    public static class AnomalyResponse {
        private String status;
        private List<String> recommendations;
        private BigDecimal median;
        private BigDecimal mad;
        private List<AnomalyItem> anomalies;
    }

    @Data
    public static class AnomalyItem {
        private Integer idGasto;
        private LocalDate fecha;
        private String articulo;
        private Integer idCategoria;
        private BigDecimal monto;
        private BigDecimal robustZScore;
    }

    @Data
    public static class ConfidenceIntervalResponse {
        private String status;
        private List<String> recommendations;
        private String metricDefinition;
        private Integer sampleSize;
        private BigDecimal mean;
        private BigDecimal confidence;
        private BigDecimal lower;
        private BigDecimal upper;
    }

    @Data
    public static class CompareMeansRequest {
        private LocalDate fromA;
        private LocalDate toA;
        private LocalDate fromB;
        private LocalDate toB;
        private Integer categoryId;
        private BigDecimal alpha;
    }

    @Data
    public static class HypothesisTestResponse {
        private String status;
        private List<String> recommendations;
        private Integer nA;
        private Integer nB;
        private BigDecimal meanA;
        private BigDecimal meanB;
        private BigDecimal differenceMeans;
        private BigDecimal t;
        private BigDecimal df;
        private BigDecimal pValue;
        private String conclusion;
    }

    @Data
    public static class AiChatRequest {
        private String message;
        private LocalDate from;
        private LocalDate to;
        private LocalDate fromA;
        private LocalDate toA;
        private LocalDate fromB;
        private LocalDate toB;
        private Integer horizonDays;
    }

    @Data
    public static class AiChatResponse {
        private String status;
        private String message;
        private List<String> recommendations;
        private AISummaryResponse summary;
        private SpendForecastResponse spendForecast;
        private BudgetRiskResponse budgetRisk;
        private AnomalyResponse anomalies;
        private ConfidenceIntervalResponse confidenceInterval;
        private HypothesisTestResponse hypothesisTest;
    }
}

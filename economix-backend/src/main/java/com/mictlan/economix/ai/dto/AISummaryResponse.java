package com.mictlan.economix.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AISummaryResponse {
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

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TopEntry {
        private String label;
        private BigDecimal total;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SavingGoalProgress {
        private Integer idAhorro;
        private String nombreObjetivo;
        private BigDecimal meta;
        private BigDecimal montoAhorrado;
        private BigDecimal porcentajeProgreso;
    }
}

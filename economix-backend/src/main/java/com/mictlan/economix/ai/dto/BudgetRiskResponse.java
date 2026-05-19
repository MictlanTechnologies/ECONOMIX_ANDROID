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
public class BudgetRiskResponse {
    private String status;
    private List<String> recommendations;
    private List<BudgetRiskItem> items;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
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
}

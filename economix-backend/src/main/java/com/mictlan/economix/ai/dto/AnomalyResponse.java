package com.mictlan.economix.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnomalyResponse {
    private String status;
    private List<String> recommendations;

    private BigDecimal median;
    private BigDecimal mad;
    private List<AnomalyItem> anomalies;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnomalyItem {
        private Integer idGasto;
        private LocalDate fecha;
        private String articulo;
        private Integer idCategoria;
        private BigDecimal monto;
        private BigDecimal robustZScore;
    }
}

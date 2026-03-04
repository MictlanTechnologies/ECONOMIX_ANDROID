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
public class SpendForecastResponse {
    private String status;
    private List<String> recommendations;

    private Integer horizonDays;
    private LocalDate trainedUntil;
    private BigDecimal expectedSpend;
    private BigDecimal lower95;
    private BigDecimal upper95;
    private String explanation;
}

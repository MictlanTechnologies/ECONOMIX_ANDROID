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
public class ConfidenceIntervalResponse {
    private String status;
    private List<String> recommendations;

    private String metricDefinition;
    private Integer sampleSize;
    private BigDecimal mean;
    private BigDecimal confidence;
    private BigDecimal lower;
    private BigDecimal upper;
}

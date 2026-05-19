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
public class HypothesisTestResponse {
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

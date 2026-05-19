package com.mictlan.economix.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompareMeansRequest {
    private Integer userId;
    private LocalDate fromA;
    private LocalDate toA;
    private LocalDate fromB;
    private LocalDate toB;
    private Integer categoryId;
    private BigDecimal alpha;
}

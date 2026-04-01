package com.mictlan.economix.sql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConceptoIngresoDto {
    private Integer idConcepto;
    private String nombreConcepto;
    private String descripcionConcepto;
    private BigDecimal precioConcepto;
    private Integer idIngresos;
}

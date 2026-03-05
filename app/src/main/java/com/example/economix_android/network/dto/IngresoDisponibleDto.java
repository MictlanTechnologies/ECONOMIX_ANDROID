package com.example.economix_android.network.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IngresoDisponibleDto {
    private Integer ingresoId;
    private String nombre;
    private BigDecimal montoTotal;
    private BigDecimal asignado;
    private BigDecimal disponible;
}

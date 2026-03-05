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
public class PresupuestoResumenDto {
    private Integer categoriaId;
    private String nombre;
    private String colorHex;
    private String iconKey;
    private BigDecimal asignado;
    private BigDecimal gastado;
    private BigDecimal disponible;
}

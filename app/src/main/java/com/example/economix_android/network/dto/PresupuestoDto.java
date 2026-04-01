package com.example.economix_android.network.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PresupuestoDto {
    private Integer idPresupuesto;
    private Integer idUsuario;
    private Integer idCategoria;
    private String categoria;
    private BigDecimal montoMaximo;
    private BigDecimal montoGastado;
    private Integer mes;
    private Integer anio;
}

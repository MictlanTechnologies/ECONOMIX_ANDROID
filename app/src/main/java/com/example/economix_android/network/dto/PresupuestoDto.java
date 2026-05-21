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
public class PresupuestoDto {
    private Integer idPresupuesto;
    private Integer idUsuario;
    private Integer idCategoria;
    private String categoria;
    private BigDecimal montoMaximo;
    private BigDecimal montoGastado;
    private BigDecimal montoRestante;
    private BigDecimal porcentajeUso;
    private String estado;
    private Integer mes;
    private Integer anio;
}

package com.example.economix_android.sql.dto;

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
public class GastoDto {
    private Integer idGastos;
    private Integer idUsuario;
    private Integer idCategoria;
    private Integer idPresupuesto;
    private String descripcionGasto;
    private String articuloGasto;
    private BigDecimal montoGasto;
    private LocalDate fechaGastos;
    private String periodoGastos;
}

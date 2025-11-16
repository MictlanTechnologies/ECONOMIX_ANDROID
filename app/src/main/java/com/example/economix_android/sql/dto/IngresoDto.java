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
public class IngresoDto {
    private Integer idIngresos;
    private Integer idUsuario;
    private Integer idFuente;
    private BigDecimal montoIngreso;
    private String periodicidadIngreso;
    private LocalDate fechaIngresos;
    private String descripcionIngreso;
}

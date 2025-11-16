package com.mictlan.economix.sql.dto;

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
public class AhorroDto {
    private Integer idAhorro;
    private Integer idUsuario;
    private String nombreObjetivo;
    private String descripcionObjetivo;
    private BigDecimal meta;
    private BigDecimal montoAhorrado;
    private LocalDate fechaLimite;
}

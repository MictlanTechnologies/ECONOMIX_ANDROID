package com.mictlan.economix.sql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoAhorroDto {
    private Integer idMovimiento;
    private Integer idAhorro;
    private Integer idUsuario;
    private String tipoMovimiento;
    private BigDecimal monto;
    private LocalDateTime fechaMovimiento;
    private String nota;
}

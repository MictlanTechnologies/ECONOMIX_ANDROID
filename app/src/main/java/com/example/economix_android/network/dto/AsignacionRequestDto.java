package com.example.economix_android.network.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionRequestDto {
    private Integer ingresoId;
    private Integer categoriaId;
    private BigDecimal monto;
    private LocalDate fecha;
}

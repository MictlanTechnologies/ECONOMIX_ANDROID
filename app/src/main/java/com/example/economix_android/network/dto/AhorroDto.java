package com.example.economix_android.network.dto;

import com.google.gson.annotations.SerializedName;

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
    private Integer idIngresos;
    private BigDecimal montoAhorro;
    private String periodoTAhorro;
    private LocalDate fechaAhorro;
    @SerializedName(value = "fechaActualizacionA", alternate = {"fechaActualizaciónA"})
    private LocalDate fechaActualizacionA;
}

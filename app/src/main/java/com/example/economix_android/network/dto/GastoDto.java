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
public class GastoDto {
    private Integer idGastos;
    private Integer idUsuario;
    @SerializedName(value = "descripcionGasto", alternate = {"descripciónGasto"})
    private String descripcionGasto;
    @SerializedName(value = "articuloGasto", alternate = {"artículoGasto"})
    private String articuloGasto;
    private BigDecimal montoGasto;
    private LocalDate fechaGastos;
    private String periodoGastos;
}

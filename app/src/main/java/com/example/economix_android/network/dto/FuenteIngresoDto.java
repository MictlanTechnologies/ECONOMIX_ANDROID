package com.example.economix_android.network.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FuenteIngresoDto {
    private Integer idFuente;
    private Integer idUsuario;
    private String nombreFuente;
    private String descripcion;
    private Boolean activo;
}

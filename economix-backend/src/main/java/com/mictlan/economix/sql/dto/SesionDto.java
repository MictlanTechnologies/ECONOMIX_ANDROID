package com.mictlan.economix.sql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SesionDto {
    private Long idSesion;
    private Integer idUsuario;
    private String token;
    private String ipOrigen;
    private String dispositivo;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaUltimoAcceso;
    private Boolean vigente;
}

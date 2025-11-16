package com.example.economix_android.sql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DomicilioDto {
    private Integer idDomicilio;
    private String ciudad;
    private String calle;
    private String colonia;
    private String numero;
    private String codigoPostal;
    private Integer idPersona;
}

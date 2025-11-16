package com.mictlan.economix.sql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactoDto {
    private Integer idContactos;
    private String numCelular;
    private String correoAlterno;
    private Integer idPersona;
}

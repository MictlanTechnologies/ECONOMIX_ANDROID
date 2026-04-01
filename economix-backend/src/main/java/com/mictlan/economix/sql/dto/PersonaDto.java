package com.mictlan.economix.sql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonaDto {
    private Integer idPersona;
    private String nombrePersona;
    private String apellidoP;
    private String apellidoM;
    private LocalDate fechaNacimiento;
    private Integer idUsuario;
}

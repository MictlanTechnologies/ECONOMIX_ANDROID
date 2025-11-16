package com.example.economix_android.network.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRolDto {
    private Integer idUsuarioRol;
    private Integer idUsuario;
    private Integer idRol;
}

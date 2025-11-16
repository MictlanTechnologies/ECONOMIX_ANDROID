package com.example.economix_android.network.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDto {
    private Integer idUsuario;
    private String perfilUsuario;
    private String correo;
    private String contrasenaUsuario;
    private LocalDateTime fechaRegistro;
    private String estado;
}

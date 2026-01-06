package com.example.economix_android.network.dto;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDto {
    private Integer idUsuario;
    private String perfilUsuario;
    @SerializedName(value = "contrasenaUsuario", alternate = {"contraseñaUsuario"})
    private String contrasenaUsuario;
}

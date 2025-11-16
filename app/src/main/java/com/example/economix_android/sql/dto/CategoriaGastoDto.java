package com.example.economix_android.sql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaGastoDto {
    private Integer idCategoria;
    private Integer idUsuario;
    private String nombreCategoria;
    private String descripcion;
    private String color;
}

package com.example.economix_android.sql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_categoria_gasto")
public class CategoriaGasto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCategoria")
    private Integer idCategoria;

    @Column(name = "idUsuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "nombreCategoria", nullable = false, length = 80)
    private String nombreCategoria;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "color", length = 7)
    private String color;
}

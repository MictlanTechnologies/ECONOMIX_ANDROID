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
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_presupuesto")
public class Presupuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPresupuesto")
    private Integer idPresupuesto;

    @Column(name = "idUsuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "idCategoria")
    private Integer idCategoria;

    @Column(name = "categoria", length = 40)
    private String categoria;

    @Column(name = "montoMaximo", nullable = false)
    private BigDecimal montoMaximo;

    @Column(name = "montoGastado", nullable = false)
    private BigDecimal montoGastado;

    @Column(name = "mes", nullable = false)
    private Integer mes;

    @Column(name = "anio", nullable = false)
    private Integer anio;
}

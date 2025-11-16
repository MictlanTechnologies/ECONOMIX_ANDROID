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
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_ingresos")
public class Ingreso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idIngresos")
    private Integer idIngresos;

    @Column(name = "idUsuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "idFuente")
    private Integer idFuente;

    @Column(name = "montoIngreso", nullable = false)
    private BigDecimal montoIngreso;

    @Column(name = "periodicidadIngreso", length = 50)
    private String periodicidadIngreso;

    @Column(name = "fechaIngresos", nullable = false)
    private LocalDate fechaIngresos;

    @Column(name = "descripcionIngreso")
    private String descripcionIngreso;
}

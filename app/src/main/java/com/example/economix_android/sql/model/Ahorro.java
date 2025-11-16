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
@Table(name = "tbl_ahorro")
public class Ahorro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAhorro")
    private Integer idAhorro;

    @Column(name = "idUsuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "nombreObjetivo", nullable = false, length = 100)
    private String nombreObjetivo;

    @Column(name = "descripcionObjetivo")
    private String descripcionObjetivo;

    @Column(name = "meta", nullable = false)
    private BigDecimal meta;

    @Column(name = "montoAhorrado", nullable = false)
    private BigDecimal montoAhorrado;

    @Column(name = "fechaLimite")
    private LocalDate fechaLimite;
}

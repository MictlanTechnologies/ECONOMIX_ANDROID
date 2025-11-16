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
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_movimiento_ahorro")
public class MovimientoAhorro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idMovimiento")
    private Integer idMovimiento;

    @Column(name = "idAhorro", nullable = false)
    private Integer idAhorro;

    @Column(name = "idUsuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "tipoMovimiento", nullable = false, length = 10)
    private String tipoMovimiento;

    @Column(name = "monto", nullable = false)
    private BigDecimal monto;

    @Column(name = "fechaMovimiento", nullable = false)
    private LocalDateTime fechaMovimiento;

    @Column(name = "nota", length = 150)
    private String nota;
}

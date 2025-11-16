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
@Table(name = "tbl_conceptogastos")
public class ConceptoGasto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idConcepto")
    private Integer idConcepto;

    @Column(name = "nombreConcepto", length = 100)
    private String nombreConcepto;

    @Column(name = "descripcionConcepto")
    private String descripcionConcepto;

    @Column(name = "precioConcepto")
    private BigDecimal precioConcepto;

    @Column(name = "idGastos")
    private Integer idGastos;
}

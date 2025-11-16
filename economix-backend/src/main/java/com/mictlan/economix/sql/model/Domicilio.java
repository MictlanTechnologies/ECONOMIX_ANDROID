package com.mictlan.economix.sql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_domicilio")
public class Domicilio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idDomicilio")
    private Integer idDomicilio;

    @Column(name = "ciudad", length = 50)
    private String ciudad;

    @Column(name = "calle", length = 100)
    private String calle;

    @Column(name = "colonia", length = 100)
    private String colonia;

    @Column(name = "numero", length = 10)
    private String numero;

    @Column(name = "codigoPostal", length = 10)
    private String codigoPostal;

    @Column(name = "idPersona")
    private Integer idPersona;
}

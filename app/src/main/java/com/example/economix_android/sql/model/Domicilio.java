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

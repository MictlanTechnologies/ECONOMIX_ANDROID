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
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario")
    private Integer idUsuario;

    @Column(name = "perfilUsuario", nullable = false, length = 50)
    private String perfilUsuario;

    @Column(name = "correo", nullable = false, length = 120)
    private String correo;

    @Column(name = "contraseñaUsuario", nullable = false, length = 100)
    private String contrasenaUsuario;

    @Column(name = "fechaRegistro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "estado", nullable = false, length = 15)
    private String estado;
}

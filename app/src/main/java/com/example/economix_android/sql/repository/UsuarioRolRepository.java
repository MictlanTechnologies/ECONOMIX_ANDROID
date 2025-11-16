package com.example.economix_android.sql.repository;

import com.example.economix_android.sql.model.UsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Integer> {
}

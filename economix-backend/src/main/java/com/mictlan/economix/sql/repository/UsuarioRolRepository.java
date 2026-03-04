package com.mictlan.economix.sql.repository;

import com.mictlan.economix.sql.model.UsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Integer> {
}

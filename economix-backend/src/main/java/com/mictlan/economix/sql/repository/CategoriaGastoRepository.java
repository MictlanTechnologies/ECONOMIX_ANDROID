package com.mictlan.economix.sql.repository;

import com.mictlan.economix.sql.model.CategoriaGasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaGastoRepository extends JpaRepository<CategoriaGasto, Integer> {
}

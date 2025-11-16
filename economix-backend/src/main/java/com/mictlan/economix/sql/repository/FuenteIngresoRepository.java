package com.mictlan.economix.sql.repository;

import com.mictlan.economix.sql.model.FuenteIngreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuenteIngresoRepository extends JpaRepository<FuenteIngreso, Integer> {
}

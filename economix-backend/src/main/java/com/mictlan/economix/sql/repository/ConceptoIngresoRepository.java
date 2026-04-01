package com.mictlan.economix.sql.repository;

import com.mictlan.economix.sql.model.ConceptoIngreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConceptoIngresoRepository extends JpaRepository<ConceptoIngreso, Integer> {
}

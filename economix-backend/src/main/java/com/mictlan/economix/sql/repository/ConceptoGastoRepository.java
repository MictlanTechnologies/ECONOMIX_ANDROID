package com.mictlan.economix.sql.repository;

import com.mictlan.economix.sql.model.ConceptoGasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConceptoGastoRepository extends JpaRepository<ConceptoGasto, Integer> {
}

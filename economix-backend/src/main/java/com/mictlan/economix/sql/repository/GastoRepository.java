package com.mictlan.economix.sql.repository;

import com.mictlan.economix.sql.model.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Integer> {
}

package com.mictlan.economix.sql.repository;

import com.mictlan.economix.sql.model.Ahorro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AhorroRepository extends JpaRepository<Ahorro, Integer> {
}

package com.example.economix_android.sql.repository;

import com.example.economix_android.sql.model.Presupuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Integer> {
}

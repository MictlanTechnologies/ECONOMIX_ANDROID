package com.mictlan.economix.sql.repository;

import com.mictlan.economix.sql.model.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactoRepository extends JpaRepository<Contacto, Integer> {
}

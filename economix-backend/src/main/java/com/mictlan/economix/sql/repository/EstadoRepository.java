package com.mictlan.economix.sql.repository;


import com.mictlan.economix.sql.model.Estado;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Integer> {

}

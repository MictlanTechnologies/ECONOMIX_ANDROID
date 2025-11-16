package com.example.economix_android.sql.repository;

import com.example.demo.model.Estado;
import com.example.demo.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Integer>
{

}

package com.mictlan.economix.sql.service;

import com.mictlan.economix.sql.model.FuenteIngreso;

import java.util.List;

public interface FuenteIngresoService {
    List<FuenteIngreso> getAll();
    FuenteIngreso getById(Integer id);
    FuenteIngreso save(FuenteIngreso fuenteIngreso);
    void delete(Integer id);
    FuenteIngreso update(Integer id, FuenteIngreso fuenteIngreso);
}

package com.mictlan.economix.sql.service;

import com.mictlan.economix.sql.model.Presupuesto;

import java.util.List;

public interface PresupuestoService {
    List<Presupuesto> getAll();
    Presupuesto getById(Integer id);
    Presupuesto save(Presupuesto presupuesto);
    void delete(Integer id);
    Presupuesto update(Integer id, Presupuesto presupuesto);
}

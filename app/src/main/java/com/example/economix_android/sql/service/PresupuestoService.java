package com.example.economix_android.sql.service;

import com.example.economix_android.sql.model.Presupuesto;

import java.util.List;

public interface PresupuestoService {
    List<Presupuesto> getAll();
    Presupuesto getById(Integer id);
    Presupuesto save(Presupuesto presupuesto);
    void delete(Integer id);
    Presupuesto update(Integer id, Presupuesto presupuesto);
}

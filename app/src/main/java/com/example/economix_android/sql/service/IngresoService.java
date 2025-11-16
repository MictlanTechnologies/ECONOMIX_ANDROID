package com.example.economix_android.sql.service;

import com.example.economix_android.sql.model.Ingreso;

import java.util.List;

public interface IngresoService {
    List<Ingreso> getAll();
    Ingreso getById(Integer id);
    Ingreso save(Ingreso ingreso);
    void delete(Integer id);
    Ingreso update(Integer id, Ingreso ingreso);
}

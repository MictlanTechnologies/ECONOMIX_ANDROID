package com.example.economix_android.sql.service;

import com.example.economix_android.sql.model.Gasto;

import java.util.List;

public interface GastoService {
    List<Gasto> getAll();
    Gasto getById(Integer id);
    Gasto save(Gasto gasto);
    void delete(Integer id);
    Gasto update(Integer id, Gasto gasto);
}

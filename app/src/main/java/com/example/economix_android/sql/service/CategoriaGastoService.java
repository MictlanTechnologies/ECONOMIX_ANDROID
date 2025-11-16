package com.example.economix_android.sql.service;

import com.example.economix_android.sql.model.CategoriaGasto;

import java.util.List;

public interface CategoriaGastoService {
    List<CategoriaGasto> getAll();
    CategoriaGasto getById(Integer id);
    CategoriaGasto save(CategoriaGasto categoriaGasto);
    void delete(Integer id);
    CategoriaGasto update(Integer id, CategoriaGasto categoriaGasto);
}

package com.mictlan.economix.sql.service;

import com.mictlan.economix.sql.model.CategoriaGasto;

import java.util.List;

public interface CategoriaGastoService {
    List<CategoriaGasto> getAll();
    CategoriaGasto getById(Integer id);
    CategoriaGasto save(CategoriaGasto categoriaGasto);
    void delete(Integer id);
    CategoriaGasto update(Integer id, CategoriaGasto categoriaGasto);
}

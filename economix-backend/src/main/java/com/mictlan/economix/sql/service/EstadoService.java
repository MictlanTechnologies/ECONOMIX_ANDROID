package com.mictlan.economix.sql.service;

import com.mictlan.economix.sql.model.Estado;

import java.util.List;

public interface EstadoService
{
    List<Estado> getAll( );
    Estado getById(Integer id);
    Estado save(Estado estado);
    void delete(Integer id);
    Estado update(Integer id, Estado estado);
}

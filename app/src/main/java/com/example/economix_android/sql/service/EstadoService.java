package com.example.economix_android.sql.service;

import com.example.economix_android.sql.model.Estado;

import java.util.List;

public interface EstadoService
{
    List<Estado> getAll( );
    Estado getById(Integer id);
    Estado save(Estado estado);
    void delete(Integer id);
    Estado update(Integer id, Estado estado);
}

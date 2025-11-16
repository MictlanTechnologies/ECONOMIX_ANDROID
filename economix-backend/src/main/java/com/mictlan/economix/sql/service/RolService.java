package com.mictlan.economix.sql.service;

import com.mictlan.economix.sql.model.Rol;

import java.util.List;

public interface RolService {
    List<Rol> getAll();
    Rol getById(Integer id);
    Rol save(Rol rol);
    void delete(Integer id);
    Rol update(Integer id, Rol rol);
}

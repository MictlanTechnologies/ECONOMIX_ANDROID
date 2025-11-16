package com.example.economix_android.sql.service;

import com.example.economix_android.sql.model.Rol;

import java.util.List;

public interface RolService {
    List<Rol> getAll();
    Rol getById(Integer id);
    Rol save(Rol rol);
    void delete(Integer id);
    Rol update(Integer id, Rol rol);
}

package com.example.economix_android.sql.service;

import com.example.economix_android.sql.model.Sesion;

import java.util.List;

public interface SesionService {
    List<Sesion> getAll();
    Sesion getById(Long id);
    Sesion save(Sesion sesion);
    void delete(Long id);
    Sesion update(Long id, Sesion sesion);
}

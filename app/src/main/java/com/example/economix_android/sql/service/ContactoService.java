package com.example.economix_android.sql.service;

import com.example.economix_android.sql.model.Contacto;

import java.util.List;

public interface ContactoService {
    List<Contacto> getAll();
    Contacto getById(Integer id);
    Contacto save(Contacto contacto);
    void delete(Integer id);
    Contacto update(Integer id, Contacto contacto);
}

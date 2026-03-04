package com.mictlan.economix.sql.service;

import com.mictlan.economix.sql.model.Contacto;

import java.util.List;

public interface ContactoService {
    List<Contacto> getAll();
    Contacto getById(Integer id);
    Contacto save(Contacto contacto);
    void delete(Integer id);
    Contacto update(Integer id, Contacto contacto);
}

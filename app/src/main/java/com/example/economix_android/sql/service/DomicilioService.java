package com.example.economix_android.sql.service;

import com.example.economix_android.sql.model.Domicilio;

import java.util.List;

public interface DomicilioService {
    List<Domicilio> getAll();
    Domicilio getById(Integer id);
    Domicilio save(Domicilio domicilio);
    void delete(Integer id);
    Domicilio update(Integer id, Domicilio domicilio);
}

package com.mictlan.economix.sql.service;

import com.mictlan.economix.sql.model.Ahorro;

import java.util.List;

public interface AhorroService {
    List<Ahorro> getAll();
    Ahorro getById(Integer id);
    Ahorro save(Ahorro ahorro);
    void delete(Integer id);
    Ahorro update(Integer id, Ahorro ahorro);
}

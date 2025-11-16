package com.example.economix_android.sql.service;

import com.example.economix_android.sql.model.MovimientoAhorro;

import java.util.List;

public interface MovimientoAhorroService {
    List<MovimientoAhorro> getAll();
    MovimientoAhorro getById(Integer id);
    MovimientoAhorro save(MovimientoAhorro movimientoAhorro);
    void delete(Integer id);
    MovimientoAhorro update(Integer id, MovimientoAhorro movimientoAhorro);
}

package com.example.economix_android.sql.service.impl;

import com.example.economix_android.sql.model.MovimientoAhorro;
import com.example.economix_android.sql.repository.MovimientoAhorroRepository;
import com.example.economix_android.sql.service.MovimientoAhorroService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MovimientoAhorroServiceImpl implements MovimientoAhorroService {

    private final MovimientoAhorroRepository movimientoAhorroRepository;

    @Override
    public List<MovimientoAhorro> getAll() {
        return movimientoAhorroRepository.findAll();
    }

    @Override
    public MovimientoAhorro getById(Integer id) {
        return movimientoAhorroRepository.findById(id).orElse(null);
    }

    @Override
    public MovimientoAhorro save(MovimientoAhorro movimientoAhorro) {
        return movimientoAhorroRepository.save(movimientoAhorro);
    }

    @Override
    public void delete(Integer id) {
        movimientoAhorroRepository.deleteById(id);
    }

    @Override
    public MovimientoAhorro update(Integer id, MovimientoAhorro movimientoAhorro) {
        return movimientoAhorroRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(movimientoAhorro, existing, "idMovimiento");
                    return movimientoAhorroRepository.save(existing);
                })
                .orElse(null);
    }
}

package com.mictlan.economix.sql.service.impl;

import com.mictlan.economix.sql.model.Ingreso;
import com.mictlan.economix.sql.repository.IngresoRepository;
import com.mictlan.economix.sql.service.IngresoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class IngresoServiceImpl implements IngresoService {

    private final IngresoRepository ingresoRepository;

    @Override
    public List<Ingreso> getAll() {
        return ingresoRepository.findAll();
    }

    @Override
    public Ingreso getById(Integer id) {
        return ingresoRepository.findById(id).orElse(null);
    }

    @Override
    public Ingreso save(Ingreso ingreso) {
        return ingresoRepository.save(ingreso);
    }

    @Override
    public void delete(Integer id) {
        ingresoRepository.deleteById(id);
    }

    @Override
    public Ingreso update(Integer id, Ingreso ingreso) {
        return ingresoRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(ingreso, existing, "idIngresos");
                    return ingresoRepository.save(existing);
                })
                .orElse(null);
    }
}

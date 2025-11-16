package com.example.economix_android.sql.service.impl;

import com.example.economix_android.sql.model.Ingreso;
import com.example.economix_android.sql.repository.IngresoRepository;
import com.example.economix_android.sql.service.IngresoService;
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

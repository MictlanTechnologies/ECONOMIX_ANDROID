package com.mictlan.economix.sql.service.impl;

import com.mictlan.economix.sql.model.FuenteIngreso;
import com.mictlan.economix.sql.repository.FuenteIngresoRepository;
import com.mictlan.economix.sql.service.FuenteIngresoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FuenteIngresoServiceImpl implements FuenteIngresoService {

    private final FuenteIngresoRepository fuenteIngresoRepository;

    @Override
    public List<FuenteIngreso> getAll() {
        return fuenteIngresoRepository.findAll();
    }

    @Override
    public FuenteIngreso getById(Integer id) {
        return fuenteIngresoRepository.findById(id).orElse(null);
    }

    @Override
    public FuenteIngreso save(FuenteIngreso fuenteIngreso) {
        return fuenteIngresoRepository.save(fuenteIngreso);
    }

    @Override
    public void delete(Integer id) {
        fuenteIngresoRepository.deleteById(id);
    }

    @Override
    public FuenteIngreso update(Integer id, FuenteIngreso fuenteIngreso) {
        return fuenteIngresoRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(fuenteIngreso, existing, "idFuente");
                    return fuenteIngresoRepository.save(existing);
                })
                .orElse(null);
    }
}

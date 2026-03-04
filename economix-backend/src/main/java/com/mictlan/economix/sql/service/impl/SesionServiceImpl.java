package com.mictlan.economix.sql.service.impl;

import com.mictlan.economix.sql.model.Sesion;
import com.mictlan.economix.sql.repository.SesionRepository;
import com.mictlan.economix.sql.service.SesionService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SesionServiceImpl implements SesionService {

    private final SesionRepository sesionRepository;

    @Override
    public List<Sesion> getAll() {
        return sesionRepository.findAll();
    }

    @Override
    public Sesion getById(Long id) {
        return sesionRepository.findById(id).orElse(null);
    }

    @Override
    public Sesion save(Sesion sesion) {
        return sesionRepository.save(sesion);
    }

    @Override
    public void delete(Long id) {
        sesionRepository.deleteById(id);
    }

    @Override
    public Sesion update(Long id, Sesion sesion) {
        return sesionRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(sesion, existing, "idSesion");
                    return sesionRepository.save(existing);
                })
                .orElse(null);
    }
}

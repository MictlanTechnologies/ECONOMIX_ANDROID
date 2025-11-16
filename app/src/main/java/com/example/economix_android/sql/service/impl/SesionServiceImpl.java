package com.example.economix_android.sql.service.impl;

import com.example.economix_android.sql.model.Sesion;
import com.example.economix_android.sql.repository.SesionRepository;
import com.example.economix_android.sql.service.SesionService;
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

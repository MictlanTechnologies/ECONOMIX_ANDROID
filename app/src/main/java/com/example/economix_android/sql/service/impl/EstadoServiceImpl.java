package com.example.economix_android.sql.service.impl;

import com.example.economix_android.sql.model.Estado;
import com.example.economix_android.sql.repository.EstadoRepository;
import com.example.economix_android.sql.service.EstadoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class EstadoServiceImpl implements EstadoService {
    private final EstadoRepository estadoRepository;

    @Override
    public List<Estado> getAll() {
        return estadoRepository.findAll();
    }

    @Override
    public Estado getById(Integer id) {
        return estadoRepository.findById(id).orElse(null);
    }

    @Override
    public Estado save(Estado estado) {
        return estadoRepository.save(estado);
    }

    @Override
    public void delete(Integer id) {
        estadoRepository.deleteById(id);
    }

    @Override
    public Estado update(Integer id, Estado estado) {
        return estadoRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(estado, existing, "id");
                    return estadoRepository.save(existing);
                })
                .orElse(null);
    }
}

package com.mictlan.economix.sql.service.impl;

import com.mictlan.economix.sql.model.Domicilio;
import com.mictlan.economix.sql.repository.DomicilioRepository;
import com.mictlan.economix.sql.service.DomicilioService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DomicilioServiceImpl implements DomicilioService {

    private final DomicilioRepository domicilioRepository;

    @Override
    public List<Domicilio> getAll() {
        return domicilioRepository.findAll();
    }

    @Override
    public Domicilio getById(Integer id) {
        return domicilioRepository.findById(id).orElse(null);
    }

    @Override
    public Domicilio save(Domicilio domicilio) {
        return domicilioRepository.save(domicilio);
    }

    @Override
    public void delete(Integer id) {
        domicilioRepository.deleteById(id);
    }

    @Override
    public Domicilio update(Integer id, Domicilio domicilio) {
        return domicilioRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(domicilio, existing, "idDomicilio");
                    return domicilioRepository.save(existing);
                })
                .orElse(null);
    }
}

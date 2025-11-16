package com.mictlan.economix.sql.service.impl;

import com.mictlan.economix.sql.model.Ahorro;
import com.mictlan.economix.sql.repository.AhorroRepository;
import com.mictlan.economix.sql.service.AhorroService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AhorroServiceImpl implements AhorroService {

    private final AhorroRepository ahorroRepository;

    @Override
    public List<Ahorro> getAll() {
        return ahorroRepository.findAll();
    }

    @Override
    public Ahorro getById(Integer id) {
        return ahorroRepository.findById(id).orElse(null);
    }

    @Override
    public Ahorro save(Ahorro ahorro) {
        return ahorroRepository.save(ahorro);
    }

    @Override
    public void delete(Integer id) {
        ahorroRepository.deleteById(id);
    }

    @Override
    public Ahorro update(Integer id, Ahorro ahorro) {
        return ahorroRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(ahorro, existing, "idAhorro");
                    return ahorroRepository.save(existing);
                })
                .orElse(null);
    }
}

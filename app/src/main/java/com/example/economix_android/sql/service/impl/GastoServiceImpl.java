package com.example.economix_android.sql.service.impl;

import com.example.economix_android.sql.model.Gasto;
import com.example.economix_android.sql.repository.GastoRepository;
import com.example.economix_android.sql.service.GastoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GastoServiceImpl implements GastoService {

    private final GastoRepository gastoRepository;

    @Override
    public List<Gasto> getAll() {
        return gastoRepository.findAll();
    }

    @Override
    public Gasto getById(Integer id) {
        return gastoRepository.findById(id).orElse(null);
    }

    @Override
    public Gasto save(Gasto gasto) {
        return gastoRepository.save(gasto);
    }

    @Override
    public void delete(Integer id) {
        gastoRepository.deleteById(id);
    }

    @Override
    public Gasto update(Integer id, Gasto gasto) {
        return gastoRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(gasto, existing, "idGastos");
                    return gastoRepository.save(existing);
                })
                .orElse(null);
    }
}

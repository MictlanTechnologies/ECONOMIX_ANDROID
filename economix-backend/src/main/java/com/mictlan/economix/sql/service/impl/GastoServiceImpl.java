package com.mictlan.economix.sql.service.impl;

import com.mictlan.economix.sql.model.Gasto;
import com.mictlan.economix.sql.repository.GastoRepository;
import com.mictlan.economix.sql.repository.PresupuestoRepository;
import com.mictlan.economix.sql.service.GastoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GastoServiceImpl implements GastoService {

    private final GastoRepository gastoRepository;
    private final PresupuestoRepository presupuestoRepository;

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
        return gastoRepository.save(normalizePresupuesto(gasto));
    }

    @Override
    public void delete(Integer id) {
        gastoRepository.deleteById(id);
    }

    @Override
    public Gasto update(Integer id, Gasto gasto) {
        return gastoRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(normalizePresupuesto(gasto), existing, "idGastos");
                    return gastoRepository.save(existing);
                })
                .orElse(null);
    }

    private Gasto normalizePresupuesto(Gasto gasto) {
        if (gasto.getIdPresupuesto() != null &&
                !presupuestoRepository.existsById(gasto.getIdPresupuesto())) {
            gasto.setIdPresupuesto(null);
        }
        return gasto;
    }
}
package com.example.economix_android.sql.service.impl;

import com.example.economix_android.sql.model.Presupuesto;
import com.example.economix_android.sql.repository.PresupuestoRepository;
import com.example.economix_android.sql.service.PresupuestoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PresupuestoServiceImpl implements PresupuestoService {

    private final PresupuestoRepository presupuestoRepository;

    @Override
    public List<Presupuesto> getAll() {
        return presupuestoRepository.findAll();
    }

    @Override
    public Presupuesto getById(Integer id) {
        return presupuestoRepository.findById(id).orElse(null);
    }

    @Override
    public Presupuesto save(Presupuesto presupuesto) {
        return presupuestoRepository.save(presupuesto);
    }

    @Override
    public void delete(Integer id) {
        presupuestoRepository.deleteById(id);
    }

    @Override
    public Presupuesto update(Integer id, Presupuesto presupuesto) {
        return presupuestoRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(presupuesto, existing, "idPresupuesto");
                    return presupuestoRepository.save(existing);
                })
                .orElse(null);
    }
}

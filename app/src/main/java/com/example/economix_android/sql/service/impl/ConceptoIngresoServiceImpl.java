package com.example.economix_android.sql.service.impl;

import com.example.economix_android.sql.model.ConceptoIngreso;
import com.example.economix_android.sql.repository.ConceptoIngresoRepository;
import com.example.economix_android.sql.service.ConceptoIngresoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ConceptoIngresoServiceImpl implements ConceptoIngresoService {

    private final ConceptoIngresoRepository conceptoIngresoRepository;

    @Override
    public List<ConceptoIngreso> getAll() {
        return conceptoIngresoRepository.findAll();
    }

    @Override
    public ConceptoIngreso getById(Integer id) {
        return conceptoIngresoRepository.findById(id).orElse(null);
    }

    @Override
    public ConceptoIngreso save(ConceptoIngreso conceptoIngreso) {
        return conceptoIngresoRepository.save(conceptoIngreso);
    }

    @Override
    public void delete(Integer id) {
        conceptoIngresoRepository.deleteById(id);
    }

    @Override
    public ConceptoIngreso update(Integer id, ConceptoIngreso conceptoIngreso) {
        return conceptoIngresoRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(conceptoIngreso, existing, "idConcepto");
                    return conceptoIngresoRepository.save(existing);
                })
                .orElse(null);
    }
}

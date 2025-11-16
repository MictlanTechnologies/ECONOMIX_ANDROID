package com.example.economix_android.sql.service.impl;

import com.example.economix_android.sql.model.ConceptoGasto;
import com.example.economix_android.sql.repository.ConceptoGastoRepository;
import com.example.economix_android.sql.service.ConceptoGastoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ConceptoGastoServiceImpl implements ConceptoGastoService {

    private final ConceptoGastoRepository conceptoGastoRepository;

    @Override
    public List<ConceptoGasto> getAll() {
        return conceptoGastoRepository.findAll();
    }

    @Override
    public ConceptoGasto getById(Integer id) {
        return conceptoGastoRepository.findById(id).orElse(null);
    }

    @Override
    public ConceptoGasto save(ConceptoGasto conceptoGasto) {
        return conceptoGastoRepository.save(conceptoGasto);
    }

    @Override
    public void delete(Integer id) {
        conceptoGastoRepository.deleteById(id);
    }

    @Override
    public ConceptoGasto update(Integer id, ConceptoGasto conceptoGasto) {
        return conceptoGastoRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(conceptoGasto, existing, "idConcepto");
                    return conceptoGastoRepository.save(existing);
                })
                .orElse(null);
    }
}

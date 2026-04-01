package com.mictlan.economix.sql.service;

import com.mictlan.economix.sql.model.ConceptoGasto;

import java.util.List;

public interface ConceptoGastoService {
    List<ConceptoGasto> getAll();
    ConceptoGasto getById(Integer id);
    ConceptoGasto save(ConceptoGasto conceptoGasto);
    void delete(Integer id);
    ConceptoGasto update(Integer id, ConceptoGasto conceptoGasto);
}

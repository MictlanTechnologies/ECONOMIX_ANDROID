package com.mictlan.economix.sql.service;

import com.mictlan.economix.sql.model.ConceptoIngreso;

import java.util.List;

public interface ConceptoIngresoService {
    List<ConceptoIngreso> getAll();
    ConceptoIngreso getById(Integer id);
    ConceptoIngreso save(ConceptoIngreso conceptoIngreso);
    void delete(Integer id);
    ConceptoIngreso update(Integer id, ConceptoIngreso conceptoIngreso);
}

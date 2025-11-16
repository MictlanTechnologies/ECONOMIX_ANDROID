package com.example.economix_android.sql.service.impl;

import com.example.economix_android.sql.model.CategoriaGasto;
import com.example.economix_android.sql.repository.CategoriaGastoRepository;
import com.example.economix_android.sql.service.CategoriaGastoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoriaGastoServiceImpl implements CategoriaGastoService {

    private final CategoriaGastoRepository categoriaGastoRepository;

    @Override
    public List<CategoriaGasto> getAll() {
        return categoriaGastoRepository.findAll();
    }

    @Override
    public CategoriaGasto getById(Integer id) {
        return categoriaGastoRepository.findById(id).orElse(null);
    }

    @Override
    public CategoriaGasto save(CategoriaGasto categoriaGasto) {
        return categoriaGastoRepository.save(categoriaGasto);
    }

    @Override
    public void delete(Integer id) {
        categoriaGastoRepository.deleteById(id);
    }

    @Override
    public CategoriaGasto update(Integer id, CategoriaGasto categoriaGasto) {
        return categoriaGastoRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(categoriaGasto, existing, "idCategoria");
                    return categoriaGastoRepository.save(existing);
                })
                .orElse(null);
    }
}

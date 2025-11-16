package com.example.economix_android.sql.service.impl;

import com.example.economix_android.sql.model.Rol;
import com.example.economix_android.sql.repository.RolRepository;
import com.example.economix_android.sql.service.RolService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;

    @Override
    public List<Rol> getAll() {
        return rolRepository.findAll();
    }

    @Override
    public Rol getById(Integer id) {
        return rolRepository.findById(id).orElse(null);
    }

    @Override
    public Rol save(Rol rol) {
        return rolRepository.save(rol);
    }

    @Override
    public void delete(Integer id) {
        rolRepository.deleteById(id);
    }

    @Override
    public Rol update(Integer id, Rol rol) {
        return rolRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(rol, existing, "idRol");
                    return rolRepository.save(existing);
                })
                .orElse(null);
    }
}

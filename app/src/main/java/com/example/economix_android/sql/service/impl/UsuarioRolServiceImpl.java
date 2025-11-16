package com.example.economix_android.sql.service.impl;

import com.example.economix_android.sql.model.UsuarioRol;
import com.example.economix_android.sql.repository.UsuarioRolRepository;
import com.example.economix_android.sql.service.UsuarioRolService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UsuarioRolServiceImpl implements UsuarioRolService {

    private final UsuarioRolRepository usuarioRolRepository;

    @Override
    public List<UsuarioRol> getAll() {
        return usuarioRolRepository.findAll();
    }

    @Override
    public UsuarioRol getById(Integer id) {
        return usuarioRolRepository.findById(id).orElse(null);
    }

    @Override
    public UsuarioRol save(UsuarioRol usuarioRol) {
        return usuarioRolRepository.save(usuarioRol);
    }

    @Override
    public void delete(Integer id) {
        usuarioRolRepository.deleteById(id);
    }

    @Override
    public UsuarioRol update(Integer id, UsuarioRol usuarioRol) {
        return usuarioRolRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(usuarioRol, existing, "idUsuarioRol");
                    return usuarioRolRepository.save(existing);
                })
                .orElse(null);
    }
}

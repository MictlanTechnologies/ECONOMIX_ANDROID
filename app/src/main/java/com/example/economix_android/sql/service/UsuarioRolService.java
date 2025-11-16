package com.example.economix_android.sql.service;

import com.example.economix_android.sql.model.UsuarioRol;

import java.util.List;

public interface UsuarioRolService {
    List<UsuarioRol> getAll();
    UsuarioRol getById(Integer id);
    UsuarioRol save(UsuarioRol usuarioRol);
    void delete(Integer id);
    UsuarioRol update(Integer id, UsuarioRol usuarioRol);
}

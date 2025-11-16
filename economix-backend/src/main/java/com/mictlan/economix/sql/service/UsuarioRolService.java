package com.mictlan.economix.sql.service;

import com.mictlan.economix.sql.model.UsuarioRol;

import java.util.List;

public interface UsuarioRolService {
    List<UsuarioRol> getAll();
    UsuarioRol getById(Integer id);
    UsuarioRol save(UsuarioRol usuarioRol);
    void delete(Integer id);
    UsuarioRol update(Integer id, UsuarioRol usuarioRol);
}

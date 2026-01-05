package com.mictlan.economix.sql.service;

import com.mictlan.economix.sql.model.Usuario;

import java.util.List;

public interface UsuarioService {
    List<Usuario> getAll();
    Usuario getById(Integer id);
    Usuario getByCorreo(String correo);
    Usuario save(Usuario usuario);
    boolean existsByCorreo(String correo);
    void delete(Integer id);
    Usuario update(Integer id, Usuario usuario);
}

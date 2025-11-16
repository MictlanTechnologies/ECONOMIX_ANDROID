package com.example.economix_android.sql.service;

import com.example.economix_android.sql.model.Usuario;

import java.util.List;

public interface UsuarioService {
    List<Usuario> getAll();
    Usuario getById(Integer id);
    Usuario save(Usuario usuario);
    void delete(Integer id);
    Usuario update(Integer id, Usuario usuario);
}

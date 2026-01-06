package com.example.economix_android.network.repository;

import com.example.economix_android.network.ApiClient;
import com.example.economix_android.network.api.UsuarioApi;
import com.example.economix_android.network.dto.LoginRequest;
import com.example.economix_android.network.dto.UsuarioDto;

import java.util.List;

import retrofit2.Callback;

public class UsuarioRepository {

    private final UsuarioApi usuarioApi = ApiClient.getUsuarioApi();

    public void obtenerUsuarios(Callback<List<UsuarioDto>> callback) {
        usuarioApi.getAll().enqueue(callback);
    }

    public void crearUsuario(UsuarioDto dto, Callback<UsuarioDto> callback) {
        usuarioApi.create(dto).enqueue(callback);
    }

    public void login(LoginRequest request, Callback<UsuarioDto> callback) {
        usuarioApi.login(request).enqueue(callback);
    }
}

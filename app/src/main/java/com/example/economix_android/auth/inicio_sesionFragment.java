package com.example.economix_android.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.economix_android.Model.data.DataRepository;
import com.example.economix_android.R;
import com.example.economix_android.Vista.menu;
import com.example.economix_android.databinding.FragmentInicioSesionBinding;
import com.example.economix_android.network.dto.LoginRequest;
import com.example.economix_android.network.dto.PersonaDto;
import com.example.economix_android.network.dto.UsuarioDto;
import com.example.economix_android.network.repository.PersonaRepository;
import com.example.economix_android.network.repository.UsuarioRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class inicio_sesionFragment extends Fragment {

    private FragmentInicioSesionBinding binding;
    private UsuarioRepository usuarioRepository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInicioSesionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        usuarioRepository = new UsuarioRepository();

        binding.btnBack.setOnClickListener(v -> requireActivity()
                .getOnBackPressedDispatcher()
                .onBackPressed());
        binding.btnSignIn.setOnClickListener(v -> iniciarSesion());
    }

    private void iniciarSesion() {
        limpiarErrores();

        String perfil = obtenerTexto(binding.etPerfil);
        String contrasena = obtenerTexto(binding.etPassword);

        boolean hayError = false;

        if (TextUtils.isEmpty(perfil)) {
            binding.tilPerfil.setError(getString(R.string.error_perfil_obligatorio));
            hayError = true;
        }

        if (TextUtils.isEmpty(contrasena)) {
            binding.tilPassword.setError(getString(R.string.error_contrasena_obligatoria));
            hayError = true;
        }

        if (hayError) {
            return;
        }

        binding.btnSignIn.setEnabled(false);
        usuarioRepository.login(new LoginRequest(perfil.trim(), contrasena), new Callback<UsuarioDto>() {
            @Override
            public void onResponse(Call<UsuarioDto> call, Response<UsuarioDto> response) {
                if (binding != null) {
                    binding.btnSignIn.setEnabled(true);
                }
                if (!isAdded()) {
                    return;
                }

                if (!response.isSuccessful() || response.body() == null) {
                    if (response.code() == 401) {
                        Toast.makeText(requireContext(), getString(R.string.error_credenciales_invalidas), Toast.LENGTH_SHORT).show();
                    } else {
                        mostrarMensajeError(null);
                    }
                    return;
                }

                DataRepository.clearAll();
                SessionManager.saveSession(requireContext(), response.body());
                abrirMenu();
            }

            @Override
            public void onFailure(Call<UsuarioDto> call, Throwable t) {
                if (binding != null) {
                    binding.btnSignIn.setEnabled(true);
                }
                if (!isAdded()) {
                    return;
                }
                mostrarMensajeError(null);
            }
        });
    }

    private void abrirMenu() {
        Intent menuIntent = new Intent(requireContext(), menu.class);
        menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(menuIntent);
        requireActivity().finish();
    }

    private void limpiarErrores() {
        binding.tilPerfil.setError(null);
        binding.tilPassword.setError(null);
    }

    private String obtenerTexto(com.google.android.material.textfield.TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private void mostrarMensajeError(String message) {
        String texto = message != null ? message : getString(R.string.mensaje_error_servidor);
        Toast.makeText(requireContext(), texto, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

package com.example.economix_android.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.economix_android.R;
import com.example.economix_android.Vista.menu;
import com.example.economix_android.auth.AuthValidator;
import com.example.economix_android.databinding.FragmentInicioSesionBinding;
import com.example.economix_android.network.dto.UsuarioDto;
import com.example.economix_android.network.repository.UsuarioRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class inicio_sesionFragment extends Fragment {

    private FragmentInicioSesionBinding binding;
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInicioSesionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnBack.setOnClickListener(v -> requireActivity()
                .getOnBackPressedDispatcher()
                .onBackPressed());
        binding.btnSignIn.setOnClickListener(v -> iniciarSesion());
    }

    private void iniciarSesion() {
        limpiarErrores();

        String correo = obtenerTexto(binding.etEmail);
        String contrasena = obtenerTexto(binding.etPassword);

        boolean correoValido = AuthValidator.validarCorreo(correo, binding.tilEmail, requireContext());
        boolean contrasenaValida = AuthValidator.validarPassword(contrasena, binding.tilPassword, requireContext());

        if (!correoValido || !contrasenaValida) {
            return;
        }

        binding.btnSignIn.setEnabled(false);
        final String correoNormalizado = correo.trim();
        final String contrasenaFinal = contrasena;

        usuarioRepository.iniciarSesion(correoNormalizado, contrasenaFinal, new Callback<UsuarioDto>() {
            @Override
            public void onResponse(Call<UsuarioDto> call, Response<UsuarioDto> response) {
                if (binding != null) {
                    binding.btnSignIn.setEnabled(true);
                }
                if (!isAdded()) {
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    Intent menuIntent = new Intent(requireContext(), menu.class);
                    startActivity(menuIntent);
                    requireActivity().finish();
                } else if (response.code() == 401) {
                    Toast.makeText(requireContext(), getString(R.string.error_credenciales_invalidas), Toast.LENGTH_SHORT).show();
                } else {
                    mostrarMensajeError(null);
                }
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

    private void limpiarErrores() {
        binding.tilEmail.setError(null);
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

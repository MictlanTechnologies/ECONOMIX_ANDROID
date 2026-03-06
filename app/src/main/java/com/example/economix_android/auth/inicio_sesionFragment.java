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
import androidx.navigation.fragment.NavHostFragment;

import com.example.economix_android.Model.data.DataRepository;
import com.example.economix_android.R;
import com.example.economix_android.Vista.menu;
import com.example.economix_android.databinding.FragmentInicioSesionBinding;
import com.example.economix_android.network.auth.dto.LoginRequest;
import com.example.economix_android.network.auth.dto.LoginResponse;
import com.example.economix_android.network.repository.auth.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class inicio_sesionFragment extends Fragment {

    private FragmentInicioSesionBinding binding;
    private AuthRepository authRepository;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInicioSesionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authRepository = new AuthRepository(requireContext());
        sessionManager = new SessionManager(requireContext());

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
        authRepository.login(new LoginRequest(perfil.trim(), contrasena), new Callback<>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
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

                LoginResponse loginResponse = response.body();
                if (loginResponse.isRequires2fa()) {
                    Bundle args = new Bundle();
                    args.putString("challengeId", loginResponse.getChallengeId());
                    args.putString("challengeExpiresAt", loginResponse.getChallengeExpiresAt());
                    NavHostFragment.findNavController(inicio_sesionFragment.this)
                            .navigate(R.id.action_inicio_sesionFragment_to_twoFactorFragment, args);
                    return;
                }

                DataRepository.clearAll();
                sessionManager.saveAuthSession(
                        loginResponse.getAccessToken(),
                        loginResponse.getRefreshToken(),
                        loginResponse.getUserInfo(),
                        loginResponse.getChallengeExpiresAt()
                );
                abrirMenu();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
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

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
import com.example.economix_android.databinding.FragmentInicioSesionBinding;
import com.example.economix_android.Vista.menu;

public class inicio_sesionFragment extends Fragment {

    private FragmentInicioSesionBinding binding;

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

        boolean hayError = false;

        if (TextUtils.isEmpty(correo)) {
            binding.tilEmail.setError(getString(R.string.error_correo_obligatorio));
            hayError = true;
        }

        if (TextUtils.isEmpty(contrasena)) {
            binding.tilPassword.setError(getString(R.string.error_contrasena_obligatoria));
            hayError = true;
        }

        if (hayError) {
            return;
        }

        if (DataRepository.validarCredenciales(correo, contrasena)) {
            Intent menuIntent = new Intent(requireContext(), menu.class);
            startActivity(menuIntent);
            requireActivity().finish();
        } else {
            Toast.makeText(requireContext(), getString(R.string.error_credenciales_invalidas), Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarErrores() {
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
    }

    private String obtenerTexto(com.google.android.material.textfield.TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

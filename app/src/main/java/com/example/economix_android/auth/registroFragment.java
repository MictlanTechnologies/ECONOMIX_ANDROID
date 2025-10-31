package com.example.economix_android.auth;

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
import com.example.economix_android.Model.data.UserAccount;
import com.example.economix_android.R;
import com.example.economix_android.databinding.FragmentRegistroBinding;

public class registroFragment extends Fragment {

    private FragmentRegistroBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegistroBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnBack.setOnClickListener(v -> requireActivity()
                .getOnBackPressedDispatcher()
                .onBackPressed());
        binding.btnSignUp.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        limpiarErrores();

        String nombre = obtenerTexto(binding.etName);
        String correo = obtenerTexto(binding.etEmail);
        String contrasena = obtenerTexto(binding.etPassword);
        String confirmar = obtenerTexto(binding.etConfirmPassword);

        boolean hayError = false;

        if (TextUtils.isEmpty(nombre)) {
            binding.tilName.setError(getString(R.string.error_nombre_obligatorio));
            hayError = true;
        }

        if (TextUtils.isEmpty(correo)) {
            binding.tilEmail.setError(getString(R.string.error_correo_obligatorio));
            hayError = true;
        } else if (DataRepository.existeCorreo(correo)) {
            binding.tilEmail.setError(getString(R.string.error_correo_registrado));
            hayError = true;
        }

        if (TextUtils.isEmpty(contrasena)) {
            binding.tilPassword.setError(getString(R.string.error_contrasena_obligatoria));
            hayError = true;
        }

        if (TextUtils.isEmpty(confirmar)) {
            binding.tilConfirmPassword.setError(getString(R.string.error_confirmar_contrasena));
            hayError = true;
        } else if (!TextUtils.isEmpty(contrasena) && !contrasena.equals(confirmar)) {
            binding.tilConfirmPassword.setError(getString(R.string.error_contrasenas_no_coinciden));
            hayError = true;
        }

        if (hayError) {
            return;
        }

        boolean agregado = DataRepository.addUsuario(new UserAccount(nombre, correo.trim(), contrasena));
        if (agregado) {
            Toast.makeText(requireContext(), getString(R.string.mensaje_registro_exitoso), Toast.LENGTH_SHORT).show();
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        } else {
            Toast.makeText(requireContext(), getString(R.string.error_correo_registrado), Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarErrores() {
        binding.tilName.setError(null);
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
        binding.tilConfirmPassword.setError(null);
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
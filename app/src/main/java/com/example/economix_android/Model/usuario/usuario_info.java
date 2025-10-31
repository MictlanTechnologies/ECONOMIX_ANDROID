package com.example.economix_android.Model.usuario;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.economix_android.R;
import com.example.economix_android.databinding.FragmentUsuarioInfoBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class usuario_info extends Fragment {

    private FragmentUsuarioInfoBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUsuarioInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnRegresar.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.usuario));
        binding.btnAyudaUsInf.setOnClickListener(v -> mostrarAyuda());
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_usuario_info)
                .setMessage(R.string.mensaje_ayuda_usuario_info)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
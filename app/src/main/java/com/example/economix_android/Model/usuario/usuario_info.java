package com.example.economix_android.Model.usuario;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.economix_android.databinding.FragmentUsuarioInfoBinding;

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
                NavHostFragment.findNavController(this).popBackStack());

        binding.btnAyudaUsInf.setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
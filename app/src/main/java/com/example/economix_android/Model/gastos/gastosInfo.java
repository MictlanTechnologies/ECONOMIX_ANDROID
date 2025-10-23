package com.example.economix_android.Model.gastos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.economix_android.R;
import com.example.economix_android.databinding.FragmentGastosInfoBinding;

public class gastosInfo extends Fragment {

    private FragmentGastosInfoBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGastosInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnPerfil.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.nav_host_fragment_usuario));

        binding.btnAyudaGasInf.setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
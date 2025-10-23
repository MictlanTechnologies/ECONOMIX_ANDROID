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
import com.example.economix_android.databinding.FragmentGastosBinding;

public class gastosFragment extends Fragment {

    private FragmentGastosBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGastosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnVerGas.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_navigation_gastos_to_gastosInfoFragment));

        binding.btnAyudaGas.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_navigation_gastos_to_gastosInfoFragment));

        binding.btnPerfil.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.nav_host_fragment_usuario));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
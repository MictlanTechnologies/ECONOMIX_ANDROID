package com.example.economix_android.Model.ingresos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.economix_android.R;
import com.example.economix_android.databinding.FragmentIngresosBinding;

public class ingresosFragment extends Fragment {

    private FragmentIngresosBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentIngresosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnVerIng.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_navigation_ingresos_to_ingresosInfoFragment));

        binding.btnAyudaIng.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_navigation_ingresos_to_ingresosInfoFragment));

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
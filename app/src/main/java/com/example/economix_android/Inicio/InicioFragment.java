package com.example.economix_android.Inicio;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.economix_android.R;
import com.example.economix_android.databinding.InicioBinding;

public class InicioFragment extends Fragment {

    private InicioBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = InicioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iniciarFondoAnimado();

        final NavController navController = Navigation.findNavController(view);
        binding.inicioSesion.setOnClickListener(v ->
                navController.navigate(R.id.action_inicioFragment_to_inicioSesionFragment));
        binding.registrarse.setOnClickListener(v ->
                navController.navigate(R.id.action_inicioFragment_to_registroFragment));
    }

    private void iniciarFondoAnimado() {
        if (binding == null || binding.getRoot() == null) {
            return;
        }
        if (binding.getRoot().getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) binding.getRoot().getBackground();
            animationDrawable.setEnterFadeDuration(1200);
            animationDrawable.setExitFadeDuration(1200);
            animationDrawable.start();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

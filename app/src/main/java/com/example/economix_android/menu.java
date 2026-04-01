package com.example.economix_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.economix_android.auth.SessionManager;
import com.example.economix_android.util.ProfileImageUtils;

public class menu extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View tileGastos = view.findViewById(R.id.tileGastos);
        View tileIngresos = view.findViewById(R.id.tileIngresos);
        View tileAhorro = view.findViewById(R.id.tileAhorro);
        View tileGraficas = view.findViewById(R.id.tileGraficas);
        ImageView perfilButton = view.findViewById(R.id.btnPerfil);
        TextView saludoUsuario = view.findViewById(R.id.txtHolaUsuario);

        ProfileImageUtils.applyProfileImage(requireContext(), perfilButton);
        String perfil = SessionManager.getPerfil(requireContext());
        String nombreVisible = (perfil != null && !perfil.trim().isEmpty()) ? perfil : "Usuario";
        String saludo = "Hola, " + nombreVisible;
        saludoUsuario.setText(saludo);

        tileGastos.setOnClickListener(this);
        tileIngresos.setOnClickListener(this);
        tileAhorro.setOnClickListener(this);
        tileGraficas.setOnClickListener(this);
        perfilButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.tileGastos) {
            Navigation.findNavController(v).navigate(R.id.action_menu_to_navigation_gastos);
        } else if (viewId == R.id.tileIngresos) {
            Navigation.findNavController(v).navigate(R.id.action_menu_to_navigation_ingresos);
        } else if (viewId == R.id.tileAhorro) {
            Navigation.findNavController(v).navigate(R.id.action_menu_to_navigation_ahorro);
        } else if (viewId == R.id.tileGraficas) {
            Navigation.findNavController(v).navigate(R.id.action_menu_to_navigation_graficas);
        } else if (viewId == R.id.btnPerfil) {
            Navigation.findNavController(v).navigate(R.id.action_menu_to_usuario);
        }
    }
}

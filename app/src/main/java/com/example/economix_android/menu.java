package com.example.economix_android;

import android.widget.ImageButton;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class menu extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton gastosButton = view.findViewById(R.id.gastoFr);
        ImageButton ingresosButton = view.findViewById(R.id.ingresoFr);
        ImageButton ahorroButton = view.findViewById(R.id.ahorroFr);
        ImageButton graficasButton = view.findViewById(R.id.graficasFr);
        ImageButton perfilButton = view.findViewById(R.id.btnPerfil);

        gastosButton.setOnClickListener(this);
        ingresosButton.setOnClickListener(this);
        ahorroButton.setOnClickListener(this);
        graficasButton.setOnClickListener(this);
        perfilButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.gastoFr) {
            Navigation.findNavController(v).navigate(R.id.action_menu_to_navigation_gastos);
        } else if (viewId == R.id.ingresoFr) {
            Navigation.findNavController(v).navigate(R.id.action_menu_to_navigation_ingresos);
        } else if (viewId == R.id.ahorroFr) {
            Navigation.findNavController(v).navigate(R.id.action_menu_to_navigation_ahorro);
        } else if (viewId == R.id.graficasFr) {
            Navigation.findNavController(v).navigate(R.id.action_menu_to_navigation_graficas);
        } else if (viewId == R.id.btnPerfil) {
            Navigation.findNavController(v).navigate(R.id.action_menu_to_usuario);
        }
    }
}
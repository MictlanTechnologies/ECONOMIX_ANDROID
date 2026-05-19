package com.example.economix_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.economix_android.auth.SessionManager;
import com.example.economix_android.util.ProfileImageUtils;
import com.example.economix_android.util.UsuarioAnimationNavigator;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class menu extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton ayudaButton = view.findViewById(R.id.btnAyuda);
        View gastosButton = view.findViewById(R.id.tileGastos);
        View ingresosButton = view.findViewById(R.id.tileIngresos);
        View ahorroButton = view.findViewById(R.id.tileAhorro);
        View graficasButton = view.findViewById(R.id.tileGraficas);
        ImageButton perfilButton = view.findViewById(R.id.btnPerfil);
        TextView saludoUsuario = view.findViewById(R.id.txtHolaUsuario);

        ProfileImageUtils.applyProfileImage(requireContext(), perfilButton);
        String perfil = SessionManager.getPerfil(requireContext());
        String saludo = perfil != null ? getString(R.string.label_hola_usuario, perfil) : getString(R.string.label_hola);
        saludoUsuario.setText(saludo);

        ayudaButton.setOnClickListener(v -> mostrarAyuda());
        gastosButton.setOnClickListener(this);
        ingresosButton.setOnClickListener(this);
        ahorroButton.setOnClickListener(this);
        graficasButton.setOnClickListener(this);
        perfilButton.setOnClickListener(this);
        View recentActivityCard = view.findViewById(R.id.recentActivityCard);
        if (recentActivityCard != null) {
            recentActivityCard.setOnClickListener(v -> Toast.makeText(requireContext(), R.string.label_no_recent_activity, Toast.LENGTH_SHORT).show());
        }
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
            UsuarioAnimationNavigator.playAndNavigate(v, R.id.action_menu_to_usuario, R.raw.usuario, 6500f, 8000f);
        }
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_menu)
                .setMessage(R.string.mensaje_ayuda_menu)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}

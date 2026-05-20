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
import com.example.economix_android.Model.data.DataRepository;
import com.example.economix_android.Model.data.Gasto;
import com.example.economix_android.Model.data.Ingreso;
import com.example.economix_android.util.ProfileImageUtils;
import com.example.economix_android.util.UsuarioAnimationNavigator;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class menu extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View ayudaButton = view.findViewById(R.id.btnAyuda);
        View gastosButton = view.findViewById(R.id.tileGastos);
        View ingresosButton = view.findViewById(R.id.tileIngresos);
        View ahorroButton = view.findViewById(R.id.tileAhorro);
        View graficasButton = view.findViewById(R.id.tileGraficas);
        ImageView perfilButton = view.findViewById(R.id.btnPerfil);
        TextView saludoUsuario = view.findViewById(R.id.txtHolaUsuario);

        if (perfilButton != null) {
            ProfileImageUtils.applyProfileImage(requireContext(), perfilButton);
            perfilButton.setOnClickListener(this);
        }
        String perfil = SessionManager.getPerfil(requireContext());
        String saludo = perfil != null ? getString(R.string.label_hola_usuario, perfil) : getString(R.string.label_hola);
        if (saludoUsuario != null) {
            saludoUsuario.setText(saludo);
        }

        if (ayudaButton != null) {
            ayudaButton.setOnClickListener(v -> mostrarAyuda());
        }
        if (gastosButton != null) gastosButton.setOnClickListener(this);
        if (ingresosButton != null) ingresosButton.setOnClickListener(this);
        if (ahorroButton != null) ahorroButton.setOnClickListener(this);
        if (graficasButton != null) graficasButton.setOnClickListener(this);
        View recentActivityCard = view.findViewById(R.id.recentActivityCard);
        if (recentActivityCard != null) {
            recentActivityCard.setOnClickListener(v -> mostrarActividadReciente());
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


    private void mostrarActividadReciente() {
        DataRepository.refreshIngresos(requireContext(), new DataRepository.RepositoryCallback<List<Ingreso>>() {
            @Override
            public void onSuccess(List<Ingreso> result) {
                DataRepository.refreshGastos(requireContext(), new DataRepository.RepositoryCallback<List<Gasto>>() {
                    @Override
                    public void onSuccess(List<Gasto> gastos) {
                        mostrarDialogoActividadReciente(DataRepository.getIngresos(), DataRepository.getGastos());
                    }

                    @Override
                    public void onError(String message) {
                        mostrarDialogoActividadReciente(DataRepository.getIngresos(), DataRepository.getGastos());
                    }
                });
            }

            @Override
            public void onError(String message) {
                DataRepository.refreshGastos(requireContext(), new DataRepository.RepositoryCallback<List<Gasto>>() {
                    @Override
                    public void onSuccess(List<Gasto> gastos) {
                        mostrarDialogoActividadReciente(DataRepository.getIngresos(), DataRepository.getGastos());
                    }

                    @Override
                    public void onError(String error) {
                        mostrarDialogoActividadReciente(DataRepository.getIngresos(), DataRepository.getGastos());
                    }
                });
            }
        });
    }

    private void mostrarDialogoActividadReciente(List<Ingreso> ingresos, List<Gasto> gastos) {
        LocalDate limite = LocalDate.now().minusDays(2);
        List<ActividadItem> items = new ArrayList<>();

        for (Ingreso ingreso : ingresos) {
            LocalDate fecha = parseFecha(ingreso.getFecha());
            if (fecha != null && !fecha.isBefore(limite)) {
                items.add(new ActividadItem(fecha, "Ingreso", ingreso.getArticulo(), ingreso.getDescripcion()));
            }
        }
        for (Gasto gasto : gastos) {
            LocalDate fecha = parseFecha(gasto.getFecha());
            if (fecha != null && !fecha.isBefore(limite)) {
                items.add(new ActividadItem(fecha, "Gasto", gasto.getArticulo(), gasto.getDescripcion()));
            }
        }

        items.sort(Comparator.comparing((ActividadItem i) -> i.fecha).reversed());

        StringBuilder mensaje = new StringBuilder();
        if (items.isEmpty()) {
            mensaje.append(getString(R.string.label_no_recent_activity));
        } else {
            for (ActividadItem item : items) {
                mensaje.append("• ")
                        .append(item.tipo)
                        .append(": ")
                        .append(item.articulo != null ? item.articulo : "-")
                        .append(" · $")
                        .append(item.monto != null ? item.monto : "0")
                        .append(" · ")
                        .append(item.fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())))
                        .append("\n");
            }
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.title_recent_activity)
                .setMessage(mensaje.toString().trim())
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private LocalDate parseFecha(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) return null;
        String value = fecha.trim();
        DateTimeFormatter[] formatters = new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()),
                DateTimeFormatter.ISO_LOCAL_DATE
        };
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }

    private static class ActividadItem {
        final LocalDate fecha;
        final String tipo;
        final String articulo;
        final String monto;

        ActividadItem(LocalDate fecha, String tipo, String articulo, String monto) {
            this.fecha = fecha;
            this.tipo = tipo;
            this.articulo = articulo;
            this.monto = monto;
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

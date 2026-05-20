package com.example.economix_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.airbnb.lottie.LottieAnimationView;
import com.example.economix_android.Model.data.DataRepository;
import com.example.economix_android.Model.data.Gasto;
import com.example.economix_android.Model.data.Ingreso;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class RecentActivityFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recent_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LottieAnimationView animationView = view.findViewById(R.id.recentActivityAnimation);
        if (animationView != null) {
            animationView.setAnimation(R.raw.act_reci);
            animationView.playAnimation();
        }

        View backButton = view.findViewById(R.id.btnVolverActividad);
        if (backButton != null) {
            backButton.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        }

        TextView content = view.findViewById(R.id.tvRecentActivityDetail);
        cargarDetalle(content);
    }

    private void cargarDetalle(TextView content) {
        DataRepository.refreshIngresos(requireContext(), new DataRepository.RepositoryCallback<List<Ingreso>>() {
            @Override
            public void onSuccess(List<Ingreso> result) {
                DataRepository.refreshGastos(requireContext(), new DataRepository.RepositoryCallback<List<Gasto>>() {
                    @Override
                    public void onSuccess(List<Gasto> gastos) {
                        renderizar(content, DataRepository.getIngresos(), DataRepository.getGastos());
                    }

                    @Override
                    public void onError(String message) {
                        renderizar(content, DataRepository.getIngresos(), DataRepository.getGastos());
                    }
                });
            }

            @Override
            public void onError(String message) {
                DataRepository.refreshGastos(requireContext(), new DataRepository.RepositoryCallback<List<Gasto>>() {
                    @Override
                    public void onSuccess(List<Gasto> gastos) {
                        renderizar(content, DataRepository.getIngresos(), DataRepository.getGastos());
                    }

                    @Override
                    public void onError(String error) {
                        renderizar(content, DataRepository.getIngresos(), DataRepository.getGastos());
                    }
                });
            }
        });
    }

    private void renderizar(TextView content, List<Ingreso> ingresos, List<Gasto> gastos) {
        if (content == null) return;
        List<Item> items = colectar(ingresos, gastos);
        if (items.isEmpty()) {
            content.setText(R.string.label_no_recent_activity);
            return;
        }
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter out = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
        for (Item item : items) {
            sb.append("• ").append(item.tipo)
                    .append("\n  Categoría: ").append(item.categoria)
                    .append("\n  Monto: $").append(item.monto)
                    .append("\n  Fecha: ").append(item.fecha.format(out))
                    .append("\n\n");
        }
        content.setText(sb.toString().trim());
    }

    private List<Item> colectar(List<Ingreso> ingresos, List<Gasto> gastos) {
        LocalDate limite = LocalDate.now().minusDays(2);
        List<Item> items = new ArrayList<>();
        for (Ingreso i : ingresos) {
            LocalDate fecha = parseFecha(i.getFecha());
            if (fecha != null && !fecha.isBefore(limite)) {
                items.add(new Item(fecha, "Ingreso", valor(i.getPeriodo(), i.getArticulo()), valor(i.getDescripcion(), "0")));
            }
        }
        for (Gasto g : gastos) {
            LocalDate fecha = parseFecha(g.getFecha());
            if (fecha != null && !fecha.isBefore(limite)) {
                items.add(new Item(fecha, "Gasto", valor(g.getPeriodo(), g.getArticulo()), valor(g.getDescripcion(), "0")));
            }
        }
        items.sort(Comparator.comparing((Item x) -> x.fecha).reversed());
        return items;
    }

    private String valor(String main, String alt) {
        if (main != null && !main.trim().isEmpty()) return main.trim();
        return alt != null ? alt : "-";
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
            } catch (DateTimeParseException ignored) {}
        }
        return null;
    }

    private static class Item {
        final LocalDate fecha;
        final String tipo;
        final String categoria;
        final String monto;

        Item(LocalDate fecha, String tipo, String categoria, String monto) {
            this.fecha = fecha;
            this.tipo = tipo;
            this.categoria = categoria;
            this.monto = monto;
        }
    }
}

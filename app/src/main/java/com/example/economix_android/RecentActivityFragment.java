package com.example.economix_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        LinearLayout container = view.findViewById(R.id.recentActivityListContainer);
        cargarDetalle(container);
    }

    private void cargarDetalle(LinearLayout container) {
        DataRepository.refreshIngresos(requireContext(), new DataRepository.RepositoryCallback<List<Ingreso>>() {
            @Override
            public void onSuccess(List<Ingreso> result) {
                DataRepository.refreshGastos(requireContext(), new DataRepository.RepositoryCallback<List<Gasto>>() {
                    @Override
                    public void onSuccess(List<Gasto> gastos) {
                        renderizar(container, DataRepository.getIngresos(), DataRepository.getGastos());
                    }

                    @Override
                    public void onError(String message) {
                        renderizar(container, DataRepository.getIngresos(), DataRepository.getGastos());
                    }
                });
            }

            @Override
            public void onError(String message) {
                DataRepository.refreshGastos(requireContext(), new DataRepository.RepositoryCallback<List<Gasto>>() {
                    @Override
                    public void onSuccess(List<Gasto> gastos) {
                        renderizar(container, DataRepository.getIngresos(), DataRepository.getGastos());
                    }

                    @Override
                    public void onError(String error) {
                        renderizar(container, DataRepository.getIngresos(), DataRepository.getGastos());
                    }
                });
            }
        });
    }

    private void renderizar(LinearLayout container, List<Ingreso> ingresos, List<Gasto> gastos) {
        if (container == null) return;
        container.removeAllViews();
        List<Item> items = colectar(ingresos, gastos);
        if (items.isEmpty()) {
            TextView empty = new TextView(requireContext());
            empty.setText(R.string.label_no_recent_activity);
            empty.setTextColor(getResources().getColor(R.color.economix_text_secondary));
            empty.setTextSize(15f);
            container.addView(empty);
            return;
        }

        DateTimeFormatter out = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
        for (Item item : items) {
            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(20, 18, 20, 18);
            row.setBackgroundResource(R.drawable.bg_recent_activity);

            ImageView icon = new ImageView(requireContext());
            icon.setImageResource("Ingreso".equals(item.tipo) ? R.drawable.ic_trending_up_green : R.drawable.ic_trending_down_red);
            LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(54, 54);
            iconLp.rightMargin = 18;
            icon.setLayoutParams(iconLp);

            TextView text = new TextView(requireContext());
            text.setText(item.tipo + "\n" + "Categoría: " + item.categoria + "\nMonto: $" + item.monto + "\nFecha: " + item.fecha.format(out));
            text.setTextColor(getResources().getColor(R.color.economix_text_primary));
            text.setTextSize(14f);

            row.addView(icon);
            row.addView(text);

            LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rowLp.bottomMargin = 12;
            row.setLayoutParams(rowLp);
            container.addView(row);
        }
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

package com.example.economix_android.Model.graficas.gastos;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.economix_android.Model.data.DataRepository;
import com.example.economix_android.Model.data.Gasto;
import com.example.economix_android.Model.data.RegistroFinanciero;
import com.example.economix_android.R;
import com.example.economix_android.databinding.FragmentGraficaCircularGastosBinding;
import com.example.economix_android.util.ProfileImageUtils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class graficaCircularGastos extends Fragment {

    private FragmentGraficaCircularGastosBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGraficaCircularGastosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnPerfil.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.usuario));
        ProfileImageUtils.applyProfileImage(requireContext(), binding.btnPerfil);
        binding.btnAyudaIngInf.setOnClickListener(v -> mostrarAyuda());
        binding.buttonBack.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.graficasMenuGastos));

        configureChartAppearance();
        refreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        DataRepository.refreshGastos(requireContext(), new DataRepository.RepositoryCallback<List<Gasto>>() {
            @Override
            public void onSuccess(List<Gasto> result) {
                if (!isAdded()) {
                    return;
                }
                updateChartData();
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                mostrarMensajeError(message);
                updateChartData();
            }
        });
    }

    private void configureChartAppearance() {
        PieChart chart = binding.pieChartGastos;
        chart.setUsePercentValues(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawHoleEnabled(true);
        chart.setHoleRadius(55f);
        chart.setTransparentCircleRadius(60f);
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTextSize(12f);
        chart.setNoDataText(getString(R.string.gastos_chart_empty));
        chart.setNoDataTextColor(Color.WHITE);

        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.WHITE);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
    }

    private void updateChartData() {
        if (binding == null) {
            return;
        }

        PieChart chart = binding.pieChartGastos;

        Map<String, Float> montosPorCategoria = agruparPorCategoria(DataRepository.getGastos());

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        int[] colorResources = new int[]{
                R.color.economix_bar_1,
                R.color.economix_bar_2,
                R.color.economix_bar_3,
                R.color.economix_bar_4
        };

        int index = 0;
        for (Map.Entry<String, Float> entry : montosPorCategoria.entrySet()) {
            float monto = entry.getValue();
            if (monto <= 0f) {
                continue;
            }
            entries.add(new PieEntry(monto, entry.getKey()));
            int colorResId = colorResources[index % colorResources.length];
            colors.add(ContextCompat.getColor(requireContext(), colorResId));
            index++;
        }

        if (entries.isEmpty()) {
            chart.clear();
            chart.invalidate();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(6f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPieLabel(float value, PieEntry pieEntry) {
                return String.format(Locale.getDefault(), "%.2f", value);
            }
        });
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(12f);

        chart.setData(data);
        chart.highlightValues(null);
        chart.invalidate();
        chart.animateY(800);
    }

    private Map<String, Float> agruparPorCategoria(List<? extends RegistroFinanciero> registros) {
        Map<String, Float> montos = new LinkedHashMap<>();
        for (RegistroFinanciero registro : registros) {
            String categoria = normalizarEtiqueta(registro.getArticulo());
            float monto = registro.getMonto();
            if (monto <= 0f) {
                continue;
            }
            montos.put(categoria, montos.getOrDefault(categoria, 0f) + monto);
        }
        return montos;
    }

    private String normalizarEtiqueta(String articulo) {
        if (TextUtils.isEmpty(articulo)) {
            return getString(R.string.label_categoria_sin_definir);
        }
        return articulo.trim();
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_grafica_circular_gastos)
                .setMessage(R.string.mensaje_ayuda_grafica_circular_gastos)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void mostrarMensajeError(String message) {
        String texto = message != null ? message : getString(R.string.mensaje_error_servidor);
        Toast.makeText(requireContext(), texto, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

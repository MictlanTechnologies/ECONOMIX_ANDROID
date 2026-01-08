package com.example.economix_android.Model.graficas.IngresosVsGastos;

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
import com.example.economix_android.Model.data.Ingreso;
import com.example.economix_android.Model.data.RegistroFinanciero;
import com.example.economix_android.R;
import com.example.economix_android.databinding.FragmentGraficaBarrasIngreosVsGastosBinding;
import com.example.economix_android.util.ProfileImageUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Locale;

public class graficaBarrasIngreosVsGastos extends Fragment {

    private FragmentGraficaBarrasIngreosVsGastosBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGraficaBarrasIngreosVsGastosBinding.inflate(inflater, container, false);
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
                Navigation.findNavController(v).navigate(R.id.graficasMenuIngresosVsGastos));

        configureChartAppearance();
        refreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        DataRepository.refreshIngresos(new DataRepository.RepositoryCallback<List<Ingreso>>() {
            @Override
            public void onSuccess(List<Ingreso> result) {
                cargarGastos();
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                mostrarMensajeError(message);
                cargarGastos();
            }
        });
    }

    private void cargarGastos() {
        DataRepository.refreshGastos(new DataRepository.RepositoryCallback<List<Gasto>>() {
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
        BarChart chart = binding.barChartIngresosVsGastos;
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setNoDataText(getString(R.string.chart_empty_ingresos_vs_gastos));
        chart.setNoDataTextColor(Color.WHITE);
        chart.setFitBars(false);

        chart.getAxisRight().setEnabled(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setGridColor(Color.parseColor("#33FFFFFF"));

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);

        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.WHITE);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setForm(Legend.LegendForm.SQUARE);
    }

    private void updateChartData() {
        if (binding == null) {
            return;
        }

        BarChart chart = binding.barChartIngresosVsGastos;

        Map<String, Float> ingresosPorPeriodo = agruparPorPeriodo(DataRepository.getIngresosHistorial());
        Map<String, Float> gastosPorPeriodo = agruparPorPeriodo(DataRepository.getGastos());

        LinkedHashSet<String> periodos = new LinkedHashSet<>();
        periodos.addAll(ingresosPorPeriodo.keySet());
        periodos.addAll(gastosPorPeriodo.keySet());

        if (periodos.isEmpty()) {
            chart.clear();
            chart.invalidate();
            return;
        }

        List<String> etiquetas = new ArrayList<>(periodos);
        List<BarEntry> entradasIngresos = new ArrayList<>();
        List<BarEntry> entradasGastos = new ArrayList<>();

        for (int index = 0; index < etiquetas.size(); index++) {
            String periodo = etiquetas.get(index);
            entradasIngresos.add(new BarEntry(index, ingresosPorPeriodo.getOrDefault(periodo, 0f)));
            entradasGastos.add(new BarEntry(index, gastosPorPeriodo.getOrDefault(periodo, 0f)));
        }

        BarDataSet dataSetIngresos = new BarDataSet(entradasIngresos, getString(R.string.label_ingresos));
        dataSetIngresos.setColor(ContextCompat.getColor(requireContext(), R.color.economix_bar_1));
        dataSetIngresos.setValueTextColor(Color.WHITE);

        BarDataSet dataSetGastos = new BarDataSet(entradasGastos, getString(R.string.label_gastos));
        dataSetGastos.setColor(ContextCompat.getColor(requireContext(), R.color.economix_bar_3));
        dataSetGastos.setValueTextColor(Color.WHITE);

        BarData data = new BarData(dataSetIngresos, dataSetGastos);
        data.setValueTextSize(10f);
        data.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return String.format(Locale.getDefault(), "%.2f", barEntry.getY());
            }
        });

        float groupSpace = 0.3f;
        float barSpace = 0.05f;
        float barWidth = 0.3f;

        data.setBarWidth(barWidth);
        chart.setData(data);

        float groupWidth = data.getGroupWidth(groupSpace, barSpace);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(etiquetas));
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(0f + groupWidth * etiquetas.size());

        chart.groupBars(0f, groupSpace, barSpace);
        chart.invalidate();
        chart.animateY(800);
    }

    private Map<String, Float> agruparPorPeriodo(List<? extends RegistroFinanciero> registros) {
        Map<String, Float> conteo = new LinkedHashMap<>();
        for (RegistroFinanciero registro : registros) {
            String periodo = normalizarEtiqueta(registro.getPeriodo());
            float monto = registro.getMonto();
            if (monto <= 0f) {
                continue;
            }
            conteo.put(periodo, conteo.getOrDefault(periodo, 0f) + monto);
        }
        return conteo;
    }

    private String normalizarEtiqueta(String periodo) {
        if (TextUtils.isEmpty(periodo)) {
            return getString(R.string.label_periodo_sin_definir);
        }
        return periodo.trim();
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_grafica_barras_ingresos_vs_gastos)
                .setMessage(R.string.mensaje_ayuda_grafica_barras_ingresos_vs_gastos)
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

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
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.economix_android.Model.data.DataRepository;
import com.example.economix_android.Model.data.Gasto;
import com.example.economix_android.Model.data.RegistroFinanciero;
import com.example.economix_android.R;
import com.example.economix_android.databinding.FragmentGraficaBarrasGastosBinding;
import com.example.economix_android.util.ProfileImageUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class graficaBarrasGastos extends Fragment {

    private FragmentGraficaBarrasGastosBinding binding;
    private LocalDate filtroInicio;
    private LocalDate filtroFin;
    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGraficaBarrasGastosBinding.inflate(inflater, container, false);
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
        configurarFiltros();
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
        BarChart chart = binding.barChartGastos;
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setNoDataText(getString(R.string.gastos_chart_empty));
        chart.setNoDataTextColor(Color.WHITE);
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

        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.WHITE);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setWordWrapEnabled(true);
        legend.setTextSize(12f);
        legend.setFormSize(12f);
        legend.setFormToTextSpace(8f);
        legend.setXEntrySpace(16f);
        legend.setYEntrySpace(8f);
    }

    private void updateChartData() {
        if (binding == null) {
            return;
        }

        BarChart chart = binding.barChartGastos;
        Map<String, Float> montosPorPeriodo =
                agruparPorPeriodo(filtrarPorFecha(DataRepository.getGastos()));

        List<String> etiquetas = new ArrayList<>(montosPorPeriodo.keySet());
        List<BarEntry> entradas = new ArrayList<>();

        for (int index = 0; index < etiquetas.size(); index++) {
            String periodo = etiquetas.get(index);
            entradas.add(new BarEntry(index, montosPorPeriodo.getOrDefault(periodo, 0f)));
        }

        if (entradas.isEmpty()) {
            chart.clear();
            chart.invalidate();
            return;
        }

        BarDataSet dataSet = new BarDataSet(entradas, getString(R.string.label_gastos));
        dataSet.setColor(ContextCompat.getColor(requireContext(), R.color.economix_bar_3));
        dataSet.setValueTextColor(Color.WHITE);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);
        data.setValueTextSize(10f);
        data.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return String.format(Locale.getDefault(), "%.2f", barEntry.getY());
            }
        });

        chart.setData(data);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(etiquetas));
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(entradas.size() - 0.5f);

        chart.invalidate();
        chart.animateY(800);
    }

    private void configurarFiltros() {
        binding.btnFiltroFechaGastos.setOnClickListener(v -> mostrarSelectorFechas());
        binding.btnFiltroFechaGastos.setOnLongClickListener(v -> {
            limpiarFiltroFechas();
            return true;
        });
        actualizarTextoRango();
    }

    private void mostrarSelectorFechas() {
        MaterialDatePicker<Pair<Long, Long>> picker =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText(R.string.titulo_seleccionar_periodo)
                        .build();
        picker.addOnPositiveButtonClickListener(selection -> {
            if (selection == null || selection.first == null || selection.second == null) {
                return;
            }
            filtroInicio = Instant.ofEpochMilli(selection.first)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            filtroFin = Instant.ofEpochMilli(selection.second)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            actualizarTextoRango();
            updateChartData();
        });
        picker.show(getParentFragmentManager(), "gastos_barras_date_range");
    }

    private void limpiarFiltroFechas() {
        filtroInicio = null;
        filtroFin = null;
        actualizarTextoRango();
        updateChartData();
    }

    private void actualizarTextoRango() {
        if (filtroInicio == null || filtroFin == null) {
            binding.tvRangoFechasGastos.setText(getString(R.string.label_todas_fechas));
            return;
        }
        String rango = getString(R.string.label_rango_fechas,
                dateFormatter.format(filtroInicio),
                dateFormatter.format(filtroFin));
        binding.tvRangoFechasGastos.setText(rango);
    }

    private <T extends RegistroFinanciero> List<T> filtrarPorFecha(List<T> registros) {
        if (filtroInicio == null || filtroFin == null) {
            return registros;
        }
        List<T> resultado = new ArrayList<>();
        for (T registro : registros) {
            LocalDate fechaRegistro = parseFecha(registro.getFecha());
            if (fechaRegistro == null) {
                continue;
            }
            if (!fechaRegistro.isBefore(filtroInicio) && !fechaRegistro.isAfter(filtroFin)) {
                resultado.add(registro);
            }
        }
        return resultado;
    }

    private LocalDate parseFecha(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(fecha, dateFormatter);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private Map<String, Float> agruparPorPeriodo(List<? extends RegistroFinanciero> registros) {
        Map<String, Float> montos = new LinkedHashMap<>();
        for (RegistroFinanciero registro : registros) {
            String periodo = normalizarPeriodo(registro.getPeriodo());
            float monto = registro.getMonto();
            if (monto <= 0f) {
                continue;
            }
            montos.put(periodo, montos.getOrDefault(periodo, 0f) + monto);
        }
        return montos;
    }

    private String normalizarPeriodo(String periodo) {
        if (TextUtils.isEmpty(periodo)) {
            return getString(R.string.label_periodo_sin_definir);
        }
        return periodo.trim();
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_grafica_barras_gastos)
                .setMessage(R.string.mensaje_ayuda_grafica_barras_gastos)
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

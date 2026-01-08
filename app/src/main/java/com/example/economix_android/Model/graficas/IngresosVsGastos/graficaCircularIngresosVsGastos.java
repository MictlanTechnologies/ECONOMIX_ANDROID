package com.example.economix_android.Model.graficas.IngresosVsGastos;

import android.graphics.Color;
import android.os.Bundle;
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
import com.example.economix_android.databinding.FragmentGraficaCircularIngresosVsGastosBinding;
import com.example.economix_android.util.ProfileImageUtils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class graficaCircularIngresosVsGastos extends Fragment {

    private FragmentGraficaCircularIngresosVsGastosBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGraficaCircularIngresosVsGastosBinding.inflate(inflater, container, false);
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
        DataRepository.refreshIngresos(requireContext(), new DataRepository.RepositoryCallback<List<Ingreso>>() {
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
        PieChart chart = binding.pieChartIngresosVsGastos;
        chart.setUsePercentValues(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawHoleEnabled(true);
        chart.setHoleRadius(55f);
        chart.setTransparentCircleRadius(60f);
        chart.setEntryLabelColor(Color.WHITE);
        chart.setNoDataText(getString(R.string.chart_empty_ingresos_vs_gastos));
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

        PieChart chart = binding.pieChartIngresosVsGastos;

        float ingresos = sumarMontos(DataRepository.getIngresosHistorial());
        float gastos = sumarMontos(DataRepository.getGastos());

        if (ingresos <= 0f && gastos <= 0f) {
            chart.clear();
            chart.invalidate();
            return;
        }

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        if (ingresos > 0f) {
            entries.add(new PieEntry(ingresos, getString(R.string.label_ingresos)));
            colors.add(ContextCompat.getColor(requireContext(), R.color.economix_bar_1));
        }

        if (gastos > 0f) {
            entries.add(new PieEntry(gastos, getString(R.string.label_gastos)));
            colors.add(ContextCompat.getColor(requireContext(), R.color.economix_bar_3));
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

    private float sumarMontos(List<? extends RegistroFinanciero> registros) {
        float total = 0f;
        for (RegistroFinanciero registro : registros) {
            total += registro.getMonto();
        }
        return total;
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_grafica_circular_ingresos_vs_gastos)
                .setMessage(R.string.mensaje_ayuda_grafica_circular_ingresos_vs_gastos)
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

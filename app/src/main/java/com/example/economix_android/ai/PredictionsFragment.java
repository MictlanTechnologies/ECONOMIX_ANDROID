package com.example.economix_android.ai;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.economix_android.auth.SessionManager;
import com.example.economix_android.databinding.FragmentPredictionsBinding;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PredictionsFragment extends Fragment {

    private FragmentPredictionsBinding binding;
    private AiViewModel viewModel;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPredictionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AiViewModel.class);

        setupDatePicker(binding.etFromA);
        setupDatePicker(binding.etToA);
        setupDatePicker(binding.etFromB);
        setupDatePicker(binding.etToB);

        LocalDate now = LocalDate.now();
        binding.etToA.setText(now.format(formatter));
        binding.etFromA.setText(now.minusDays(7).format(formatter));
        binding.etToB.setText(now.minusDays(8).format(formatter));
        binding.etFromB.setText(now.minusDays(15).format(formatter));

        binding.btnAnalizar.setOnClickListener(v -> analizar());

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
            boolean isLoading = Boolean.TRUE.equals(loading);
            binding.progressAnalysis.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnAnalizar.setEnabled(!isLoading);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.trim().isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getData().observe(getViewLifecycleOwner(), this::renderData);
    }

    private void analizar() {
        Integer userId = SessionManager.getUserId(requireContext());
        if (userId == null) {
            Toast.makeText(requireContext(), "Sesión no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        LocalDate fromA = parseDate(binding.etFromA.getText() != null ? binding.etFromA.getText().toString() : "");
        LocalDate toA = parseDate(binding.etToA.getText() != null ? binding.etToA.getText().toString() : "");
        LocalDate fromB = parseDate(binding.etFromB.getText() != null ? binding.etFromB.getText().toString() : "");
        LocalDate toB = parseDate(binding.etToB.getText() != null ? binding.etToB.getText().toString() : "");

        if (fromA == null || toA == null || fromB == null || toB == null) {
            Toast.makeText(requireContext(), "Completa periodos A y B con fechas válidas", Toast.LENGTH_SHORT).show();
            return;
        }

        int horizon = binding.rb30Dias.isChecked() ? 30 : 7;
        LocalDate fromRange = LocalDate.now().minusDays(horizon);
        LocalDate toRange = LocalDate.now();

        viewModel.analyze(userId, horizon, fromRange, toRange, fromA, toA, fromB, toB);
    }

    private void renderData(AiViewModel.AnalysisData data) {
        if (data == null) return;

        if (data.spendForecast != null) {
            binding.tvForecast.setText("Predicción gasto: "
                    + money(data.spendForecast.getExpectedSpend())
                    + " (IC95% " + money(data.spendForecast.getLower95())
                    + " - " + money(data.spendForecast.getUpper95()) + ")");
        }

        List<String> budgetItems = new ArrayList<>();
        if (data.budgetRisk != null && data.budgetRisk.getItems() != null) {
            for (AiModels.BudgetRiskItem item : data.budgetRisk.getItems()) {
                budgetItems.add((item.getCategoria() != null ? item.getCategoria() : "Sin categoría")
                        + " | consumido=" + money(item.getMontoConsumido())
                        + " | %=" + money(item.getPorcentajeConsumido())
                        + " | riesgo=" + (item.getRiesgo() != null ? item.getRiesgo() : "N/A"));
            }
        }
        binding.lvBudgetRisk.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, budgetItems));

        List<String> anomalyItems = new ArrayList<>();
        if (data.anomalies != null && data.anomalies.getAnomalies() != null) {
            for (AiModels.AnomalyItem anomaly : data.anomalies.getAnomalies()) {
                anomalyItems.add((anomaly.getFecha() != null ? anomaly.getFecha().toString() : "")
                        + " | " + (anomaly.getArticulo() != null ? anomaly.getArticulo() : "")
                        + " | monto=" + money(anomaly.getMonto())
                        + " | z=" + money(anomaly.getRobustZScore()));
            }
        }
        binding.lvAnomalies.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, anomalyItems));

        if (data.confidenceInterval != null) {
            binding.tvConfidenceInterval.setText("IC media (" + data.confidenceInterval.getMetricDefinition() + "): "
                    + money(data.confidenceInterval.getMean()) + " ["
                    + money(data.confidenceInterval.getLower()) + " - "
                    + money(data.confidenceInterval.getUpper()) + "]");
        }

        if (data.hypothesisTest != null) {
            binding.tvHypothesis.setText("Prueba Welch p-value="
                    + money(data.hypothesisTest.getPValue())
                    + "\n" + (data.hypothesisTest.getConclusion() != null ? data.hypothesisTest.getConclusion() : "Sin conclusión"));
        }
    }

    private LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text, formatter);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private void setupDatePicker(com.google.android.material.textfield.TextInputEditText editText) {
        editText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                    (view, year, month, dayOfMonth) -> editText.setText(LocalDate.of(year, month + 1, dayOfMonth).format(formatter)),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });
    }

    private String money(BigDecimal value) {
        if (value == null) return "N/A";
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}

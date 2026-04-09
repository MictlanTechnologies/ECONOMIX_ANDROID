package com.example.economix_android.Model.presupuestos;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import com.example.economix_android.R;
import com.example.economix_android.databinding.FragmentPresupuestosBinding;
import com.example.economix_android.network.dto.PresupuestoDto;
import com.example.economix_android.network.repository.PresupuestoRepository;
import com.example.economix_android.util.ProfileImageUtils;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresupuestosFragment extends Fragment {

    private FragmentPresupuestosBinding binding;
    private final PresupuestoRepository repository = new PresupuestoRepository();
    private final Map<Integer, String> chipCategoryMap = new HashMap<>();

    private final String[] meses = {
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPresupuestosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initChipCategoryMap();
        setupMonthYearDropdowns();
        setupQuickAmountButtons();

        ProfileImageUtils.applyProfileImage(requireContext(), binding.btnPerfilPres);
        binding.btnAyudaPres.setOnClickListener(v -> mostrarAyuda());
        binding.btnGuardarPres.setOnClickListener(v -> guardarPresupuesto());
        binding.btnMenuAhorroPres.setOnClickListener(v -> navigateSafely(v, R.id.navigation_ahorro));
        binding.btnMenuPresupuestosPres.setOnClickListener(v -> navigateSafely(v, R.id.navigation_presupuestos));

        // Pre-select current month and year
        Calendar now = Calendar.getInstance();
        AutoCompleteTextView mesView = (AutoCompleteTextView) binding.etMesPres;
        mesView.setText(meses[now.get(Calendar.MONTH)], false);
        AutoCompleteTextView anioView = (AutoCompleteTextView) binding.etAnioPres;
        anioView.setText(String.valueOf(now.get(Calendar.YEAR)), false);
    }

    private void initChipCategoryMap() {
        chipCategoryMap.put(R.id.chipPresAlimentacion, getString(R.string.cat_alimentacion));
        chipCategoryMap.put(R.id.chipPresTransporte, getString(R.string.cat_transporte));
        chipCategoryMap.put(R.id.chipPresEntretenimiento, getString(R.string.cat_entretenimiento));
        chipCategoryMap.put(R.id.chipPresSalud, getString(R.string.cat_salud));
        chipCategoryMap.put(R.id.chipPresEducacion, getString(R.string.cat_educacion));
        chipCategoryMap.put(R.id.chipPresServicios, getString(R.string.cat_servicios));
        chipCategoryMap.put(R.id.chipPresHogar, getString(R.string.cat_hogar));
        chipCategoryMap.put(R.id.chipPresOtro, getString(R.string.cat_otro));
    }

    private void setupMonthYearDropdowns() {
        ArrayAdapter<String> mesAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, meses);
        AutoCompleteTextView mesView = (AutoCompleteTextView) binding.etMesPres;
        mesView.setAdapter(mesAdapter);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[5];
        for (int i = 0; i < 5; i++) {
            years[i] = String.valueOf(currentYear + i);
        }
        ArrayAdapter<String> anioAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, years);
        AutoCompleteTextView anioView = (AutoCompleteTextView) binding.etAnioPres;
        anioView.setAdapter(anioAdapter);
    }

    private void setupQuickAmountButtons() {
        binding.btnMonto500.setOnClickListener(v -> binding.etMontoMaxPres.setText("500"));
        binding.btnMonto1000.setOnClickListener(v -> binding.etMontoMaxPres.setText("1000"));
        binding.btnMonto2500.setOnClickListener(v -> binding.etMontoMaxPres.setText("2500"));
        binding.btnMonto5000.setOnClickListener(v -> binding.etMontoMaxPres.setText("5000"));
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_presupuestos)
                .setMessage(R.string.mensaje_ayuda_presupuestos)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private String getSelectedCategory() {
        java.util.List<Integer> checkedIds = binding.chipGroupCategoriaPres.getCheckedChipIds();
        if (checkedIds.isEmpty()) return null;
        return chipCategoryMap.get(checkedIds.get(0));
    }

    private int getMesSeleccionado() {
        String mesText = binding.etMesPres.getText().toString();
        for (int i = 0; i < meses.length; i++) {
            if (meses[i].equals(mesText)) {
                return i + 1;
            }
        }
        return -1;
    }

    private int getAnioSeleccionado() {
        String anioText = binding.etAnioPres.getText().toString();
        if (TextUtils.isEmpty(anioText)) return -1;
        try {
            return Integer.parseInt(anioText);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void guardarPresupuesto() {
        String categoria = getSelectedCategory();
        String montoText = binding.etMontoMaxPres.getText() != null
                ? binding.etMontoMaxPres.getText().toString().trim() : "";
        int mes = getMesSeleccionado();
        int anio = getAnioSeleccionado();

        if (categoria == null || TextUtils.isEmpty(montoText) || mes < 1 || anio < 1) {
            Toast.makeText(requireContext(), R.string.error_campos_presupuesto, Toast.LENGTH_SHORT).show();
            return;
        }

        BigDecimal monto;
        try {
            String normalizado = montoText.replace(",", ".");
            monto = new BigDecimal(normalizado);
            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                Toast.makeText(requireContext(), R.string.error_monto_presupuesto, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), R.string.error_monto_presupuesto, Toast.LENGTH_SHORT).show();
            return;
        }

        PresupuestoDto dto = PresupuestoDto.builder()
                .categoria(categoria)
                .montoMaximo(monto)
                .montoGastado(BigDecimal.ZERO)
                .mes(mes)
                .anio(anio)
                .build();

        binding.btnGuardarPres.setEnabled(false);
        repository.guardarPresupuesto(dto, new Callback<PresupuestoDto>() {
            @Override
            public void onResponse(@NonNull Call<PresupuestoDto> call, @NonNull Response<PresupuestoDto> response) {
                if (!isAdded()) return;
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), R.string.mensaje_presupuesto_guardado, Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                } else {
                    Toast.makeText(requireContext(), R.string.mensaje_error_operacion, Toast.LENGTH_SHORT).show();
                }
                binding.btnGuardarPres.setEnabled(true);
            }

            @Override
            public void onFailure(@NonNull Call<PresupuestoDto> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), R.string.mensaje_error_servidor, Toast.LENGTH_SHORT).show();
                binding.btnGuardarPres.setEnabled(true);
            }
        });
    }

    private void limpiarCampos() {
        binding.chipGroupCategoriaPres.clearCheck();
        binding.etMontoMaxPres.setText("");
        Calendar now = Calendar.getInstance();
        AutoCompleteTextView mesView = (AutoCompleteTextView) binding.etMesPres;
        mesView.setText(meses[now.get(Calendar.MONTH)], false);
        AutoCompleteTextView anioView = (AutoCompleteTextView) binding.etAnioPres;
        anioView.setText(String.valueOf(now.get(Calendar.YEAR)), false);
    }

    private void navigateSafely(View view, int destinationId) {
        NavController navController = Navigation.findNavController(view);
        NavDestination currentDestination = navController.getCurrentDestination();
        if (currentDestination == null || currentDestination.getId() != destinationId) {
            navController.navigate(destinationId);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

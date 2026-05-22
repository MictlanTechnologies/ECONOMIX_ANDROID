package com.example.economix_android.Model.presupuestos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.economix_android.R;
import com.example.economix_android.auth.SessionManager;
import com.example.economix_android.databinding.FragmentPresupuestosBinding;
import com.example.economix_android.network.dto.PresupuestoDto;
import com.example.economix_android.network.repository.PresupuestoRepository;
import com.example.economix_android.util.ProfileImageUtils;
import com.example.economix_android.util.UsuarioAnimationNavigator;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresupuestosFragment extends Fragment {

    private FragmentPresupuestosBinding binding;
    private final PresupuestoRepository repository = new PresupuestoRepository();
    private final Map<Integer, String> chipCategoryMap = new HashMap<>();
    private PresupuestoAdapter adapter;
    private PresupuestoDto presupuestoSeleccionado;

    private final String[] meses = {
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPresupuestosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initChipCategoryMap();
        setupMonthYearDropdowns();
        setupQuickAmountButtons();
        setupRecycler();

        ProfileImageUtils.applyProfileImage(requireContext(), binding.btnPerfilPres);
        binding.btnPerfilPres.setOnClickListener(v -> UsuarioAnimationNavigator.playAndNavigate(
                v,
                R.id.action_navigation_presupuestos_to_usuario,
                R.raw.usuario,
                6500f,
                8000f
        ));

        binding.btnAyudaPres.setOnClickListener(v -> mostrarAyuda());
        binding.btnGuardarPres.setOnClickListener(v -> guardarPresupuesto());
        binding.btnEliminarPres.setOnClickListener(v -> eliminarPresupuestoSeleccionado());
        binding.btnLimpiarPres.setOnClickListener(v -> limpiarCampos());

        View.OnClickListener bottomNavListener = v -> {
            int id = v.getId();
            if (id == R.id.navGastos) {
                navigateSafely(v, R.id.navigation_gastos);
            } else if (id == R.id.navIngresos) {
                navigateSafely(v, R.id.navigation_ingresos);
            } else if (id == R.id.navAhorro) {
                navigateSafely(v, R.id.navigation_ahorro);
            } else if (id == R.id.navGraficas) {
                navigateSafely(v, R.id.navigation_graficas);
            } else if (id == R.id.navMenuMini) {
                navigateSafely(v, R.id.menu);
            }
        };

        binding.navGastos.setOnClickListener(bottomNavListener);
        binding.navIngresos.setOnClickListener(bottomNavListener);
        binding.navAhorro.setOnClickListener(bottomNavListener);
        binding.navGraficas.setOnClickListener(bottomNavListener);
        binding.navMenuMini.setOnClickListener(bottomNavListener);

        LocalDate now = LocalDate.now();
        binding.etMesPres.setText(String.valueOf(now.getMonthValue()));
        binding.etAnioPres.setText(String.valueOf(now.getYear()));

        cargarPresupuestosPeriodoActual();
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
        binding.etMesPres.setShowSoftInputOnFocus(false);
        binding.etAnioPres.setShowSoftInputOnFocus(false);
        View.OnClickListener openCalendar = v -> mostrarSelectorMesAnio();
        binding.etMesPres.setOnClickListener(openCalendar);
        binding.etAnioPres.setOnClickListener(openCalendar);
        binding.etMesPres.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) mostrarSelectorMesAnio(); });
        binding.etAnioPres.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) mostrarSelectorMesAnio(); });
    }

    private void mostrarSelectorMesAnio() {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.titulo_seleccionar_periodo)
                .build();
        picker.addOnPositiveButtonClickListener(selection -> {
            if (selection == null || binding == null) return;
            LocalDate selectedDate = Instant.ofEpochMilli(selection)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            binding.etMesPres.setText(String.valueOf(selectedDate.getMonthValue()));
            binding.etAnioPres.setText(String.valueOf(selectedDate.getYear()));
            cargarPresupuestosPeriodoActual();
        });
        picker.show(getParentFragmentManager(), "presupuesto_month_year_picker");
    }

    private void setupQuickAmountButtons() {
        binding.btnMonto500.setOnClickListener(v -> binding.etMontoMaxPres.setText("500"));
        binding.btnMonto1000.setOnClickListener(v -> binding.etMontoMaxPres.setText("1000"));
        binding.btnMonto2500.setOnClickListener(v -> binding.etMontoMaxPres.setText("2500"));
        binding.btnMonto5000.setOnClickListener(v -> binding.etMontoMaxPres.setText("5000"));
    }

    private void setupRecycler() {
        adapter = new PresupuestoAdapter(this::cargarPresupuestoEnFormulario);
        binding.rvPresupuestos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvPresupuestos.setAdapter(adapter);
    }

    private void cargarPresupuestosPeriodoActual() {
        Integer idUsuario = SessionManager.getUserId(requireContext());
        Integer mes = getMesSeleccionado();
        Integer anio = getAnioSeleccionado();
        if (idUsuario == null || mes == null || anio == null) return;

        repository.obtenerPresupuestosPorPeriodo(idUsuario, mes, anio, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<java.util.List<PresupuestoDto>> call,
                                   @NonNull Response<java.util.List<PresupuestoDto>> response) {
                if (!isAdded() || binding == null) return;
                java.util.List<PresupuestoDto> body = response.body() == null
                        ? Collections.emptyList() : response.body();
                adapter.submitList(body);
                binding.tvPresupuestosVacio.setVisibility(body.isEmpty() ? View.VISIBLE : View.GONE);
                binding.rvPresupuestos.setVisibility(body.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onFailure(@NonNull Call<java.util.List<PresupuestoDto>> call, @NonNull Throwable t) {
                if (!isAdded() || binding == null) return;
                Toast.makeText(requireContext(), R.string.mensaje_error_servidor, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getSelectedCategory() {
        int id = binding.chipGroupCategoriaPres.getCheckedChipId();
        return id == -1 ? null : chipCategoryMap.get(id);
    }

    private Integer getMesSeleccionado() {
        try {
            int m = Integer.parseInt(Objects.requireNonNull(binding.etMesPres.getText()).toString().trim());
            return (m >= 1 && m <= 12) ? m : null;
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getAnioSeleccionado() {
        try {
            int a = Integer.parseInt(Objects.requireNonNull(binding.etAnioPres.getText()).toString().trim());
            return a > 0 ? a : null;
        } catch (Exception e) {
            return null;
        }
    }

    private void guardarPresupuesto() {
        Integer idUsuario = SessionManager.getUserId(requireContext());
        if (idUsuario == null) {
            Toast.makeText(requireContext(), R.string.error_usuario_no_autenticado, Toast.LENGTH_SHORT).show();
            return;
        }

        String categoria = getSelectedCategory();
        String montoTxt = binding.etMontoMaxPres.getText() != null
                ? binding.etMontoMaxPres.getText().toString().trim() : "";
        Integer mes = getMesSeleccionado();
        Integer anio = getAnioSeleccionado();

        if (categoria == null || montoTxt.isEmpty() || mes == null || anio == null) {
            Toast.makeText(requireContext(), R.string.error_campos_presupuesto, Toast.LENGTH_SHORT).show();
            return;
        }

        BigDecimal monto;
        try {
            monto = new BigDecimal(montoTxt);
        } catch (Exception e) {
            Toast.makeText(requireContext(), R.string.error_monto_presupuesto, Toast.LENGTH_SHORT).show();
            return;
        }

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            Toast.makeText(requireContext(), R.string.error_monto_presupuesto, Toast.LENGTH_SHORT).show();
            return;
        }

        PresupuestoDto dto = PresupuestoDto.builder()
                .idUsuario(idUsuario)
                .categoria(categoria)
                .montoMaximo(monto)
                .mes(mes)
                .anio(anio)
                .build();

        Callback<PresupuestoDto> cb = new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PresupuestoDto> call, @NonNull Response<PresupuestoDto> response) {
                if (!isAdded() || binding == null) return;
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(),
                            presupuestoSeleccionado == null ? R.string.mensaje_presupuesto_guardado : R.string.mensaje_presupuesto_actualizado,
                            Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                    cargarPresupuestosPeriodoActual();
                } else if (response.code() == 409) {
                    Toast.makeText(requireContext(), R.string.error_presupuesto_duplicado, Toast.LENGTH_SHORT).show();
                } else if (response.code() == 400) {
                    Toast.makeText(requireContext(), R.string.error_campos_presupuesto, Toast.LENGTH_SHORT).show();
                } else if (response.code() == 404) {
                    Toast.makeText(requireContext(), "No se encontró el presupuesto.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), R.string.mensaje_error_operacion, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PresupuestoDto> call, @NonNull Throwable t) {
                if (!isAdded() || binding == null) return;
                Toast.makeText(requireContext(), R.string.mensaje_error_servidor, Toast.LENGTH_SHORT).show();
            }
        };

        if (presupuestoSeleccionado == null) {
            repository.guardarPresupuesto(dto, cb);
        } else {
            repository.actualizarPresupuesto(presupuestoSeleccionado.getIdPresupuesto(), dto, cb);
        }
    }

    private void eliminarPresupuestoSeleccionado() {
        if (presupuestoSeleccionado == null || presupuestoSeleccionado.getIdPresupuesto() == null) {
            Toast.makeText(requireContext(), "Selecciona un presupuesto para eliminar.", Toast.LENGTH_SHORT).show();
            return;
        }

        repository.eliminarPresupuesto(presupuestoSeleccionado.getIdPresupuesto(), new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (!isAdded() || binding == null) return;
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), R.string.mensaje_presupuesto_eliminado, Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                    cargarPresupuestosPeriodoActual();
                } else {
                    Toast.makeText(requireContext(), R.string.mensaje_error_operacion, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                if (!isAdded() || binding == null) return;
                Toast.makeText(requireContext(), R.string.mensaje_error_servidor, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void limpiarCampos() {
        binding.chipGroupCategoriaPres.clearCheck();
        binding.etMontoMaxPres.setText("");
        LocalDate now = LocalDate.now();
        binding.etMesPres.setText(String.valueOf(now.getMonthValue()));
        binding.etAnioPres.setText(String.valueOf(now.getYear()));
        presupuestoSeleccionado = null;
        binding.btnGuardarPres.setText(R.string.label_guardar);
        binding.btnEliminarPres.setEnabled(false);
    }

    private void cargarPresupuestoEnFormulario(PresupuestoDto p) {
        presupuestoSeleccionado = p;
        for (Map.Entry<Integer, String> e : chipCategoryMap.entrySet()) {
            if (Objects.equals(e.getValue(), p.getCategoria())) {
                binding.chipGroupCategoriaPres.check(e.getKey());
                break;
            }
        }
        binding.etMontoMaxPres.setText(p.getMontoMaximo() != null ? p.getMontoMaximo().toPlainString() : "");
        binding.etMesPres.setText(String.valueOf(p.getMes()));
        binding.etAnioPres.setText(String.valueOf(p.getAnio()));
        binding.btnGuardarPres.setText(R.string.label_actualizar);
        binding.btnEliminarPres.setEnabled(true);
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_presupuestos)
                .setMessage(R.string.mensaje_ayuda_presupuestos)
                .setPositiveButton(android.R.string.ok, null)
                .show();
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

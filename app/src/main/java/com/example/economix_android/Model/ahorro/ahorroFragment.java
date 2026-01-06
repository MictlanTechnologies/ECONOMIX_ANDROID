package com.example.economix_android.Model.ahorro;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.economix_android.Model.ahorro.AhorroItem;
import com.example.economix_android.databinding.FragmentAhorroBinding;
import com.example.economix_android.network.dto.AhorroDto;
import com.example.economix_android.network.repository.AhorroRepository;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ahorroFragment extends Fragment {

    private FragmentAhorroBinding binding;
    private final AhorroRepository ahorroRepository = new AhorroRepository();
    private AhorroAdapter ahorroAdapter;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault());

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAhorroBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnPerfil.setOnClickListener(v -> navigateSafely(v, R.id.usuario));
        binding.btnAyuda.setOnClickListener(v -> mostrarAyuda());
        binding.btnGuardar.setOnClickListener(v -> guardarAhorro());
        binding.btnEliminar.setOnClickListener(v -> eliminarUltimoAhorro());
        binding.btnLimpiar.setOnClickListener(v -> limpiarCampos());

        View.OnClickListener bottomNavListener = v -> {
            int viewId = v.getId();
            if (viewId == R.id.navGastos) {
                navigateSafely(v, R.id.navigation_gastos);
            } else if (viewId == R.id.navIngresos) {
                navigateSafely(v, R.id.navigation_ingresos);
            } else if (viewId == R.id.navAhorro) {
                navigateSafely(v, R.id.navigation_ahorro);
            } else if (viewId == R.id.navGraficas) {
                navigateSafely(v, R.id.navigation_graficas);
            }
        };

        binding.navGastos.setOnClickListener(bottomNavListener);
        binding.navIngresos.setOnClickListener(bottomNavListener);
        binding.navAhorro.setOnClickListener(bottomNavListener);
        binding.navGraficas.setOnClickListener(bottomNavListener);

        configurarLista();
        cargarAhorros();
    }

    private void configurarLista() {
        ahorroAdapter = new AhorroAdapter();
        binding.listaAhorros.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listaAhorros.setAdapter(ahorroAdapter);
    }

    private void cargarAhorros() {
        ahorroRepository.obtenerAhorros(new Callback<List<AhorroDto>>() {
            @Override
            public void onResponse(Call<List<AhorroDto>> call, Response<List<AhorroDto>> response) {
                if (!isAdded()) {
                    return;
                }
                if (response.isSuccessful()) {
                    List<AhorroDto> body = response.body();
                    List<AhorroItem> items = new ArrayList<>();
                    if (body != null) {
                        for (AhorroDto dto : body) {
                            AhorroItem item = convertir(dto);
                            if (item != null) {
                                items.add(item);
                            }
                        }
                    }
                    ahorroAdapter.update(items);
                    binding.tvAhorrosVacio.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    mostrarMensajeError(null);
                }
            }

            @Override
            public void onFailure(Call<List<AhorroDto>> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                mostrarMensajeError(null);
            }
        });
    }

    private void guardarAhorro() {
        limpiarErrores();
        String montoTexto = obtenerTexto(binding.etMontoAhorro);
        String periodo = obtenerTexto(binding.etPeriodoAhorro);
        String ingresoIdTexto = obtenerTexto(binding.etIngresoId);

        boolean hayError = false;

        if (TextUtils.isEmpty(montoTexto)) {
            binding.tilMontoAhorro.setError(getString(R.string.error_monto_ahorro_obligatorio));
            hayError = true;
        }

        if (TextUtils.isEmpty(periodo)) {
            binding.tilPeriodoAhorro.setError(getString(R.string.error_periodo_ahorro_obligatorio));
            hayError = true;
        }

        BigDecimal monto = parseMonto(montoTexto);
        if (monto == null) {
            binding.tilMontoAhorro.setError(getString(R.string.error_monto_ahorro_obligatorio));
            hayError = true;
        }

        Integer ingresoId = null;
        if (!TextUtils.isEmpty(ingresoIdTexto)) {
            try {
                ingresoId = Integer.parseInt(ingresoIdTexto);
            } catch (NumberFormatException e) {
                binding.tilIngresoId.setError(getString(R.string.error_ingreso_id));
                hayError = true;
            }
        }

        if (hayError || monto == null) {
            return;
        }

        setButtonsEnabled(false);
        AhorroDto dto = AhorroDto.builder()
                .montoAhorro(monto)
                .periodoTAhorro(periodo)
                .idIngresos(ingresoId)
                .build();

        ahorroRepository.crearAhorro(dto, new Callback<AhorroDto>() {
            @Override
            public void onResponse(Call<AhorroDto> call, Response<AhorroDto> response) {
                if (!isAdded()) {
                    return;
                }
                setButtonsEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), R.string.mensaje_ahorro_guardado, Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                    cargarAhorros();
                } else {
                    mostrarMensajeError(null);
                }
            }

            @Override
            public void onFailure(Call<AhorroDto> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                setButtonsEnabled(true);
                mostrarMensajeError(null);
            }
        });
    }

    private void eliminarUltimoAhorro() {
        AhorroItem ultimo = ahorroAdapter.getLast();
        if (ultimo == null || ultimo.getIdAhorro() == null) {
            Toast.makeText(requireContext(), R.string.error_sin_ahorros, Toast.LENGTH_SHORT).show();
            return;
        }
        setButtonsEnabled(false);
        ahorroRepository.eliminarAhorro(ultimo.getIdAhorro(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) {
                    return;
                }
                setButtonsEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), R.string.mensaje_ahorro_eliminado, Toast.LENGTH_SHORT).show();
                    cargarAhorros();
                } else {
                    mostrarMensajeError(null);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                setButtonsEnabled(true);
                mostrarMensajeError(null);
            }
        });
    }

    private void limpiarCampos() {
        binding.etMontoAhorro.setText("");
        binding.etPeriodoAhorro.setText("");
        binding.etIngresoId.setText("");
    }

    private void limpiarErrores() {
        binding.tilMontoAhorro.setError(null);
        binding.tilPeriodoAhorro.setError(null);
        binding.tilIngresoId.setError(null);
    }

    private void setButtonsEnabled(boolean enabled) {
        binding.btnGuardar.setEnabled(enabled);
        binding.btnEliminar.setEnabled(enabled);
        binding.btnLimpiar.setEnabled(enabled);
    }

    private String obtenerTexto(com.google.android.material.textfield.TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private AhorroItem convertir(AhorroDto dto) {
        if (dto == null) {
            return null;
        }
        String monto = dto.getMontoAhorro() != null ? dto.getMontoAhorro().stripTrailingZeros().toPlainString() : "0";
        String periodo = dto.getPeriodoTAhorro() != null ? dto.getPeriodoTAhorro() : getString(R.string.label_periodo_sin_definir);
        String fecha = formatearFecha(dto.getFechaAhorro());
        return new AhorroItem(dto.getIdAhorro(), monto, periodo, fecha, dto.getIdIngresos());
    }

    private String formatearFecha(LocalDate fecha) {
        if (fecha == null) {
            return getString(R.string.label_fecha_desconocida);
        }
        try {
            return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()));
        } catch (Exception ex) {
            try {
                return LocalDate.parse(fecha.toString(), dateFormatter).format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()));
            } catch (DateTimeParseException ignored) {
                return fecha.toString();
            }
        }
    }

    private BigDecimal parseMonto(String valor) {
        if (TextUtils.isEmpty(valor)) {
            return null;
        }
        try {
            return new BigDecimal(valor.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void navigateSafely(View view, int destinationId) {
        NavController navController = Navigation.findNavController(view);
        NavDestination currentDestination = navController.getCurrentDestination();
        if (currentDestination == null || currentDestination.getId() != destinationId) {
            navController.navigate(destinationId);
        }
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_ahorro)
                .setMessage(R.string.mensaje_ayuda_ahorro)
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
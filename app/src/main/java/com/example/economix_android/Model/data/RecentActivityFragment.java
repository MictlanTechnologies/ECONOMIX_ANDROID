package com.example.economix_android.Model.data;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.economix_android.R;
import com.example.economix_android.auth.SessionManager;
import com.example.economix_android.databinding.FragmentRecentActivityBinding;
import com.example.economix_android.network.dto.GastoDto;
import com.example.economix_android.network.dto.IngresoDto;
import com.example.economix_android.network.repository.GastoRepository;
import com.example.economix_android.network.repository.IngresoRepository;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecentActivityFragment extends Fragment {

    private FragmentRecentActivityBinding binding;
    private final IngresoRepository ingresoRepository = new IngresoRepository();
    private final GastoRepository gastoRepository = new GastoRepository();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecentActivityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnVolver.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        cargarActividadReciente();
    }

    private void cargarActividadReciente() {
        Integer userId = SessionManager.getUserId(requireContext());
        if (userId == null) {
            binding.tvIngresoDetalle.setText(getString(R.string.label_no_recent_activity));
            binding.tvGastoDetalle.setText(getString(R.string.label_no_recent_activity));
            return;
        }

        ingresoRepository.obtenerIngresos(new Callback<List<IngresoDto>>() {
            @Override
            public void onResponse(Call<List<IngresoDto>> call, Response<List<IngresoDto>> response) {
                if (!isAdded()) {
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    IngresoDto ultimoIngreso = obtenerUltimoIngreso(userId, response.body());
                    binding.tvIngresoDetalle.setText(formatearIngreso(ultimoIngreso));
                } else {
                    binding.tvIngresoDetalle.setText(getString(R.string.label_no_recent_activity));
                }
            }

            @Override
            public void onFailure(Call<List<IngresoDto>> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                binding.tvIngresoDetalle.setText(getString(R.string.label_no_recent_activity));
            }
        });

        gastoRepository.obtenerGastos(new Callback<List<GastoDto>>() {
            @Override
            public void onResponse(Call<List<GastoDto>> call, Response<List<GastoDto>> response) {
                if (!isAdded()) {
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    GastoDto ultimoGasto = obtenerUltimoGasto(userId, response.body());
                    binding.tvGastoDetalle.setText(formatearGasto(ultimoGasto));
                } else {
                    binding.tvGastoDetalle.setText(getString(R.string.label_no_recent_activity));
                }
            }

            @Override
            public void onFailure(Call<List<GastoDto>> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                binding.tvGastoDetalle.setText(getString(R.string.label_no_recent_activity));
            }
        });
    }

    private IngresoDto obtenerUltimoIngreso(Integer userId, List<IngresoDto> ingresos) {
        IngresoDto ultimo = null;
        for (IngresoDto ingreso : ingresos) {
            if (!userId.equals(ingreso.getIdUsuario())) {
                continue;
            }
            if (ultimo == null || compararFechas(ingreso.getFechaIngresos(), ultimo.getFechaIngresos()) > 0) {
                ultimo = ingreso;
            }
        }
        return ultimo;
    }

    private GastoDto obtenerUltimoGasto(Integer userId, List<GastoDto> gastos) {
        GastoDto ultimo = null;
        for (GastoDto gasto : gastos) {
            if (!userId.equals(gasto.getIdUsuario())) {
                continue;
            }
            if (ultimo == null || compararFechas(gasto.getFechaGastos(), ultimo.getFechaGastos()) > 0) {
                ultimo = gasto;
            }
        }
        return ultimo;
    }

    private int compararFechas(LocalDate f1, LocalDate f2) {
        if (f1 == null && f2 == null) return 0;
        if (f1 == null) return -1;
        if (f2 == null) return 1;
        return f1.compareTo(f2);
    }

    private String formatearIngreso(IngresoDto ingreso) {
        if (ingreso == null) {
            return getString(R.string.label_no_recent_activity);
        }
        String monto = formatearMoneda(ingreso.getMontoIngreso());
        String fecha = ingreso.getFechaIngresos() != null ? ingreso.getFechaIngresos().toString() : getString(R.string.label_fecha_desconocida);
        return getString(R.string.label_recent_ingreso_format, monto, fecha);
    }

    private String formatearGasto(GastoDto gasto) {
        if (gasto == null) {
            return getString(R.string.label_no_recent_activity);
        }
        String monto = formatearMoneda(gasto.getMontoGasto());
        String fecha = gasto.getFechaGastos() != null ? gasto.getFechaGastos().toString() : getString(R.string.label_fecha_desconocida);
        return getString(R.string.label_recent_gasto_format, monto, fecha);
    }

    private String formatearMoneda(BigDecimal monto) {
        BigDecimal seguro = monto != null ? monto : BigDecimal.ZERO;
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
        return format.format(seguro);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

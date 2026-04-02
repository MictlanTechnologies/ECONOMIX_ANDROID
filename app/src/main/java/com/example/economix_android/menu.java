package com.example.economix_android;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.economix_android.auth.SessionManager;
import com.example.economix_android.network.dto.GastoDto;
import com.example.economix_android.network.dto.IngresoDto;
import com.example.economix_android.network.repository.GastoRepository;
import com.example.economix_android.network.repository.IngresoRepository;
import com.example.economix_android.util.ProfileImageUtils;
import com.example.economix_android.util.UsuarioAnimationNavigator;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class menu extends Fragment implements View.OnClickListener {

    private final IngresoRepository ingresoRepository = new IngresoRepository();
    private final GastoRepository gastoRepository = new GastoRepository();
    private TextView tvRecentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View tileGastos = view.findViewById(R.id.tileGastos);
        View tileIngresos = view.findViewById(R.id.tileIngresos);
        View tileAhorro = view.findViewById(R.id.tileAhorro);
        View tileGraficas = view.findViewById(R.id.tileGraficas);
        View recentActivityCard = view.findViewById(R.id.recentActivityCard);
        tvRecentActivity = view.findViewById(R.id.tvRecentActivity);
        ImageView perfilButton = view.findViewById(R.id.btnPerfil);
        TextView saludoUsuario = view.findViewById(R.id.txtHolaUsuario);

        ProfileImageUtils.applyProfileImage(requireContext(), perfilButton);

        String nombreVisible = SessionManager.getDisplayName(requireContext());
        if (TextUtils.isEmpty(nombreVisible)) {
            nombreVisible = SessionManager.getPerfil(requireContext());
        }
        if (TextUtils.isEmpty(nombreVisible)) {
            nombreVisible = getString(R.string.label_usuario_generico);
        }
        saludoUsuario.setText(getString(R.string.label_hola_usuario, nombreVisible));

        tileGastos.setOnClickListener(this);
        tileIngresos.setOnClickListener(this);
        tileAhorro.setOnClickListener(this);
        tileGraficas.setOnClickListener(this);
        recentActivityCard.setOnClickListener(this);
        perfilButton.setOnClickListener(this);

        cargarActividadRecienteWidget();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarActividadRecienteWidget();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.tileGastos) {
            Navigation.findNavController(v).navigate(R.id.action_menu_to_navigation_gastos);
        } else if (viewId == R.id.tileIngresos) {
            Navigation.findNavController(v).navigate(R.id.action_menu_to_navigation_ingresos);
        } else if (viewId == R.id.tileAhorro) {
            Navigation.findNavController(v).navigate(R.id.action_menu_to_navigation_ahorro);
        } else if (viewId == R.id.tileGraficas) {
            Navigation.findNavController(v).navigate(R.id.action_menu_to_navigation_graficas);
        } else if (viewId == R.id.btnPerfil) {
            UsuarioAnimationNavigator.playAndNavigate(v, R.id.action_menu_to_usuario);
        } else if (viewId == R.id.recentActivityCard) {
            UsuarioAnimationNavigator.playAndNavigate(v, R.id.action_menu_to_recentActivity, R.raw.act_reci, 6500f, 8000f);
        }
    }

    private void cargarActividadRecienteWidget() {
        if (!isAdded() || tvRecentActivity == null) {
            return;
        }
        Integer userId = SessionManager.getUserId(requireContext());
        if (userId == null) {
            tvRecentActivity.setText(getString(R.string.label_no_recent_activity));
            return;
        }

        AtomicReference<MovimientoReciente> ultimoMovimiento = new AtomicReference<>(null);
        AtomicInteger pendientes = new AtomicInteger(2);

        ingresoRepository.obtenerIngresos(new Callback<List<IngresoDto>>() {
            @Override
            public void onResponse(Call<List<IngresoDto>> call, Response<List<IngresoDto>> response) {
                if (!isAdded()) {
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    IngresoDto ingreso = obtenerUltimoIngresoReciente(userId, response.body());
                    actualizarMovimientoReciente(ultimoMovimiento, convertirIngreso(ingreso));
                }
                finalizarCarga(ultimoMovimiento, pendientes);
            }

            @Override
            public void onFailure(Call<List<IngresoDto>> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                finalizarCarga(ultimoMovimiento, pendientes);
            }
        });

        gastoRepository.obtenerGastos(new Callback<List<GastoDto>>() {
            @Override
            public void onResponse(Call<List<GastoDto>> call, Response<List<GastoDto>> response) {
                if (!isAdded()) {
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    GastoDto gasto = obtenerUltimoGastoReciente(userId, response.body());
                    actualizarMovimientoReciente(ultimoMovimiento, convertirGasto(gasto));
                }
                finalizarCarga(ultimoMovimiento, pendientes);
            }

            @Override
            public void onFailure(Call<List<GastoDto>> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                finalizarCarga(ultimoMovimiento, pendientes);
            }
        });
    }

    private void finalizarCarga(@NonNull AtomicReference<MovimientoReciente> ultimoMovimiento,
                                @NonNull AtomicInteger pendientes) {
        if (pendientes.decrementAndGet() > 0 || !isAdded() || tvRecentActivity == null) {
            return;
        }
        MovimientoReciente movimiento = ultimoMovimiento.get();
        if (movimiento == null) {
            tvRecentActivity.setText(getString(R.string.label_no_recent_activity));
            return;
        }
        String monto = NumberFormat.getCurrencyInstance(new Locale("es", "MX"))
                .format(movimiento.monto != null ? movimiento.monto : BigDecimal.ZERO);
        String fecha = movimiento.fecha != null
                ? movimiento.fecha.toString()
                : getString(R.string.label_fecha_desconocida);

        tvRecentActivity.setText(movimiento.esIngreso
                ? getString(R.string.label_recent_widget_ingreso, monto, fecha)
                : getString(R.string.label_recent_widget_gasto, monto, fecha));
    }

    private IngresoDto obtenerUltimoIngresoReciente(@NonNull Integer userId, @NonNull List<IngresoDto> ingresos) {
        IngresoDto ultimo = null;
        LocalDate limite = LocalDate.now().minusDays(2);
        for (IngresoDto ingreso : ingresos) {
            if (!userId.equals(ingreso.getIdUsuario()) || ingreso.getFechaIngresos() == null
                    || ingreso.getFechaIngresos().isBefore(limite)) {
                continue;
            }
            if (ultimo == null || ingreso.getFechaIngresos().isAfter(ultimo.getFechaIngresos())) {
                ultimo = ingreso;
            }
        }
        return ultimo;
    }

    private GastoDto obtenerUltimoGastoReciente(@NonNull Integer userId, @NonNull List<GastoDto> gastos) {
        GastoDto ultimo = null;
        LocalDate limite = LocalDate.now().minusDays(2);
        for (GastoDto gasto : gastos) {
            if (!userId.equals(gasto.getIdUsuario()) || gasto.getFechaGastos() == null
                    || gasto.getFechaGastos().isBefore(limite)) {
                continue;
            }
            if (ultimo == null || gasto.getFechaGastos().isAfter(ultimo.getFechaGastos())) {
                ultimo = gasto;
            }
        }
        return ultimo;
    }

    private void actualizarMovimientoReciente(@NonNull AtomicReference<MovimientoReciente> actual,
                                              @Nullable MovimientoReciente candidato) {
        if (candidato == null) {
            return;
        }
        MovimientoReciente previo = actual.get();
        if (previo == null || (candidato.fecha != null && previo.fecha != null && candidato.fecha.isAfter(previo.fecha))
                || (candidato.fecha != null && previo.fecha == null)) {
            actual.set(candidato);
        }
    }

    @Nullable
    private MovimientoReciente convertirIngreso(@Nullable IngresoDto ingreso) {
        if (ingreso == null) {
            return null;
        }
        return new MovimientoReciente(true, ingreso.getMontoIngreso(), ingreso.getFechaIngresos());
    }

    @Nullable
    private MovimientoReciente convertirGasto(@Nullable GastoDto gasto) {
        if (gasto == null) {
            return null;
        }
        return new MovimientoReciente(false, gasto.getMontoGasto(), gasto.getFechaGastos());
    }

    private static final class MovimientoReciente {
        private final boolean esIngreso;
        private final BigDecimal monto;
        private final LocalDate fecha;

        private MovimientoReciente(boolean esIngreso, @Nullable BigDecimal monto, @Nullable LocalDate fecha) {
            this.esIngreso = esIngreso;
            this.monto = monto;
            this.fecha = fecha;
        }
    }
}

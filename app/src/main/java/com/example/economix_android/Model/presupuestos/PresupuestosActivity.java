package com.example.economix_android.Model.presupuestos;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economix_android.R;
import com.example.economix_android.network.dto.CategoriaPresupuestoDto;
import com.example.economix_android.network.dto.IngresoDisponibleDto;
import com.example.economix_android.network.dto.PresupuestoResumenDto;
import com.example.economix_android.network.repository.PresupuestoRepository;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresupuestosActivity extends AppCompatActivity {

    private final PresupuestoRepository repository = new PresupuestoRepository();
    private final List<IngresoDisponibleDto> ingresos = new ArrayList<>();
    private PresupuestosListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presupuestos);

        findViewById(R.id.btnCerrarPresupuestos).setOnClickListener(v -> finish());
        RecyclerView rv = findViewById(R.id.rvPresupuestos);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PresupuestosListAdapter(this::openBottomSheet);
        rv.setAdapter(adapter);

        SwitchMaterial switchAlertas = findViewById(R.id.switchAlertas);
        SharedPreferences prefs = getSharedPreferences("presupuestos_prefs", MODE_PRIVATE);
        switchAlertas.setChecked(prefs.getBoolean("alertas_presupuesto", true));
        switchAlertas.setOnCheckedChangeListener((buttonView, isChecked) -> prefs.edit().putBoolean("alertas_presupuesto", isChecked).apply());

        cargarResumen();
        cargarIngresosDisponibles();
    }

    private void cargarResumen() {
        Calendar c = Calendar.getInstance();
        repository.obtenerResumen(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR), new Callback<List<PresupuestoResumenDto>>() {
            @Override
            public void onResponse(Call<List<PresupuestoResumenDto>> call, Response<List<PresupuestoResumenDto>> response) {
                adapter.submit(response.body());
            }

            @Override
            public void onFailure(Call<List<PresupuestoResumenDto>> call, Throwable t) {
                Toast.makeText(PresupuestosActivity.this, R.string.mensaje_error_servidor, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarIngresosDisponibles() {
        Calendar c = Calendar.getInstance();
        repository.obtenerIngresosDisponibles(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR), new Callback<List<IngresoDisponibleDto>>() {
            @Override
            public void onResponse(Call<List<IngresoDisponibleDto>> call, Response<List<IngresoDisponibleDto>> response) {
                ingresos.clear();
                if (response.body() != null) ingresos.addAll(response.body());
            }

            @Override
            public void onFailure(Call<List<IngresoDisponibleDto>> call, Throwable t) { }
        });
    }

    private void openBottomSheet(PresupuestoResumenDto item) {
        BottomSheetEditPresupuesto sheet = new BottomSheetEditPresupuesto(item, new BottomSheetEditPresupuesto.Callback() {
            @Override
            public void onColorChanged(PresupuestoResumenDto item, String colorHex) {
                repository.editarCategoria(item.getCategoriaId(), CategoriaPresupuestoDto.builder()
                        .id(item.getCategoriaId())
                        .nombre(item.getNombre())
                        .iconKey(item.getIconKey())
                        .colorHex(colorHex)
                        .build(), new Callback<CategoriaPresupuestoDto>() {
                    @Override
                    public void onResponse(Call<CategoriaPresupuestoDto> call, Response<CategoriaPresupuestoDto> response) {
                        cargarResumen();
                    }
                    @Override
                    public void onFailure(Call<CategoriaPresupuestoDto> call, Throwable t) { }
                });
            }

            @Override
            public void onDelete(PresupuestoResumenDto item) {
                repository.eliminarCategoria(item.getCategoriaId(), new Callback<Void>() {
                    @Override public void onResponse(Call<Void> call, Response<Void> response) { cargarResumen(); }
                    @Override public void onFailure(Call<Void> call, Throwable t) { }
                });
            }

            @Override
            public void onAddMoney(PresupuestoResumenDto item) {
                new DialogAddAsignacion(item.getCategoriaId(), ingresos, dto -> repository.asignar(dto, new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            cargarResumen();
                        } else {
                            mostrarErrorCentrado(getString(R.string.error_monto_asignacion));
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        mostrarErrorCentrado(getString(R.string.mensaje_error_servidor));
                    }
                })).show(getSupportFragmentManager(), "asignacion");
            }
        });
        sheet.show(getSupportFragmentManager(), "edit-presupuesto");
    }

    public void mostrarErrorCentrado(String message) {
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.ThemeOverlay_Material3_MaterialAlertDialog)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create();
        dialog.show();
    }
}

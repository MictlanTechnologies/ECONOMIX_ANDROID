package com.example.economix_android.Model.presupuestos;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economix_android.R;
import com.example.economix_android.network.dto.CategoriaPresupuestoDto;
import com.example.economix_android.network.repository.PresupuestoRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ElegirCategoriasActivity extends AppCompatActivity {

    private final PresupuestoRepository repository = new PresupuestoRepository();
    private CategoriasGridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elegir_categorias);

        RecyclerView rv = findViewById(R.id.rvCategorias);
        int cols = getResources().getConfiguration().screenWidthDp >= 600 ? 3 : 2;
        rv.setLayoutManager(new GridLayoutManager(this, cols));
        adapter = new CategoriasGridAdapter(new CategoriasGridAdapter.OnClick() {
            @Override public void onCategoria(CategoriaPresupuestoDto dto) { }
            @Override public void onAdd() { showAddDialog(); }
        });
        rv.setAdapter(adapter);

        findViewById(R.id.btnCerrarCategorias).setOnClickListener(v -> finish());
        findViewById(R.id.btnGuardarCategorias).setOnClickListener(v -> finish());
        cargarCategorias();
    }

    private void cargarCategorias() {
        repository.obtenerCategorias(new Callback<List<CategoriaPresupuestoDto>>() {
            @Override
            public void onResponse(Call<List<CategoriaPresupuestoDto>> call, Response<List<CategoriaPresupuestoDto>> response) {
                adapter.submit(response.body());
            }
            @Override
            public void onFailure(Call<List<CategoriaPresupuestoDto>> call, Throwable t) {
                Toast.makeText(ElegirCategoriasActivity.this, R.string.mensaje_error_servidor, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddDialog() {
        EditText input = new EditText(this);
        input.setHint("Nombre de categoría");
        new AlertDialog.Builder(this, R.style.ThemeOverlay_Material3_MaterialAlertDialog)
                .setTitle("Añadir categoría")
                .setView(input)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = input.getText() != null ? input.getText().toString().trim() : "";
                    if (nombre.isEmpty()) return;
                    repository.crearCategoria(CategoriaPresupuestoDto.builder()
                            .nombre(nombre)
                            .colorHex("#2FB6B0")
                            .iconKey("food")
                            .build(), new Callback<CategoriaPresupuestoDto>() {
                        @Override public void onResponse(Call<CategoriaPresupuestoDto> call, Response<CategoriaPresupuestoDto> response) { cargarCategorias(); }
                        @Override public void onFailure(Call<CategoriaPresupuestoDto> call, Throwable t) { }
                    });
                })
                .show();
    }
}

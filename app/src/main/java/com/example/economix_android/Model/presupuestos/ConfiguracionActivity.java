package com.example.economix_android.Model.presupuestos;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.economix_android.R;

public class ConfiguracionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        findViewById(R.id.btnCerrarConfig).setOnClickListener(v -> finish());
        findViewById(R.id.itemPresupuestos).setOnClickListener(v -> startActivity(new Intent(this, PresupuestosActivity.class)));
        findViewById(R.id.itemCategorias).setOnClickListener(v -> startActivity(new Intent(this, ElegirCategoriasActivity.class)));
    }
}

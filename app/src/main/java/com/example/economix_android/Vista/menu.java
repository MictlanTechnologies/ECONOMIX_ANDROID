package com.example.economix_android.Vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.economix_android.R;

public class menu extends AppCompatActivity {

    private View tileGastos;
    private View tileIngresos;
    private View tileAhorro;
    private View tileGraficas;
    private View btnAyuda;
    private View btnPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupListeners();
    }

    private void initViews() {
        tileGastos = findViewById(R.id.tileGastos);
        tileIngresos = findViewById(R.id.tileIngresos);
        tileAhorro = findViewById(R.id.tileAhorro);
        tileGraficas = findViewById(R.id.tileGraficas);
        btnAyuda = findViewById(R.id.btnAyuda);
        btnPerfil = findViewById(R.id.btnPerfil);
    }

    private void setupListeners() {
        View.OnClickListener openSectionListener = view -> {
            if (view.getId() == R.id.tileIngresos) {
                openSection(NavBar.DESTINATION_INGRESOS);
            } else if (view.getId() == R.id.tileAhorro) {
                openSection(NavBar.DESTINATION_AHORRO);
            } else if (view.getId() == R.id.tileGraficas) {
                openSection(NavBar.DESTINATION_GRAFICAS);
            } else {
                openSection(NavBar.DESTINATION_GASTOS);
            }
        };

        tileGastos.setOnClickListener(openSectionListener);
        tileIngresos.setOnClickListener(openSectionListener);
        tileAhorro.setOnClickListener(openSectionListener);
        tileGraficas.setOnClickListener(openSectionListener);

        btnAyuda.setOnClickListener(v -> openSection(NavBar.DESTINATION_GRAFICAS));
        btnPerfil.setOnClickListener(v -> openSection(NavBar.DESTINATION_USUARIO));
    }

    private void openSection(int destinationId) {
        Intent intent = NavBar.createIntent(menu.this, destinationId);
        startActivity(intent);
    }
}
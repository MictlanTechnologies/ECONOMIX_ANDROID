package com.example.economix_android.Inicio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.economix_android.R;
import com.example.economix_android.Vista.menu;

public class Inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.inicio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button iniciarSesion = findViewById(R.id.inicioSesion);
        Button registrarse = findViewById(R.id.registrarse);

        View.OnClickListener openMenuListener = view -> {
            Intent intent = new Intent(Inicio.this, menu.class);
            startActivity(intent);
        };

        iniciarSesion.setOnClickListener(openMenuListener);
        registrarse.setOnClickListener(openMenuListener);
    }
}
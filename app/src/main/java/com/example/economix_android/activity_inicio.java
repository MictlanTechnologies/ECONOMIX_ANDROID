package com.example.economix_android;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class activity_inicio extends AppCompatActivity {

    public static final String EXTRA_MOSTRAR_LOGIN = "com.example.economix_android.EXTRA_MOSTRAR_LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mostrarLoginSiEsNecesario(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mostrarLoginSiEsNecesario(intent);
    }

    private void mostrarLoginSiEsNecesario(Intent intent) {
        if (intent == null || !intent.getBooleanExtra(EXTRA_MOSTRAR_LOGIN, false)) {
            return;
        }
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_inicio);
        if (navHostFragment == null) {
            return;
        }
        NavController navController = navHostFragment.getNavController();
        if (navController.getCurrentDestination() != null
                && navController.getCurrentDestination().getId() == R.id.inicio_sesionFragment) {
            return;
        }
        navController.navigate(R.id.inicio_sesionFragment);
    }
}
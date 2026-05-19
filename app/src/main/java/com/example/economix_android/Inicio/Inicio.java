package com.example.economix_android.Inicio;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.economix_android.R;
import com.example.economix_android.Vista.menu;
import com.example.economix_android.auth.SessionManager;
import com.example.economix_android.network.auth.dto.RefreshRequest;
import com.example.economix_android.network.auth.dto.RefreshResponse;
import com.example.economix_android.network.repository.auth.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Inicio extends AppCompatActivity {

    public static final String EXTRA_MOSTRAR_LOGIN = "com.example.economix_android.EXTRA_MOSTRAR_LOGIN";

    private SessionManager sessionManager;
    private AuthRepository authRepository;

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

        sessionManager = new SessionManager(this);
        authRepository = new AuthRepository(this);

        boolean forzarLogin = getIntent() != null && getIntent().getBooleanExtra(EXTRA_MOSTRAR_LOGIN, false);
        if (forzarLogin) {
            irAInicioSesion();
            return;
        }

        String refreshToken = sessionManager.getRefreshToken();
        if (TextUtils.isEmpty(refreshToken)) {
            return;
        }

        authRepository.refresh(new RefreshRequest(refreshToken), new Callback<>() {
            @Override
            public void onResponse(Call<RefreshResponse> call, Response<RefreshResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String refreshed = response.body().getRefreshToken() != null
                            ? response.body().getRefreshToken() : refreshToken;
                    sessionManager.updateTokens(response.body().getAccessToken(), refreshed);
                    Intent menuIntent = new Intent(Inicio.this, menu.class);
                    menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(menuIntent);
                    finish();
                } else {
                    sessionManager.clearSession();
                }
            }

            @Override
            public void onFailure(Call<RefreshResponse> call, Throwable t) {
                sessionManager.clearSession();
                irAInicioSesion();
            }
        });
    }

    private void irAInicioSesion() {
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

package com.example.economix_android.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.economix_android.Vista.menu;
import com.example.economix_android.auth.SessionManager;
import com.example.economix_android.databinding.ActivityLoginBinding;
import com.example.economix_android.network.auth.dto.LoginRequest;
import com.example.economix_android.network.auth.dto.LoginResponse;
import com.example.economix_android.network.repository.auth.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthRepository authRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository(this);
        sessionManager = new SessionManager(this);

        binding.btnSignIn.setOnClickListener(v -> submitLogin());
    }

    private void submitLogin() {
        String username = String.valueOf(binding.etUsername.getText()).trim();
        String password = String.valueOf(binding.etPassword.getText());

        binding.tilUsername.setError(null);
        binding.tilPassword.setError(null);

        if (TextUtils.isEmpty(username)) {
            binding.tilUsername.setError("Ingresa usuario o email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError("Ingresa contraseña");
            return;
        }

        binding.btnSignIn.setEnabled(false);

        authRepository.login(new LoginRequest(username, password), new Callback<>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                binding.btnSignIn.setEnabled(true);
                if (!response.isSuccessful() || response.body() == null) {
                    if (response.code() == 401) {
                        Toast.makeText(LoginActivity.this, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error de autenticación", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                LoginResponse body = response.body();
                if (body.isRequires2fa()) {
                    Intent intent = new Intent(LoginActivity.this, TwoFactorActivity.class);
                    intent.putExtra(TwoFactorActivity.EXTRA_CHALLENGE_ID, body.getChallengeId());
                    intent.putExtra(TwoFactorActivity.EXTRA_CHALLENGE_EXPIRES_AT, body.getChallengeExpiresAt());
                    startActivity(intent);
                    return;
                }

                sessionManager.saveAuthSession(
                        body.getAccessToken(),
                        body.getRefreshToken(),
                        body.getUserInfo()
                );
                openHome();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                binding.btnSignIn.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openHome() {
        Intent intent = new Intent(this, menu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

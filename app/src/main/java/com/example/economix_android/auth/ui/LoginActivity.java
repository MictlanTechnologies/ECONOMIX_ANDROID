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
import com.example.economix_android.network.dto.LoginRequest;
import com.example.economix_android.network.dto.UsuarioDto;
import com.example.economix_android.network.repository.UsuarioRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private UsuarioRepository usuarioRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        usuarioRepository = new UsuarioRepository();

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

        usuarioRepository.login(new LoginRequest(username, password), new Callback<UsuarioDto>() {
            @Override
            public void onResponse(Call<UsuarioDto> call, Response<UsuarioDto> response) {
                binding.btnSignIn.setEnabled(true);
                if (!response.isSuccessful() || response.body() == null) {
                    if (response.code() == 401) {
                        Toast.makeText(LoginActivity.this, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error de autenticación", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                SessionManager.saveSession(LoginActivity.this, response.body());
                openHome();
            }

            @Override
            public void onFailure(Call<UsuarioDto> call, Throwable t) {
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

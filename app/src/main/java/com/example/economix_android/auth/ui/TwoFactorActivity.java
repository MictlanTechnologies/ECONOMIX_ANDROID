package com.example.economix_android.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.economix_android.Vista.menu;
import com.example.economix_android.auth.SessionManager;
import com.example.economix_android.databinding.ActivityTwoFactorBinding;
import com.example.economix_android.network.auth.dto.Verify2faRequest;
import com.example.economix_android.network.auth.dto.Verify2faResponse;
import com.example.economix_android.network.repository.auth.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TwoFactorActivity extends AppCompatActivity {

    public static final String EXTRA_CHALLENGE_ID = "challengeId";
    public static final String EXTRA_CHALLENGE_EXPIRES_AT = "challengeExpiresAt";

    private ActivityTwoFactorBinding binding;
    private AuthRepository authRepository;
    private SessionManager sessionManager;
    private String challengeId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = ActivityTwoFactorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository(this);
        sessionManager = new SessionManager(this);

        challengeId = getIntent().getStringExtra(EXTRA_CHALLENGE_ID);

        binding.btnVerify.setOnClickListener(v -> verifyOtp());
    }

    private void verifyOtp() {
        String otp = String.valueOf(binding.etOtpCode.getText()).trim();
        binding.tilOtpCode.setError(null);

        if (TextUtils.isEmpty(challengeId)) {
            Toast.makeText(this, "Challenge expirado o inválido", Toast.LENGTH_SHORT).show();
            openLogin();
            return;
        }

        if (otp.length() != 6) {
            binding.tilOtpCode.setError("El código debe tener 6 dígitos");
            return;
        }

        binding.btnVerify.setEnabled(false);
        authRepository.verify2fa(new Verify2faRequest(challengeId, otp), new Callback<>() {
            @Override
            public void onResponse(Call<Verify2faResponse> call, Response<Verify2faResponse> response) {
                binding.btnVerify.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Verify2faResponse body = response.body();
                    sessionManager.saveAuthSession(body.getAccessToken(), body.getRefreshToken(), body.getUserInfo());
                    Intent intent = new Intent(TwoFactorActivity.this, menu.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return;
                }

                if (response.code() == 400) {
                    Toast.makeText(TwoFactorActivity.this, "Código inválido", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 410) {
                    Toast.makeText(TwoFactorActivity.this, "Challenge expirado, vuelve a iniciar sesión", Toast.LENGTH_SHORT).show();
                    openLogin();
                } else if (response.code() == 429) {
                    Toast.makeText(TwoFactorActivity.this, "Demasiados intentos, espera…", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    Toast.makeText(TwoFactorActivity.this, "No autorizado para verificar este challenge", Toast.LENGTH_SHORT).show();
                    openLogin();
                } else {
                    Toast.makeText(TwoFactorActivity.this, "Error verificando código", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Verify2faResponse> call, Throwable t) {
                binding.btnVerify.setEnabled(true);
                Toast.makeText(TwoFactorActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

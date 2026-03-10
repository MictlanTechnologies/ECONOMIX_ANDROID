package com.example.economix_android.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.economix_android.R;
import com.example.economix_android.Vista.menu;
import com.example.economix_android.databinding.FragmentTwoFactorBinding;
import com.example.economix_android.network.auth.dto.Verify2faRequest;
import com.example.economix_android.network.auth.dto.Verify2faResponse;
import com.example.economix_android.network.repository.auth.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TwoFactorFragment extends Fragment {

    private FragmentTwoFactorBinding binding;
    private AuthRepository authRepository;
    private SessionManager sessionManager;
    private String challengeId;
    private String challengeExpiresAt;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTwoFactorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        authRepository = new AuthRepository(requireContext());
        sessionManager = new SessionManager(requireContext());

        if (getArguments() != null) {
            challengeId = getArguments().getString("challengeId");
            challengeExpiresAt = getArguments().getString("challengeExpiresAt");
        }

        binding.btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        binding.btnVerify.setOnClickListener(v -> verificarCodigo());
    }

    private void verificarCodigo() {
        String otp = binding.etOtpCode.getText() != null ? binding.etOtpCode.getText().toString().trim() : "";
        binding.tilOtpCode.setError(null);

        if (TextUtils.isEmpty(challengeId)) {
            Toast.makeText(requireContext(), "Sesión expirada, vuelve a iniciar sesión", Toast.LENGTH_SHORT).show();
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
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
                    Intent intent = new Intent(requireContext(), menu.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                    return;
                }

                if (response.code() == 400) {
                    Toast.makeText(requireContext(), "Código inválido", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 410) {
                    Toast.makeText(requireContext(), "Código expiró, vuelve a iniciar sesión", Toast.LENGTH_SHORT).show();
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                } else if (response.code() == 429) {
                    Toast.makeText(requireContext(), "Demasiados intentos, espera…", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), getString(R.string.mensaje_error_servidor), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Verify2faResponse> call, Throwable t) {
                binding.btnVerify.setEnabled(true);
                Toast.makeText(requireContext(), getString(R.string.mensaje_error_servidor), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        binding = null;
    }
}

package com.example.economix_android.Model.usuario;

import android.graphics.Bitmap;
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

import com.example.economix_android.databinding.FragmentSeguridad2faBinding;
import com.example.economix_android.network.auth.dto.OtpCodeRequest;
import com.example.economix_android.network.auth.dto.TwoFaSetupResponse;
import com.example.economix_android.network.repository.auth.AuthRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Seguridad2faFragment extends Fragment {

    private FragmentSeguridad2faBinding binding;
    private AuthRepository authRepository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSeguridad2faBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        authRepository = new AuthRepository(requireContext());

        binding.btnGenerarQr.setOnClickListener(v -> cargarSetup2fa());
        binding.btnActivar2fa.setOnClickListener(v -> cambiarEstado2fa(true));
        binding.btnDesactivar2fa.setOnClickListener(v -> cambiarEstado2fa(false));
    }

    private void cargarSetup2fa() {
        binding.progress2fa.setVisibility(View.VISIBLE);
        authRepository.setup2fa(new Callback<>() {
            @Override
            public void onResponse(Call<TwoFaSetupResponse> call, Response<TwoFaSetupResponse> response) {
                binding.progress2fa.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null || TextUtils.isEmpty(response.body().getOtpauthUri())) {
                    Toast.makeText(requireContext(), "No se pudo generar la configuración 2FA", Toast.LENGTH_SHORT).show();
                    return;
                }

                binding.tvSecretMasked.setText(response.body().getSecretMasked());
                renderQr(response.body().getOtpauthUri());
            }

            @Override
            public void onFailure(Call<TwoFaSetupResponse> call, Throwable t) {
                binding.progress2fa.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error de red al generar QR", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderQr(String content) {
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 640, 640);
            binding.imgQr2fa.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Toast.makeText(requireContext(), "No se pudo renderizar el QR", Toast.LENGTH_SHORT).show();
        }
    }

    private void cambiarEstado2fa(boolean activar) {
        String otp = binding.etOtp2fa.getText() != null ? binding.etOtp2fa.getText().toString().trim() : "";
        if (otp.length() != 6) {
            binding.tilOtp2fa.setError("Ingresa un OTP de 6 dígitos");
            return;
        }
        binding.tilOtp2fa.setError(null);

        Callback<Void> callback = new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    String msg = activar ? "2FA activado correctamente" : "2FA desactivado correctamente";
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(requireContext(), "No se pudo actualizar 2FA", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Error de red actualizando 2FA", Toast.LENGTH_SHORT).show();
            }
        };

        if (activar) {
            authRepository.enable2fa(new OtpCodeRequest(otp), callback);
        } else {
            authRepository.disable2fa(new OtpCodeRequest(otp), callback);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        binding = null;
    }
}

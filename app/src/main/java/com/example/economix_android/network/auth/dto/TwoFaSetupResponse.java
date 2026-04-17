package com.example.economix_android.network.auth.dto;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class TwoFaSetupResponse {
    @SerializedName(value = "otpauthUri", alternate = {"otpAuthUri", "otpauth_url", "otpAuthUrl", "qrText"})
    private String otpauthUri;

    @SerializedName(value = "secretMasked", alternate = {"maskedSecret", "secret_masked"})
    private String secretMasked;

    @SerializedName(value = "secret", alternate = {"twoFactorSecret", "totpSecret"})
    private String secret;

    @SerializedName(value = "qrImageBase64", alternate = {"qrBase64", "qrCodeBase64", "qrImage"})
    private String qrImageBase64;

    @SerializedName(value = "qrImageDataUrl", alternate = {"qrDataUrl", "qrCodeDataUrl"})
    private String qrImageDataUrl;

    public String getOtpauthUri() {
        return otpauthUri;
    }

    public String getSecretMasked() {
        return secretMasked;
    }

    public String getSecret() {
        return secret;
    }

    public String getQrImageBase64() {
        return qrImageBase64;
    }

    public String getQrImageDataUrl() {
        return qrImageDataUrl;
    }

    @Nullable
    public String getBestQrPayload() {
        if (!isBlank(otpauthUri)) return otpauthUri;
        if (!isBlank(qrImageDataUrl)) return qrImageDataUrl;
        if (!isBlank(qrImageBase64)) return qrImageBase64;
        return null;
    }

    @Nullable
    public String getBestSecretText() {
        if (!isBlank(secretMasked)) return secretMasked;
        if (!isBlank(secret)) return secret;
        return null;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

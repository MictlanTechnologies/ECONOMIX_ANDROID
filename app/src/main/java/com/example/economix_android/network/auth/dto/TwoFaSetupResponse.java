package com.example.economix_android.network.auth.dto;

public class TwoFaSetupResponse {
    private String otpauthUri;
    private String secretMasked;

    public String getOtpauthUri() {
        return otpauthUri;
    }

    public String getSecretMasked() {
        return secretMasked;
    }
}

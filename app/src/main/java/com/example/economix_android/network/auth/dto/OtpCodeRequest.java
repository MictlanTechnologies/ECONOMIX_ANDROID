package com.example.economix_android.network.auth.dto;

public class OtpCodeRequest {
    private String otpCode;

    public OtpCodeRequest(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getOtpCode() {
        return otpCode;
    }
}

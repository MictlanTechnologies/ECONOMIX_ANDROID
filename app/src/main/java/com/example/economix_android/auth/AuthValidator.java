package com.example.economix_android.auth;

import android.content.Context;
import android.text.TextUtils;

import com.example.economix_android.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public final class AuthValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=[\\]{};':\"\\\\|,.<>/?]).{10,}$");

    private AuthValidator() {
    }

    public static boolean validarCorreo(String correo, TextInputLayout til, Context context) {
        if (TextUtils.isEmpty(correo)) {
            til.setError(context.getString(R.string.error_correo_obligatorio));
            return false;
        }
        if (!EMAIL_PATTERN.matcher(correo).matches()) {
            til.setError(context.getString(R.string.error_correo_formato));
            return false;
        }
        til.setError(null);
        return true;
    }

    public static boolean validarPassword(String password, TextInputLayout til, Context context) {
        if (TextUtils.isEmpty(password)) {
            til.setError(context.getString(R.string.error_contrasena_obligatoria));
            return false;
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            til.setError(context.getString(R.string.error_contrasena_requisitos));
            return false;
        }
        til.setError(null);
        return true;
    }
}

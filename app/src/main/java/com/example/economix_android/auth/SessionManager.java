package com.example.economix_android.auth;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.economix_android.network.dto.UsuarioDto;

public final class SessionManager {

    private static final String PREF_NAME = "economix_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PERFIL = "perfil_usuario";

    private SessionManager() {
    }

    public static void saveSession(Context context, UsuarioDto usuario) {
        if (context == null || usuario == null) {
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit()
                .putInt(KEY_USER_ID, usuario.getIdUsuario() != null ? usuario.getIdUsuario() : -1)
                .putString(KEY_PERFIL, usuario.getPerfilUsuario())
                .apply();
    }

    public static Integer getUserId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int id = preferences.getInt(KEY_USER_ID, -1);
        return id > 0 ? id : null;
    }

    public static String getPerfil(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_PERFIL, null);
    }

    public static void clearSession(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
    }
}

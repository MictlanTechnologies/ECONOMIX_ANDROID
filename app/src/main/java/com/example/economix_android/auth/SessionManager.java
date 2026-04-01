package com.example.economix_android.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;

import com.example.economix_android.network.dto.UsuarioDto;

public final class SessionManager {

    private static final String PREF_NAME = "economix_session";
    private static final String PREF_PHOTOS = "economix_profile_photos";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PERFIL = "perfil_usuario";
    private static final String KEY_DISPLAY_NAME = "display_name";
    private static final String KEY_FOTO_URI = "foto_perfil_uri";

    private SessionManager() {
    }

    public static void saveSession(Context context, UsuarioDto usuario) {
        saveSession(context, usuario, null);
    }

    public static void saveSession(Context context, UsuarioDto usuario, String displayName) {
        if (context == null || usuario == null) {
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit()
                .putInt(KEY_USER_ID, usuario.getIdUsuario() != null ? usuario.getIdUsuario() : -1)
                .putString(KEY_PERFIL, usuario.getPerfilUsuario());

        if (!TextUtils.isEmpty(displayName) && !TextUtils.isEmpty(displayName.trim())) {
            editor.putString(KEY_DISPLAY_NAME, displayName.trim());
        }

        editor.apply();
    }

    public static void saveDisplayName(Context context, String displayName) {
        if (context == null || TextUtils.isEmpty(displayName) || TextUtils.isEmpty(displayName.trim())) {
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_DISPLAY_NAME, displayName.trim()).apply();
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

    public static String getDisplayName(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_DISPLAY_NAME, null);
    }

    public static void clearSession(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
    }

    public static void saveProfilePhoto(Context context, Uri uri) {
        if (context == null || uri == null) {
            return;
        }
        Integer userId = getUserId(context);
        if (userId == null) {
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences(PREF_PHOTOS, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_FOTO_URI + "_" + userId, uri.toString()).apply();
    }

    public static String getProfilePhotoUri(Context context) {
        Integer userId = getUserId(context);
        if (userId == null) {
            return null;
        }
        SharedPreferences preferences = context.getSharedPreferences(PREF_PHOTOS, Context.MODE_PRIVATE);
        return preferences.getString(KEY_FOTO_URI + "_" + userId, null);
    }

    public static void clearProfilePhoto(Context context) {
        Integer userId = getUserId(context);
        if (userId == null) {
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences(PREF_PHOTOS, Context.MODE_PRIVATE);
        preferences.edit().remove(KEY_FOTO_URI + "_" + userId).apply();
    }
}

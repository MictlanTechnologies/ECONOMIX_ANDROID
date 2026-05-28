package com.example.economix_android.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.economix_android.network.auth.dto.UserInfo;
import com.example.economix_android.network.dto.UsuarioDto;

public class SessionManager {

    private static final String PREF_NAME = "economix_secure_session";
    private static final String PREF_PHOTOS = "economix_profile_photos";

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_PERFIL = "perfil_usuario";
    private static final String KEY_FOTO_URI = "foto_perfil_uri";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_ACCESS_EXPIRATION = "access_expiration";

    private final Context appContext;

    public SessionManager(Context context) {
        this.appContext = context.getApplicationContext();
    }

    private SharedPreferences securePrefs() {
        try {
            MasterKey masterKey = new MasterKey.Builder(appContext)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            return EncryptedSharedPreferences.create(
                    appContext,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            return appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public void saveAuthSession(String accessToken,
                                String refreshToken,
                                UserInfo userInfo,
                                String accessExpiration) {
        SharedPreferences.Editor editor = securePrefs().edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .putString(KEY_ACCESS_EXPIRATION, accessExpiration);

        if (userInfo != null) {
            if (userInfo.getUserId() != null) {
                editor.putInt(KEY_USER_ID, userInfo.getUserId());
            }
            String visibleName = userInfo.getUsername();
            editor.putString(KEY_USER_NAME, visibleName);
            editor.putString(KEY_PERFIL, visibleName);
        }
        editor.apply();
    }

    public void saveAuthSession(String accessToken,
                                String refreshToken,
                                UserInfo userInfo) {
        saveAuthSession(accessToken, refreshToken, userInfo, null);
    }

    public void updateTokens(String accessToken, String refreshToken) {
        securePrefs().edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .apply();
    }

    public String getAccessToken() {
        return securePrefs().getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return securePrefs().getString(KEY_REFRESH_TOKEN, null);
    }

    public String getAccessExpiration() {
        return securePrefs().getString(KEY_ACCESS_EXPIRATION, null);
    }

    public Integer getUserId() {
        int id = securePrefs().getInt(KEY_USER_ID, -1);
        return id > 0 ? id : null;
    }

    public String getPerfil() {
        return securePrefs().getString(KEY_PERFIL, null);
    }

    public void clearSession() {
        securePrefs().edit().clear().apply();
    }

    public static void saveSession(Context context, UsuarioDto usuario) {
        if (context == null || usuario == null) {
            return;
        }
        new SessionManager(context).securePrefs().edit()
                .putInt(KEY_USER_ID, usuario.getIdUsuario() != null ? usuario.getIdUsuario() : -1)
                .putString(KEY_PERFIL, usuario.getPerfilUsuario())
                .putString(KEY_USER_NAME, usuario.getPerfilUsuario())
                .apply();
    }

    public static Integer getUserId(Context context) {
        return new SessionManager(context).getUserId();
    }

    public static String getPerfil(Context context) {
        return new SessionManager(context).getPerfil();
    }

    public static void clearSession(Context context) {
        new SessionManager(context).clearSession();
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
}

package com.example.economix_android.network.auth;

import androidx.annotation.NonNull;

import com.example.economix_android.auth.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final SessionManager sessionManager;

    public AuthInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        String path = request.url().encodedPath();
        if (path.endsWith("/auth/login") || path.endsWith("/auth/2fa/verify")
                || path.endsWith("/auth/refresh")) {
            return chain.proceed(request);
        }

        String accessToken = sessionManager.getAccessToken();
        if (accessToken == null || accessToken.isEmpty()) {
            return chain.proceed(request);
        }

        Request newRequest = request.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .build();
        return chain.proceed(newRequest);
    }
}

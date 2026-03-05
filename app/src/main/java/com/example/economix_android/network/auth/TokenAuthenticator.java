package com.example.economix_android.network.auth;

import androidx.annotation.NonNull;

import com.example.economix_android.auth.SessionManager;
import com.example.economix_android.network.auth.dto.RefreshRequest;
import com.example.economix_android.network.auth.dto.RefreshResponse;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;

public class TokenAuthenticator implements Authenticator {

    private final SessionManager sessionManager;
    private final AuthApi authApi;
    private final Object refreshLock = new Object();

    public TokenAuthenticator(SessionManager sessionManager, AuthApi authApi) {
        this.sessionManager = sessionManager;
        this.authApi = authApi;
    }

    @Override
    public Request authenticate(Route route, @NonNull Response response) throws IOException {
        if (responseCount(response) >= 2) {
            return null;
        }

        synchronized (refreshLock) {
            String refreshToken = sessionManager.getRefreshToken();
            if (refreshToken == null || refreshToken.isEmpty()) {
                return null;
            }

            Call<RefreshResponse> refreshCall = authApi.refresh(new RefreshRequest(refreshToken));
            retrofit2.Response<RefreshResponse> refreshResponse = refreshCall.execute();
            if (!refreshResponse.isSuccessful() || refreshResponse.body() == null) {
                sessionManager.clearSession();
                return null;
            }

            RefreshResponse body = refreshResponse.body();
            String newAccessToken = body.getAccessToken();
            String newRefreshToken = body.getRefreshToken() != null ? body.getRefreshToken() : refreshToken;
            sessionManager.updateTokens(newAccessToken, newRefreshToken);

            return response.request().newBuilder()
                    .header("Authorization", "Bearer " + newAccessToken)
                    .build();
        }
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}

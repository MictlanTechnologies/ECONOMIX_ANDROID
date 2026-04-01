package com.example.economix_android.network.auth;

import android.content.Context;

import com.example.economix_android.auth.SessionManager;
import com.example.economix_android.network.NetworkConfig;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class AuthServiceFactory {

    private static volatile AuthApi authApi;

    private AuthServiceFactory() {
    }

    public static AuthApi getAuthApi(Context context) {
        if (authApi == null) {
            synchronized (AuthServiceFactory.class) {
                if (authApi == null) {
                    SessionManager sessionManager = new SessionManager(context.getApplicationContext());

                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

                    Retrofit refreshRetrofit = new Retrofit.Builder()
                            .baseUrl(NetworkConfig.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create(new Gson()))
                            .build();
                    AuthApi refreshAuthApi = refreshRetrofit.create(AuthApi.class);

                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .addInterceptor(new AuthInterceptor(sessionManager))
                            .authenticator(new TokenAuthenticator(sessionManager, refreshAuthApi))
                            .addInterceptor(loggingInterceptor)
                            .build();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(NetworkConfig.BASE_URL)
                            .client(okHttpClient)
                            .addConverterFactory(GsonConverterFactory.create(new Gson()))
                            .build();

                    authApi = retrofit.create(AuthApi.class);
                }
            }
        }
        return authApi;
    }
}

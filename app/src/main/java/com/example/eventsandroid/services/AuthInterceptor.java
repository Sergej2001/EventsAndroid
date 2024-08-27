package com.example.eventsandroid.services;

import okhttp3.Interceptor;

import android.content.SharedPreferences;

import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class AuthInterceptor implements Interceptor {
    private SharedPreferences sharedPreferences;

    public AuthInterceptor(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = sharedPreferences.getString("jwt_token", null);

        Request originalRequest = chain.request();
        Request.Builder requestBuilder = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer " + token);

        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
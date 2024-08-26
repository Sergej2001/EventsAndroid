package com.example.eventsandroid.services;

import okhttp3.Interceptor;

import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.eventsandroid.activities.DodajActivity;

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
        if (token.isEmpty()) {
            System.out.println("TOKEN NIJE PRONADJEN");
        }else{
            System.out.println("EVO GA: " + token);
        }
        Request originalRequest = chain.request();
        Request.Builder requestBuilder = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer " + token);

        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
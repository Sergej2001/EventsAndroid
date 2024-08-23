package com.example.eventsandroid.services;

import com.example.eventsandroid.models.AuthRequest;
import com.example.eventsandroid.models.JwtResponse;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Body;

public interface ApiService {
    @POST("api/auth/signin")
    Call<JwtResponse> login(@Body AuthRequest loginRequest);

    @POST("api/auth/signup")
    Call<JwtResponse> register(@Body AuthRequest registerRequest);
}
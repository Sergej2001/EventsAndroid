package com.example.eventsandroid.services;

import com.example.eventsandroid.models.AuthRequest;
import com.example.eventsandroid.models.Event;
import com.example.eventsandroid.models.JwtResponse;
import com.example.eventsandroid.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("api/auth/signin")
    Call<JwtResponse> login(@Body AuthRequest loginRequest);

    @POST("api/auth/signup")
    Call<JwtResponse> register(@Body AuthRequest registerRequest);

    @GET("api/auth/validateToken")
    Call<String> validateToken();

    @GET("events/all")
    Call<List<Event>> getAllEvents();

    @POST("events/create")
    Call<Event> createEvent(@Body Event event);

    @GET("events/{id}")
    Call<Event> getEventById(@Path("id") Long id);

    @PUT("events/{id}")
    Call<Event> updateEvent(@Path("id") Long id, @Body Event eventDetails);

    @DELETE("events/{id}")
    Call<Void> deleteEvent(@Path("id") Long id);

    @GET("users/{username}")
    Call<User> getUserByUsername(@Path("username") String username);
}
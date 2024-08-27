package com.example.eventsandroid.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventsandroid.R;
import com.example.eventsandroid.models.Event;
import com.example.eventsandroid.models.EventAdapter;
import com.example.eventsandroid.services.ApiClient;
import com.example.eventsandroid.services.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private ApiService apiService;
    private ImageView addEventButton;
    private TextView textDodajSliku;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main, findViewById(R.id.content_frame));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList, this);
        recyclerView.setAdapter(eventAdapter);

        apiService = ApiClient.getRetrofitInstance(this).create(ApiService.class);
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);

        addEventButton = findViewById(R.id.btnDodajDogadjaj);
        textDodajSliku = findViewById(R.id.textDodajSliku);

        boolean isLoggedIn = sharedPreferences.contains("jwt_token");

        // Hide or show UI elements based on login status
        if (!isLoggedIn) {
            addEventButton.setVisibility(View.GONE);
            textDodajSliku.setVisibility(View.GONE);
        } else {
            addEventButton.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DodajActivity.class);
                startActivity(intent);
            });
        }

        loadEvents();
    }

    private void loadEvents() {
        Call<List<Event>> call = apiService.getAllEvents();
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventList.clear();
                    eventList.addAll(response.body());
                    eventAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

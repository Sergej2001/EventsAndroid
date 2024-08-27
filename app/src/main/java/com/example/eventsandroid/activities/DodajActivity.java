package com.example.eventsandroid.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventsandroid.R;

import android.app.DatePickerDialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import com.example.eventsandroid.models.Event;
import com.example.eventsandroid.models.User;
import com.example.eventsandroid.services.ApiClient;
import com.example.eventsandroid.services.ApiService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DodajActivity extends AppCompatActivity {

    private EditText editTextEventName, editTextEventDescription, editTextEventLocation, editTextEventDate;
    private SharedPreferences sharedPreferences;
    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj);

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        apiService = ApiClient.getRetrofitInstance(this).create(ApiService.class);

        editTextEventName = findViewById(R.id.editTextEventName);
        editTextEventDescription = findViewById(R.id.editTextEventDescription);
        editTextEventLocation = findViewById(R.id.editTextEventLocation);
        editTextEventDate = findViewById(R.id.editTextEventDate);

        Button btnSaveEvent = findViewById(R.id.btnSaveEvent);
        btnSaveEvent.setOnClickListener(v -> saveEvent());

        editTextEventDate.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    editTextEventDate.setText(formattedDate);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void saveEvent() {
        String eventName = editTextEventName.getText().toString().trim();
        String eventDescription = editTextEventDescription.getText().toString().trim();
        String eventLocation = editTextEventLocation.getText().toString().trim();
        String eventDate = editTextEventDate.getText().toString().trim();

        if (eventName.isEmpty() || eventDescription.isEmpty() || eventLocation.isEmpty() || eventDate.isEmpty()) {
            Toast.makeText(this, "Sva polja moraju biti popunjena", Toast.LENGTH_SHORT).show();
            return;
        }

        LocalDate date = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                date = LocalDate.parse(eventDate, DateTimeFormatter.ofPattern("d/M/yyyy"));
            }
        } catch (Exception e) {
            Toast.makeText(this, "Neispravan datum. Koristite format dd/MM/yyyy", Toast.LENGTH_SHORT).show();
            return;
        }

        String username = sharedPreferences.getString("username", "");

        fetchUserAndCreateEvent(username, eventName, eventDescription, eventLocation, date);
    }

    private void fetchUserAndCreateEvent(String username, String eventName, String eventDescription, String eventLocation, LocalDate date) {
        Call<User> call = apiService.getUserByUsername(username);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    Event newEvent = new Event();
                    newEvent.setName(eventName);
                    newEvent.setDescription(eventDescription);
                    newEvent.setLocation(eventLocation);
                    newEvent.setDate(date);
                    newEvent.setCreatedBy(user);

                    saveEventToBackend(newEvent);
                } else {
                    Toast.makeText(DodajActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(DodajActivity.this, "Greška: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveEventToBackend(Event event) {
        Call<Event> call = apiService.createEvent(event);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DodajActivity.this, "Događaj uspešno sačuvan!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DodajActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(DodajActivity.this, event.getCreatedBy().toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(DodajActivity.this, "Greška prilikom čuvanja događaja", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(DodajActivity.this, "Greška: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
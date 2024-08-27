package com.example.eventsandroid.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventsandroid.R;
import com.example.eventsandroid.models.Event;
import com.example.eventsandroid.services.ApiClient;
import com.example.eventsandroid.services.ApiService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IzmeniActivity extends AppCompatActivity {

    private EditText editTextEventName, editTextEventDescription, editTextEventLocation, editTextEventDate;
    private Button btnSaveEvent;
    private ApiService apiService;
    private Event event;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_izmeni);

        editTextEventName = findViewById(R.id.editTextEventName);
        editTextEventDescription = findViewById(R.id.editTextEventDescription);
        editTextEventLocation = findViewById(R.id.editTextEventLocation);
        editTextEventDate = findViewById(R.id.editTextEventDate);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);

        apiService = ApiClient.getRetrofitInstance(this).create(ApiService.class);

        long eventId = getIntent().getLongExtra("event_id", -1);
        if (eventId != -1) {
            loadEvent(eventId);
        }

        editTextEventDate.setOnClickListener(v -> showDatePickerDialog());

        btnSaveEvent.setOnClickListener(v -> saveEvent());
    }

    private void loadEvent(long eventId) {
        Call<Event> call = apiService.getEventById(eventId);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful() && response.body() != null) {
                    event = response.body();
                    populateFields();
                } else {
                    Toast.makeText(IzmeniActivity.this, "Greška prilikom učitavanja događaja", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(IzmeniActivity.this, "Greška: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateFields() {
        editTextEventName.setText(event.getName());
        editTextEventDescription.setText(event.getDescription());
        editTextEventLocation.setText(event.getLocation());
        editTextEventDate.setText(event.getDate().toString());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
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
                date = LocalDate.parse(eventDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        } catch (Exception e) {
            Toast.makeText(this, "Neispravan datum. Koristite format yyyy-MM-dd", Toast.LENGTH_SHORT).show();
            return;
        }

        event.setName(eventName);
        event.setDescription(eventDescription);
        event.setLocation(eventLocation);
        event.setDate(date);

        Call<Event> call = apiService.updateEvent(event.getId(), event);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(IzmeniActivity.this, "Događaj uspešno izmenjen!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(IzmeniActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(IzmeniActivity.this, "Greška prilikom izmene događaja", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(IzmeniActivity.this, "Greška: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

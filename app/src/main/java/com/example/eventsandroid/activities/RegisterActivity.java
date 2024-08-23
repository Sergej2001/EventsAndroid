package com.example.eventsandroid.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventsandroid.R;
import com.example.eventsandroid.models.AuthRequest;
import com.example.eventsandroid.models.JwtResponse;
import com.example.eventsandroid.services.ApiClient;
import com.example.eventsandroid.services.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegister();
            }
        });
    }

    private void performRegister() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        AuthRequest registerRequest = new AuthRequest(username, password);

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<JwtResponse> call = apiService.register(registerRequest);

        call.enqueue(new Callback<JwtResponse>() {
            @Override
            public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JwtResponse jwtResponse = response.body();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("jwt_token", jwtResponse.getToken());
                    editor.putLong("user_id", jwtResponse.getId());
                    editor.putString("username", jwtResponse.getUsername());
                    if (jwtResponse.getRoles() != null) {
                        String rolesString = String.join(",", jwtResponse.getRoles());
                        editor.putString("roles", rolesString);
                    }
                    editor.apply();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JwtResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
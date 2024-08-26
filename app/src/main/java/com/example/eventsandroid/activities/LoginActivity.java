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

public class LoginActivity extends BaseActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_login, findViewById(R.id.content_frame));

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        AuthRequest loginRequest = new AuthRequest(username, password);

        ApiService apiService = ApiClient.getRetrofitInstance(this).create(ApiService.class);
        Call<JwtResponse> call = apiService.login(loginRequest);

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

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JwtResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
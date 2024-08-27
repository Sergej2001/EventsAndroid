package com.example.eventsandroid.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.eventsandroid.R;
import com.example.eventsandroid.services.ApiClient;
import com.example.eventsandroid.services.ApiService;
import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private SharedPreferences sharedPreferences;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        apiService = ApiClient.getRetrofitInstance(this).create(ApiService.class);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.nav_view);
        updateMenuItems(navigationView.getMenu());

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_events) {
                Intent intent = new Intent(BaseActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_login) {
                Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_register) {
                Intent intent = new Intent(BaseActivity.this, RegisterActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                logout();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        checkJwtValidity();
    }

    private void checkJwtValidity() {
        String token = sharedPreferences.getString("jwt_token", "");

        if (!"".equals(token)) {
            apiService.validateToken().enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (!response.isSuccessful() || response.code() == 401) {
                        logoutUser();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                }
            });
        } else {
            // Ako nema tokena, odmah logout
            logoutUser();
        }
    }

    private void logoutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void updateMenuItems(Menu menu) {
        boolean isLoggedIn = sharedPreferences.contains("jwt_token");

        menu.findItem(R.id.nav_login).setVisible(!isLoggedIn);
        menu.findItem(R.id.nav_register).setVisible(!isLoggedIn);
        menu.findItem(R.id.nav_logout).setVisible(isLoggedIn);
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
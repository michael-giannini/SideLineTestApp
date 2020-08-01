package com.example.sidelinetestapp.standalone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class testSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}

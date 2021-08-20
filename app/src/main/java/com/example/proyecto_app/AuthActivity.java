package com.example.proyecto_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.proyecto_app.Fragments.SignInFragment;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layoutContainer,new SignInFragment()).commit();
    }
}
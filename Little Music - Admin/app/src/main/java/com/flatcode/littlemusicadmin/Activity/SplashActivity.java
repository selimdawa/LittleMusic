package com.flatcode.littlemusicadmin.Activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlemusicadmin.Unit.CLASS;
import com.flatcode.littlemusicadmin.Unit.THEME;
import com.flatcode.littlemusicadmin.Unit.VOID;
import com.flatcode.littlemusicadmin.databinding.ActivitySplashBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    Context context = SplashActivity.this;
    FirebaseAuth auth;

    int time_per_second = 2;
    final static int time_per_millis = 1000;
    int time_final = time_per_millis * time_per_second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(context);
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        VOID.Logo(getBaseContext(), binding.logo);
        VOID.Intro(getBaseContext(), binding.background, binding.backWhite, binding.backBlack);

        auth = FirebaseAuth.getInstance();

        new Handler().postDelayed(this::checkUser, time_final);
    }

    private void checkUser() {
        //get current user, if logged in
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            VOID.Intent(context, CLASS.LOGIN);
        } else {
            VOID.Intent(context, CLASS.MAIN);
        }
        finish();
    }
}
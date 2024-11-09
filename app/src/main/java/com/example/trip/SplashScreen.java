package com.example.trip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.trip.Login_Registration.LoginPage;
import com.example.trip.Login_Registration.NewUserMain;
import com.example.trip.Login_Registration.RegistrationPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 4000;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent=new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }else{
                Intent intent = new Intent(SplashScreen.this, NewUserMain.class);
                startActivity(intent);
                finish();
            }}
        },SPLASH_TIME_OUT);

    }
}

package com.example.trip.Login_Registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.trip.R;
import com.google.firebase.auth.FirebaseUser;

public class NewUserMain extends AppCompatActivity {
    private FirebaseUser firebase;
    public Button login,signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_main);
        login=findViewById(R.id.BtnLogin);
        signup=findViewById(R.id.BtnSign);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToL=new Intent(NewUserMain.this,LoginPage.class);
                startActivity(intentToL);
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToR=new Intent(NewUserMain.this,RegistrationPage.class);
                startActivity(intentToR);
            }
        });

    }
}

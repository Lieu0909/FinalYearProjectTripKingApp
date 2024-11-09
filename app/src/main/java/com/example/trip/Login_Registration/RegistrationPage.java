package com.example.trip.Login_Registration;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trip.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistrationPage extends AppCompatActivity {
    private EditText tUsername,tEmail,tPassword;
    private TextView tLogin;
    private Button bRegister;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);
        tLogin=findViewById(R.id.TvS);
        tUsername=findViewById(R.id.TvEmail);
        tEmail=findViewById(R.id.TvE);
        tPassword=findViewById(R.id.TvPM);
        bRegister=findViewById(R.id.BtnSign);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent=new Intent(RegistrationPage.this,LoginPage.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        tLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ToL=new Intent(RegistrationPage.this,LoginPage.class);
                startActivity(ToL);
            }
        });
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = tEmail.getText().toString();
                final String password = tPassword.getText().toString();
                if(!validateUsername() | !validatePassword() | !validateEmail()){
                    return;
                }else {

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationPage.this,
                            new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegistrationPage.this, "Sign Up Error", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    String userID = mAuth.getCurrentUser().getUid();
                                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                                    String username = tUsername.getText().toString();
                                    String email = tEmail.getText().toString();

                                    //save all data at the same time
                                    Map newPost = new HashMap();
                                    newPost.put("name", username);
                                    newPost.put("email", email);


                                    current_user_db.setValue(newPost);
                                } catch (Exception e) {
                                    Toast.makeText(RegistrationPage.this, "" + e, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }


            }
        });

    }
    public boolean validateUsername(){
        String username = tUsername.getText().toString();
        if (TextUtils.isEmpty(username)) {
            tUsername.setError("Username is required.");
            return false;
        }else if(username.length()>15){
            tUsername.setError("Username not longer than 15 characters");
            return false;
        }else{
            tUsername.setError(null);
            return true;
        }
    }

    public  boolean validateEmail(){
        String email = tEmail.getText().toString();
        String emailPattern="[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(email)) {
            tEmail.setError("Email is required.");
            return false;
        }else if(!email.matches(emailPattern)){
            tEmail.setError("Not Email Format");
            return false;
        }else{
            tEmail.setError(null);
            return true;
        }
    }
    public boolean validatePassword(){
        String password = tPassword.getText().toString();
        String emailPattern="^"+
                "(?=.*[0-9])"+"(?=.*[a-zA-Z])"+"(?=.*[@#$%^&+=])"+"(?=\\S+$)"+".{7,}"+"$";
            /* ^                 # start-of-string
            (?=.*[0-9])       # a digit must occur at least once
            (?=.*[a-z])       # a lower case letter must occur at least once
            (?=.*[A-Z])       # an upper case letter must occur at least once
            (?=.*[@#$%^&+=])  # a special character must occur at least once you can replace with your special characters
            (?=\\S+$)          # no whitespace allowed in the entire string
            .{7,}             # anything, at least six places though
            $                 # end-of-string
            ");
           */
        if (TextUtils.isEmpty(password)) {
            tPassword.setError("Password is required.");
            return false;
        }else if(!password.matches(emailPattern)){
            tPassword.setError("Password should contain at least 1 Upper,Lower,and Special character");
            return false;
        }else{
            tPassword.setError(null);
            return true;
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);

    }

}


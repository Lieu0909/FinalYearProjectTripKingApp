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

import com.example.trip.MainActivity;
import com.example.trip.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {
    private EditText eMail,ePassword;
    private TextView cSignUp,cForget;
    private Button bLogin;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        cSignUp=findViewById(R.id.TvS);
        cForget=findViewById(R.id.TvF);
        eMail=findViewById(R.id.TvEmail);
        ePassword=findViewById(R.id.TvPM);
        bLogin=findViewById(R.id.BtnL);
        mFirebaseAuth = FirebaseAuth.getInstance();
        cSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ToS=new Intent(LoginPage.this,RegistrationPage.class);
                startActivity(ToS);
            }
        });

        cForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ToF=new Intent(LoginPage.this,ForgetPage.class);
                startActivity(ToF);
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if( mFirebaseUser != null ){
                    Toast.makeText(LoginPage.this,"You are logged in",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginPage.this, MainActivity.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(LoginPage.this,"Please Login",Toast.LENGTH_SHORT).show();
                }
            }
        };

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = eMail.getText().toString();
                String pwd = ePassword.getText().toString();
                if(email.isEmpty()){
                    eMail.setError("Please enter email id");
                    eMail.requestFocus();
                }
                else  if(pwd.isEmpty()){
                    ePassword.setError("Please enter your password");
                    ePassword.requestFocus();
                }
                else  if(email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(LoginPage.this,"Fields Are Empty!",Toast.LENGTH_SHORT).show();
                }
                else  if(!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginPage.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(LoginPage.this,"Wrong Password, Please Login Again",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Intent intToHome = new Intent(LoginPage.this,MainActivity.class);
                                startActivity(intToHome);
                                finish();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(LoginPage.this,"Error Occurred!",Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    public  boolean validateUsername(){
        String username = eMail.getText().toString();
        if (TextUtils.isEmpty(username)) {
            eMail.setError("Username is required.");
            return false;
        }else{
            eMail.setError(null);
            return true;
        }
    }
    public  boolean validatePassword(){
        String password = ePassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            ePassword.setError("Password is required.");
            return false;
        }else{
            ePassword.setError(null);
            return true;
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

}

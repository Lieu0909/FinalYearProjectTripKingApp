package com.example.trip.Login_Registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trip.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPage extends AppCompatActivity {
    private EditText ePasswordMail;
    private Button bForget;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_page);
        ePasswordMail=findViewById(R.id.TvPM);
        bForget=findViewById(R.id.BtnSign);

        mAuth=FirebaseAuth.getInstance();
        bForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usermail=ePasswordMail.getText().toString();
                if(!usermail.equals("")){
                    mAuth.sendPasswordResetEmail(usermail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                //Log.d(TAG, "Email sent.");
                                Toast.makeText(ForgetPage.this,"Password reset email sent!",Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ForgetPage.this, LoginPage.class));
                            }
                        }
                    });

                }else{
                    ePasswordMail.setError("Fill in the email");
                    Toast.makeText(ForgetPage.this,"Fill email",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

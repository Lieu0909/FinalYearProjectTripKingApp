package com.example.trip.ui.MyTrip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trip.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TripPlanningMenu extends AppCompatActivity {
    String passedId;
    Integer dayTrip;
    private LinearLayout blankTemplate;
    private DatabaseReference tripRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_planning_menu);

        Intent intent=getIntent();
        passedId=getIntent().getStringExtra("tripID");

        blankTemplate=findViewById(R.id.layout_blankTemplate);

        tripRef= FirebaseDatabase.getInstance().getReference("Trip");
        tripRef.child(passedId).child("tripNoDay").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dayTrip=  dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        blankTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),TripSchedule.class);
                i.putExtra("tripIDT",passedId);
                i.putExtra("noDay",dayTrip);
                startActivity(i);
            }
        });




    }

    }

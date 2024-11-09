package com.example.trip.ui.MyTrip;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.trip.Adapter.TripScheduleAdapter;
import com.example.trip.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TripSchedule extends AppCompatActivity {
    String passedId,passedId2,tripDetailsId,tripId;
    Integer dayTrip;
    FirebaseDatabase mDB;
    EditText t_changeNameeT;
    private TripScheduleAdapter adapter;
    private TabLayout tab;
    private ViewPager viewPager;
    TextView tName,sDate,eDate,dash,changeDateS,changeDateE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_schedule);
        viewPager = findViewById(R.id.viewPager);
        tab = findViewById(R.id.tabLayout);
        dash=findViewById(R.id.dash);
        Intent intent=getIntent();
        if(getIntent().hasExtra("tripIDT"))
        {
            passedId=getIntent().getStringExtra("tripIDT");
            dayTrip=getIntent().getIntExtra("noDay",1);

            mDB = FirebaseDatabase.getInstance();
            final DatabaseReference table_plan = mDB.getReference("Trip");

            table_plan.child(passedId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                        String name=map.get("tripName");
                        String startDate=map.get("tripSDate");
                        String endDate=map.get("tripEDate");
                        //mToolbar.setTitle(name);
                        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
                        myToolbar.setTitle(name);
                        setSupportActionBar(myToolbar);

                        sDate.setText(startDate);
                        eDate.setText(endDate);


                    }}

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else if(getIntent().hasExtra("blogID"))
        {
            passedId2 = getIntent().getStringExtra("blogID");
            dayTrip=getIntent().getIntExtra("noDay",1);
            tripDetailsId=getIntent().getStringExtra("tripCreateIDetails");
            tripId=getIntent().getStringExtra("tripCreateID");

            mDB = FirebaseDatabase.getInstance();
            final DatabaseReference table_plan = mDB.getReference("Blog");

            table_plan.child(passedId2).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                        String name=map.get("tripName");

                        //mToolbar.setTitle(name);
                        dash.setVisibility(View.GONE);
                        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
                        myToolbar.setTitle(name);
                        setSupportActionBar(myToolbar);

                    }}

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


        sDate=findViewById(R.id.tv_dayS);
        eDate=findViewById(R.id.tv_dayE);

        for (int k = 1; k < dayTrip+1; k++) {
            tab.addTab(tab.newTab().setText("Day"+ "" + k));
        }

        adapter = new TripScheduleAdapter
                (getSupportFragmentManager(), tab.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));

    }
    //toolbar/
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_t,menu);
        return true;

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if(id == R.id.changeInfo){
            final AlertDialog.Builder builder=new AlertDialog.Builder(this);
            View edit=getLayoutInflater().inflate(R.layout.t_change_date,null);
            TextView title = new TextView(getApplicationContext());
            title.setText("Change Date");
            title.setPadding(10, 10, 10, 10);
            title.setTextSize(20);
            title.setTypeface(title.getTypeface(), Typeface.BOLD);
            builder.setCustomTitle(title);
            builder.setView(edit);
            t_changeNameeT=edit.findViewById(R.id.tvc_name);
            changeDateS=edit.findViewById(R.id.etc_Sdate);
            changeDateE=edit.findViewById(R.id.etc_Edate);

            //retrive
            DatabaseReference rref=FirebaseDatabase.getInstance().getReference("Trip").child(passedId);
            rref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        String t1_Name=dataSnapshot.child("tripName").getValue().toString();
                        String d1_Start=dataSnapshot.child("tripSDate").getValue().toString();
                        String d1_End=dataSnapshot.child("tripEDate").getValue().toString();
                        t_changeNameeT.setText(t1_Name);
                        changeDateS.setText(d1_Start);
                        changeDateE.setText(d1_End);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Calendar calendar= Calendar.getInstance();
            final int year=calendar.get(Calendar.YEAR);
            final int month=calendar.get(Calendar.MONTH);
            final int day=calendar.get(calendar.DAY_OF_MONTH);
            changeDateS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog datePickerDialog=new DatePickerDialog(
                            TripSchedule.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            month=month+1;
                            String date=dayOfMonth+"/"+month+"/"+year;
                            changeDateS.setText(date);

                        }
                    },year,month,day);
                    //disable future date
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                    datePickerDialog.show();

                }
            });

            changeDateE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatePickerDialog datePickerDialog=new DatePickerDialog(
                            TripSchedule.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            month=month+1;
                            String date=dayOfMonth+"/"+month+"/"+year;
                            changeDateE.setText(date);

                        }
                    },year,month,day);
                    //disable future date
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                    datePickerDialog.show();


                }
            });

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Trip").child(passedId);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!TextUtils.isEmpty(changeDateS.getText().toString()) && !TextUtils.isEmpty(changeDateE.getText().toString()) && !TextUtils.isEmpty(t_changeNameeT.getText().toString()) ) {
                                final String tripSdate=changeDateS.getText().toString();
                                final String tripEdate=changeDateE.getText().toString();
                                final String TripName = t_changeNameeT.getText().toString().trim();
                                Date date1= null;
                                try {
                                    date1 = new SimpleDateFormat("dd/MM/yyyy").parse(tripSdate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Date date2= null;
                                try {
                                    date2 = new SimpleDateFormat("dd/MM/yyyy").parse(tripEdate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Integer diffInMillisec = Math.toIntExact(date2.getTime() - date1.getTime());
                                Integer diffInDays = Math.toIntExact(TimeUnit.MILLISECONDS.toDays(diffInMillisec));
                                Integer dayNo=diffInDays+1;
                                DatabaseReference databaseRef  = FirebaseDatabase.getInstance().getReference().child("Trip").child(passedId);
                                databaseRef.child("tripSDate").setValue(tripSdate);
                                databaseRef.child("tripEDate").setValue(tripEdate);
                                databaseRef.child("tripNoDay").setValue(dayNo);
                                databaseRef.child("tripName").setValue(TripName);
                                Toast.makeText(getApplicationContext(),"Changed Successfully",Toast.LENGTH_SHORT).show();

                            } else{
                                Toast.makeText(getApplicationContext(),"Changed Failed",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            builder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.trip.ui.MyTrip;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trip.Login_Registration.ProfileActivity;
import com.example.trip.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TripFragment extends Fragment {


    public static TripFragment newInstance() {
        return new TripFragment();
    }

    private TextView t_username,t_email;
    private Button btn_next;
    private ImageView profileImage;
    private EditText eTname;
    private TextView eTSdate,eTEdate;
    private RecyclerView recyclerView;
    FirebaseRecyclerOptions<Trip> options;
    FirebaseRecyclerAdapter<Trip, TripFragment.TripViewHolder>adapter;
    String uID;
    FirebaseAuth fAuth;
    FirebaseDatabase mDB;
    DatabaseReference mquery;
    private FirebaseUser mCurrentUser;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
       View v= inflater.inflate(R.layout.fragment_trip, container, false);

       t_username=v.findViewById(R.id.tv_username);
       t_email=v.findViewById(R.id.tv_email);
       btn_next=v.findViewById(R.id.btn_Goprofile);
       profileImage=v.findViewById(R.id.Image_profile);
       //recycler view
        recyclerView=v.findViewById(R.id.rcv_post_tripList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        setUpRecyclerView("");

        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                View create=getLayoutInflater().inflate(R.layout.t_create_trip,null);
                TextView title = new TextView(getContext());
                title.setText("Create Trip");
                title.setPadding(10, 10, 10, 10);
                title.setTextSize(20);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                builder.setCustomTitle(title);
                builder.setView(create);

                eTname=create.findViewById(R.id.et_name);
                eTSdate=create.findViewById(R.id.et_Sdate);
                eTEdate=create.findViewById(R.id.et_Edate);
                Calendar calendar= Calendar.getInstance();
                final int year=calendar.get(Calendar.YEAR);
                final int month=calendar.get(Calendar.MONTH);
                final int day=calendar.get(calendar.DAY_OF_MONTH);

                fAuth = FirebaseAuth.getInstance();
                mCurrentUser = fAuth.getCurrentUser();

                eTSdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog=new DatePickerDialog(
                                getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                month=month+1;
                                String date=dayOfMonth+"/"+month+"/"+year;
                                eTSdate.setText(date);
                            }
                        },year,month,day);
                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                        datePickerDialog.show();

                    }
                });
                eTEdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog=new DatePickerDialog(
                                getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                month=month+1;
                                String date=dayOfMonth+"/"+month+"/"+year;
                                eTEdate.setText(date);
                            }
                        },year,month,day);
                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                        datePickerDialog.show();

                    }
                });

                //ok
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    public void onClick(DialogInterface dialog, int id) {
                        final String tripName=eTname.getText().toString().trim();
                        final String tripSdate=eTSdate.getText().toString();
                        Date date1= null;
                        try {
                            date1 = new SimpleDateFormat("dd/MM/yyyy").parse(tripSdate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        final String tripEdate=eTEdate.getText().toString();
                        Date date2= null;
                        try {
                            date2 = new SimpleDateFormat("dd/MM/yyyy").parse(tripEdate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        final String uID=mCurrentUser.getUid();
                        if(!TextUtils.isEmpty(eTname.getText().toString()) && !TextUtils.isEmpty(eTSdate.getText().toString()) && !TextUtils.isEmpty(eTEdate.getText().toString()) ) {

                            Integer diffInMillisec = Math.toIntExact(date2.getTime() - date1.getTime());
                            Integer diffInDays = Math.toIntExact(TimeUnit.MILLISECONDS.toDays(diffInMillisec));
                            Integer dayNo=diffInDays+1;
                            DatabaseReference databaseRef  = FirebaseDatabase.getInstance().getReference().child("Trip");
                            Trip trip=new Trip(uID,tripName,tripSdate,tripEdate,dayNo);
                            String tripId = databaseRef.push().getKey();
                            databaseRef.child(tripId).setValue(trip);
                            Toast.makeText(getContext(),"Successfully Create",Toast.LENGTH_SHORT).show();

                        } else{
                            Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });
        fAuth=FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = mDB.getReference();
        FirebaseUser user = fAuth.getCurrentUser();
        uID=user.getUid();


        table_user.child("Users").child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                String name=map.get("name");
                String email=map.get("email");
                String image=map.get("imageurl");
                t_username.setText(name);
                t_email.setText(email);
                Picasso.with(getContext()).load(image).into(profileImage);
            }}
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);

            }
        });
        return v;
    }
    private void setUpRecyclerView(String data) {
        fAuth=FirebaseAuth.getInstance();
        uID=fAuth.getUid();
        Query query=FirebaseDatabase.getInstance().getReference("Trip").orderByChild("uID").equalTo(uID);

        options=new FirebaseRecyclerOptions.Builder<Trip>().setQuery(query,Trip.class).build();
        adapter=new FirebaseRecyclerAdapter<Trip, TripFragment.TripViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TripFragment.TripViewHolder holder, final int position, @NonNull Trip model) {
                holder.name.setText(model.getTripName());
                holder.dateS.setText(model.getTripSDate());
                holder.dateE.setText(model.getTripEDate());

                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyDataSetChanged();
                        Intent intent=new Intent(getActivity(), TripPlanningMenu.class);
                        intent.putExtra("tripID",getRef(position).getKey());
                        startActivity(intent);
                    }
                });

                holder.v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        String key=getRef(position).getKey();
                        showDeleteDialog(key);
                        notifyDataSetChanged();
                        return true;
                    }
                });

            }

            @NonNull
            @Override
            public TripFragment.TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.t_post_cardview,parent,false);
                return new TripFragment.TripViewHolder(v);
            }


        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    public void showDeleteDialog(String key){
        final androidx.appcompat.app.AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        TextView title = new TextView(getContext());
        title.setText("Delete Message");
        title.setPadding(10, 10, 10, 10);
        title.setTextSize(28);
        title.setTypeface(title.getTypeface(), Typeface.BOLD);
        builder.setCustomTitle(title);
        builder.setMessage("Are you sure to delete?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                FirebaseDatabase.getInstance().getReference("TripDetails").orderByChild("tripID").equalTo(key);

                mquery=FirebaseDatabase.getInstance().getReference("Trip");
                mquery.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){

                                        mquery.child(key).removeValue();
                                        delete(key);
                                        Toast.makeText(getContext(),"Successfully deleted",Toast.LENGTH_SHORT).show();


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                dialog.dismiss();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    class TripViewHolder extends RecyclerView.ViewHolder {
        TextView name,dateS,dateE;
        View v;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.t_trip_title);
            dateS=itemView.findViewById(R.id.t_tripDayS);
            dateE=itemView.findViewById(R.id.t_tripDayE);

            v=itemView;
        }
    }
    private void delete(String key) {
        Query ref = FirebaseDatabase.getInstance().getReference("TripDetails").orderByChild("tripID").equalTo(key);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ref.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}

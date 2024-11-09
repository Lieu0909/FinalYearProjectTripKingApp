package com.example.trip.ui.MyTrip;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trip.R;
import com.example.trip.ui.Blogger.BlogDetailsInfo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class DynamicDayFragment extends Fragment{
    android.view.View view;
    String passedId,pages,passedBId,tripDetailsId,tripId;
    int val,dayNo;
    private Button addDestination;
    private ImageButton btn_done;
    private TimePicker picker;
    private TextView t_places,readTime,noDays;
    private FragmentActivity context;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<TripDetailsInfo> options;
    private FirebaseRecyclerAdapter<TripDetailsInfo, DynamicDayFragment.ViewTripDetailsHolder> adapter;
    FirebaseRecyclerOptions<BlogDetailsInfo> options2;
    private FirebaseRecyclerAdapter<BlogDetailsInfo, DynamicDayFragment.ViewTripDetailsHolder> adapter2;
    private String apiKey="AIzaSyB4MxAtXFh_e0MmRa14OX9dJsDtOP-9Fzs";
    private  int AUTOCOMPLETE_REQUEST_CODE=1;
    // Specify the fields to return.
    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS);
    DatabaseReference mquery;

    public DynamicDayFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dynamic_day, container, false);
        val = getArguments().getInt("someInt", 0);
        Intent intent=getActivity().getIntent();
        if(getActivity().getIntent().hasExtra("tripIDT"))
        {
            passedId=getActivity().getIntent().getStringExtra("tripIDT");
        //retrieve for list
        recyclerView=view.findViewById(R.id.rcv_itineraryList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        setUpRecyclerView("");
            noDays=view.findViewById(R.id.dayNo);
            dayNo=val+1;
            noDays.setText("Day" +dayNo);
        }
        else if(getActivity().getIntent().hasExtra("blogID"))
        {
            passedBId=getActivity().getIntent().getStringExtra("blogID");
            tripDetailsId=getActivity().getIntent().getStringExtra("tripCreateIDetails");
            tripId=getActivity().getIntent().getStringExtra("tripCreateID");
            //retrieve for list
            recyclerView=view.findViewById(R.id.rcv_itineraryList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setHasFixedSize(true);
            setUpRecyclerView2("");
            noDays=view.findViewById(R.id.dayNo);
            dayNo=val+1;
            noDays.setText("Day" +dayNo);
        }


        //button add destination
        addDestination=view.findViewById(R.id.btn_addDestination);
        addDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                View addPlaces=getLayoutInflater().inflate(R.layout.t_add_trip_info,null);
                TextView title = new TextView(getContext());
                title.setText("Add Destination ");
                title.setPadding(10, 10, 10, 10);
                title.setTextSize(28);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                builder.setCustomTitle(title);
                builder.setMessage("Planning details");
                builder.setView(addPlaces);
                //adapter component
                t_places=addPlaces.findViewById(R.id.tv_placeName);
                btn_done=addPlaces.findViewById(R.id.btn_doneTime);
                readTime=addPlaces.findViewById(R.id.et_readTime);
                picker=addPlaces.findViewById(R.id.timePicker1);
                picker.setIs24HourView(true);

                if(!Places.isInitialized()){
                    // Initialize the SDK
                    Places.initialize(getContext(), apiKey);
                }
                // Create a new PlacesClient instance
                PlacesClient placesClient = Places.createClient(getContext());

                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS);
                // Start the autocomplete intent.
                t_places.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, placeFields).build(getContext());
                        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                    }
                });
                btn_done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int hour, minute;
                        String am_pm;
                        if (Build.VERSION.SDK_INT >= 23 ){
                            hour = picker.getHour();
                            minute = picker.getMinute();
                        }
                        else{
                            hour = picker.getCurrentHour();
                            minute = picker.getCurrentMinute();
                        }
                        if(hour > 12) {
                            am_pm = "PM";
                            hour = hour - 12;
                        }
                        else
                        {
                            am_pm="AM";
                        }
                        readTime.setText(hour +":"+ minute+" "+am_pm);
                    }
                });


                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(!TextUtils.isEmpty(readTime.getText().toString()) && !TextUtils.isEmpty(t_places.getText().toString())){
                            pages="Fragment" +val;
                            DatabaseReference planRef = FirebaseDatabase.getInstance().getReference("Trip").child(passedId).child("TripDetails");
                            String tripIdI = planRef.push().getKey();
                            TripDetailsInfo info=new TripDetailsInfo(passedId,t_places.getText().toString(),readTime.getText().toString(),pages);
                            planRef.child(tripIdI).setValue(info);
                            Toast.makeText(getContext(),"Successfully Create",Toast.LENGTH_SHORT).show();

                            dialog.dismiss();
                        } else {
                            Toast.makeText(getContext(),"Please Select Image or Fill The Blank",Toast.LENGTH_SHORT).show();
                        }

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
        });


        return view;
    }
    @Override
    public void onAttach(Activity activity) {
        context = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    private void setUpRecyclerView2(String data) {

        Query query=FirebaseDatabase.getInstance().getReference("Trip").child(tripId).child("TripDetails").orderByChild("fragment_no").equalTo("Fragment0");


        options=new FirebaseRecyclerOptions.Builder<TripDetailsInfo>().setQuery(query,TripDetailsInfo.class).build();
        adapter=new FirebaseRecyclerAdapter<TripDetailsInfo, DynamicDayFragment.ViewTripDetailsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DynamicDayFragment.ViewTripDetailsHolder holder, final int position, @NonNull TripDetailsInfo model) {
                holder.p_place.setText(model.getPlace());

                //Pop out dialog
                final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                TextView title = new TextView(getContext());
                title.setText("Information");
                title.setPadding(10, 10, 10, 10);
                title.setTextSize(28);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                builder.setCustomTitle(title);
                builder.setMessage("Continue editing in Trip Planning Site");

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getContext(),"Continue editing in Trip Planning Site",Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();
            }

            @NonNull
            @Override
            public DynamicDayFragment.ViewTripDetailsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.t_rcv_plan,parent,false);
                return new ViewTripDetailsHolder(v);
            }


        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("tag", "Place: " + place.getName() + ", " + place.getLatLng());
                t_places.setText(place.getName());
                //tvPlaceId.setText(place.getId());
                //tvLatLon.setText(String.valueOf(place.getLatLng()));

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("tag", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void setUpRecyclerView(String data) {
        String fragment_no="Fragment" +val;
        Query query=FirebaseDatabase.getInstance().getReference("Trip").child(passedId).child("TripDetails").orderByChild("fragment_no").equalTo(fragment_no);


        options=new FirebaseRecyclerOptions.Builder<TripDetailsInfo>().setQuery(query,TripDetailsInfo.class).build();
        adapter=new FirebaseRecyclerAdapter<TripDetailsInfo, DynamicDayFragment.ViewTripDetailsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DynamicDayFragment.ViewTripDetailsHolder holder, final int position, @NonNull TripDetailsInfo model) {
                holder.p_place.setText(model.getPlace());
               holder.p_time.setText(model.getTime());

                holder.v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        String key=getRef(position).getKey();
                        showDeleteDialog(key);
                        notifyDataSetChanged();
                        return true;
                    }
                });
                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                        View editPlan=getLayoutInflater().inflate(R.layout.t_add_trip_info,null);
                        TextView title = new TextView(getContext());
                        title.setText("Trip Planning Info");
                        title.setPadding(10, 10, 10, 10);
                        title.setTextSize(28);
                        title.setTypeface(title.getTypeface(), Typeface.BOLD);
                        builder.setCustomTitle(title);
                        builder.setMessage("Edit trip planning details");
                        builder.setView(editPlan);
                        String key=getRef(position).getKey();
                        //adapter component
                        t_places=editPlan.findViewById(R.id.tv_placeName);
                        btn_done=editPlan.findViewById(R.id.btn_doneTime);
                        readTime=editPlan.findViewById(R.id.et_readTime);
                        picker=editPlan.findViewById(R.id.timePicker1);
                        picker.setIs24HourView(true);
                        //retrieve for dialogbox
                        DatabaseReference trip=FirebaseDatabase.getInstance().getReference("Trip").child(passedId).child("TripDetails");
                        trip.child(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                                    String t_dest = map.get("place");
                                    String t_time = map.get("time");
                                    t_places.setText(t_dest);
                                    readTime.setText(t_time);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        if(!Places.isInitialized()){
                            // Initialize the SDK
                            Places.initialize(getContext(), apiKey);
                        }
                        // Create a new PlacesClient instance
                        PlacesClient placesClient = Places.createClient(getContext());

                        // Set the fields to specify which types of place data to
                        // return after the user has made a selection.
                        placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS);
                        // Start the autocomplete intent.
                        t_places.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, placeFields).build(getContext());
                                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                            }
                        });
                        btn_done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int hour, minute;
                                String am_pm;
                                if (Build.VERSION.SDK_INT >= 23 ){
                                    hour = picker.getHour();
                                    minute = picker.getMinute();
                                }
                                else{
                                    hour = picker.getCurrentHour();
                                    minute = picker.getCurrentMinute();
                                }
                                if(hour > 12) {
                                    am_pm = "PM";
                                    hour = hour - 12;
                                }
                                else
                                {
                                    am_pm="AM";
                                }
                                readTime.setText(hour +":"+ minute+" "+am_pm);
                            }
                        });
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                       if(!TextUtils.isEmpty(t_places.getText().toString()) && !TextUtils.isEmpty(readTime.getText().toString())){
                                           DatabaseReference tripUpdate = FirebaseDatabase.getInstance().getReference("Trip").child(passedId).child("TripDetails").child(key);
                                           tripUpdate.child("place").setValue(t_places.getText().toString());
                                           tripUpdate.child("time").setValue(readTime.getText().toString());
                                       }else {
                                           Toast.makeText(getContext(),"Please Select Image or Fill The Blank",Toast.LENGTH_SHORT).show();
                                       }

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
                });

            }

            @NonNull
            @Override
            public DynamicDayFragment.ViewTripDetailsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.t_rcv_plan,parent,false);
                return new ViewTripDetailsHolder(v);
            }


        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }
    class ViewTripDetailsHolder extends RecyclerView.ViewHolder {
        TextView p_place, p_time;
        View v;

        public ViewTripDetailsHolder(@NonNull View itemView) {
            super(itemView);
            p_place = itemView.findViewById(R.id.tT_trip_place);
            p_time = itemView.findViewById(R.id.tT_trip_time);


            v = itemView;
        }
    }


    public void showDeleteDialog(String key){
        final androidx.appcompat.app.AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        TextView title = new TextView(getContext());
        title.setText("Blog Info");
        title.setPadding(10, 10, 10, 10);
        title.setTextSize(28);
        title.setTypeface(title.getTypeface(), Typeface.BOLD);
        builder.setCustomTitle(title);
        builder.setMessage("Are you sure to delete?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //FirebaseDatabase.getInstance().getReference("TripDetails").child(passedId).equalTo(key);

                mquery=FirebaseDatabase.getInstance().getReference("Trip").child(passedId).child("TripDetails");
                mquery.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){

                                        mquery.child(key).removeValue();
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


    public static DynamicDayFragment addfrag(int val) {
        DynamicDayFragment fragment = new DynamicDayFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", val);
        fragment.setArguments(args);
        return fragment;
    }


}

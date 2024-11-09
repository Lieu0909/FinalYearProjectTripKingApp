package com.example.trip.ui.Blogger;

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

import com.example.trip.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class DrafBlogFragment extends Fragment  {
    View v;
    private FirebaseAuth auth;
    private String currentUserId;
    FirebaseRecyclerOptions<CreateBlogInfo> options;
    FirebaseRecyclerAdapter<CreateBlogInfo,MyViewHolder>adapter;
    private RecyclerView recyclerView;
    DatabaseReference mquery;
    private EditText eTname,eTdescription;
    private TextView eTdate,eTEnd;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    String parent;
    CreateBlogInfo createBlogInfo;

    public static DrafBlogFragment newInstance() {
        return new DrafBlogFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        v= inflater.inflate(R.layout.fragment_draf_blog, container, false);

        recyclerView=v.findViewById(R.id.rcv_draf_blogList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        setUpRecyclerView("");



        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                View create=getLayoutInflater().inflate(R.layout.b_create_blog,null);
                TextView title = new TextView(getContext());
                title.setText("Create Blog");
                title.setPadding(10, 10, 10, 10);
                title.setTextSize(20);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                builder.setCustomTitle(title);
                builder.setView(create);
                eTname=create.findViewById(R.id.et_Bname);
                eTdate=create.findViewById(R.id.et_Bcalendar);
                eTEnd=create.findViewById(R.id.et_BEnd);
                eTdescription=create.findViewById(R.id.et_Bdescription);
                final Calendar calendar=Calendar.getInstance();
                final int year=calendar.get(Calendar.YEAR);
                final int month=calendar.get(Calendar.MONTH);
                final int day=calendar.get(calendar.DAY_OF_MONTH);

                eTdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatePickerDialog datePickerDialog=new DatePickerDialog(
                                getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                month=month+1;
                                String date=dayOfMonth+"/"+month+"/"+year;
                                eTdate.setText(date);
                            }
                        },year,month,day);
                        //disable future date
                        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                        datePickerDialog.show();

                    }
                });

                eTEnd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatePickerDialog datePickerDialog=new DatePickerDialog(
                                getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                month=month+1;
                                String date=dayOfMonth+"/"+month+"/"+year;
                                eTEnd.setText(date);
                            }
                        },year,month,day);
                        //disable future date
                        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                        datePickerDialog.show();

                    }
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    public void onClick(DialogInterface dialog, int id) {
                        mAuth = FirebaseAuth.getInstance();
                        mCurrentUser = mAuth.getCurrentUser();
                        final String TripName = eTname.getText().toString().trim();
                        final String DayStart = eTdate.getText().toString().trim();
                        final String DayEnd = eTEnd.getText().toString().trim();
                        final String Description = eTdescription.getText().toString().trim();
                        final String uID=mCurrentUser.getUid();
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
                        String dateTime=simpleDateFormat.format(calendar.getTime());

                        Date date1= null;
                        try {
                            date1 = new SimpleDateFormat("dd/MM/yyyy").parse(DayStart);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date date2= null;
                        try {
                            date2 = new SimpleDateFormat("dd/MM/yyyy").parse(DayEnd);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if(!TextUtils.isEmpty(eTname.getText().toString()) && !TextUtils.isEmpty(eTdate.getText().toString()) && !TextUtils.isEmpty(eTEnd.getText().toString()) && !TextUtils.isEmpty(eTdescription.getText().toString())) {
                            Integer diffInMillisec = Math.toIntExact(date2.getTime() - date1.getTime());
                            Integer diffInDays = Math.toIntExact(TimeUnit.MILLISECONDS.toDays(diffInMillisec));
                            Integer dayNo=diffInDays+1;
                            DatabaseReference blog = FirebaseDatabase.getInstance().getReference().child("Blog");
                            String s="No_"+uID;
                            String blogId = blog.push().getKey();
                            CreateBlogInfo createBlogInfo = new CreateBlogInfo(uID, TripName, Description, DayStart, DayEnd, dateTime, dayNo,s);
                            blog.child(blogId).setValue(createBlogInfo);
                            Toast.makeText(getContext(),"Successfully Create",Toast.LENGTH_SHORT).show();

                        } else{
                            Toast.makeText(getContext(),"Fill in the blank",Toast.LENGTH_SHORT).show();
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

        return v;
    }

    private void setUpRecyclerView(String data) {
        auth=FirebaseAuth.getInstance();
        currentUserId=auth.getUid();

        Query query=FirebaseDatabase.getInstance().getReference("Blog").orderByChild("status").equalTo("No_"+currentUserId);

        options=new FirebaseRecyclerOptions.Builder<CreateBlogInfo>().setQuery(query,CreateBlogInfo.class).build();
        adapter=new FirebaseRecyclerAdapter<CreateBlogInfo, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull CreateBlogInfo model) {
                holder.name.setText(model.getTripName());
                holder.date.setText(model.getDate());
                holder.description.setText(model.getDescription());
                if(model.getImageURL()==null){
                    Picasso.with(holder.imageView.getContext()).load(R.drawable.sea_background).into(holder.imageView);
                }else{
                    Picasso.with(holder.imageView.getContext()).load(model.getImageURL()).into(holder.imageView);
                }

                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyDataSetChanged();
                        Intent intent=new Intent(getActivity(),BlogUploadMenu.class);
                        intent.putExtra("blogID",getRef(position).getKey());
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
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.b_draf_cardview,parent,false);
                return new MyViewHolder(v);
            }


        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name,date,description;
        View v;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.b_postImage);
            date=itemView.findViewById(R.id.b_post_createDay);
            name=itemView.findViewById(R.id.b_post_title);
            description=itemView.findViewById(R.id.b_post_description);
            v=itemView;
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
                delete(key);
                mquery=FirebaseDatabase.getInstance().getReference("Blog");
                mquery.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild("imageURL")){
                                String db_image=dataSnapshot.child("imageURL").getValue().toString();
                                StorageReference fs= FirebaseStorage.getInstance().getReferenceFromUrl(db_image);
                                fs.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mquery.child(key).removeValue();

                                        Toast.makeText(getContext(),"Successfully deleted",Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }else{
                                Toast.makeText(getContext(),"Successfully deleted",Toast.LENGTH_SHORT).show();
                                mquery.child(key).removeValue();
                            }
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
    private void delete(String key){

        DatabaseReference mquery=FirebaseDatabase.getInstance().getReference("Blog").child(key).child("BlogDetails");

        mquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                     parent = childSnapshot.getKey();
                    mquery.child(parent).orderByChild("blogID").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                if(dataSnapshot.hasChild("destinationPic")){
                                    String db_image=dataSnapshot.child("destinationPic").getValue().toString();
                                    StorageReference fs= FirebaseStorage.getInstance().getReferenceFromUrl(db_image);
                                    fs.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mquery.child(parent).getRef().removeValue();
                                            Toast.makeText(getContext(),"Successfully deleted",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

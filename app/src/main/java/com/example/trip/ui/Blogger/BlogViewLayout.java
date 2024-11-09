package com.example.trip.ui.Blogger;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trip.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;



public class BlogViewLayout extends AppCompatActivity {
private TextView tripN,tripD,tripS,tripE,p_blogID;
private ImageView tripP;
private DatabaseReference blogRef,blogU;
private FirebaseAuth auth;
private String uID;
FirebaseRecyclerOptions<BlogDetailsInfo> options;
private FirebaseRecyclerAdapter<BlogDetailsInfo,ViewDetailsHolder> adapter;
private RecyclerView recyclerView;
private String passBID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_view_layout);
        Intent intent=getIntent();
        passBID=getIntent().getStringExtra("passBlogID");

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tripN=findViewById(R.id.v_tripName);
        tripD=findViewById(R.id.v_tripDescription);
        tripS=findViewById(R.id.v_tripStart);
        tripE=findViewById(R.id.v_tripEnd);
        tripP=findViewById(R.id.v_imagePic);
        p_blogID=findViewById(R.id.v_passID);
        p_blogID.setText(passBID);
        auth=FirebaseAuth.getInstance();
        uID=auth.getUid();
        blogRef=FirebaseDatabase.getInstance().getReference();


        //retrieve
        blogRef.child("Blog").child(passBID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String End=dataSnapshot.child("dayEnd").getValue().toString();
                    String Start=dataSnapshot.child("dayStart").getValue().toString();
                    String Name=dataSnapshot.child("tripName").getValue().toString();
                    String description=dataSnapshot.child("description").getValue().toString();

                    if(dataSnapshot.child("imageURL").exists()){
                        String image=dataSnapshot.child("imageURL").getValue().toString();
                        Picasso.with(getApplicationContext()).load(image).into(tripP);
                    }else{
                        Picasso.with(getApplicationContext()).load(R.drawable.bb_create_blog).into(tripP);
                    }
                    tripN.setText(Name);
                    tripD.setText(description);
                    tripS.setText(Start);
                    tripE.setText(End);

            }}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //retrieve for list
        recyclerView=findViewById(R.id.rcv_vBlog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        setUpRecyclerView("");



    }
    //toolbar
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_b,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if(id == R.id.share){
            final AlertDialog.Builder builder=new AlertDialog.Builder(BlogViewLayout.this);
            TextView title = new TextView(getApplicationContext());
            title.setText("Share");
            title.setPadding(10, 10, 10, 10);
            title.setTextSize(28);
            title.setTypeface(title.getTypeface(), Typeface.BOLD);
            builder.setCustomTitle(title);
            builder.setMessage("Confirm to share?");

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
                    Calendar cal = Calendar.getInstance();
                    String dateTime=simpleDateFormat.format(cal.getTime());
                    DatabaseReference blogUpdate = FirebaseDatabase.getInstance().getReference().child("Blog").child(passBID);
                    blogUpdate.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("imageURL").exists()){

                                blogUpdate.child("status").setValue("Share_"+uID);
                                blogUpdate.child("shareTime").setValue(dateTime);
                                Toast.makeText(getApplicationContext(), "Share Successfully ", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }else{
                                Toast.makeText(getApplicationContext(), "Failed to share, Please upload main image before share ", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
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

    private void setUpRecyclerView(String data) {
        Query query=FirebaseDatabase.getInstance().getReference("Blog").child(passBID).child("BlogDetails").orderByChild("blogID").equalTo(passBID);

        options=new FirebaseRecyclerOptions.Builder<BlogDetailsInfo>().setQuery(query,BlogDetailsInfo.class).build();
        adapter=new FirebaseRecyclerAdapter<BlogDetailsInfo, ViewDetailsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewDetailsHolder holder, final int position, @NonNull BlogDetailsInfo model) {
                holder.v_name.setText(model.getDestinationName());
                holder.v_descript.setText(model.getDestinationDescription());
                holder.v_budget.setText("RM"+ String.valueOf(model.getDestinationBudget()));
                Picasso.with(holder.v_Pic.getContext()).load(model.getDestinationPic()).into(holder.v_Pic);


            }

            @NonNull
            @Override
            public ViewDetailsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.b_view_blog,parent,false);
                return new ViewDetailsHolder(v);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }
    public class ViewDetailsHolder extends RecyclerView.ViewHolder {
        private ImageView v_Pic;
        private TextView v_name,v_descript,v_budget;
        private View v;

        public ViewDetailsHolder(View view) {
            super(view);
            v_Pic = view.findViewById(R.id.vv_Pic);
            v_name = view.findViewById(R.id.vv_name);
            v_descript = view.findViewById(R.id.vv_description);
            v_budget = view.findViewById(R.id.vv_budget);
            v=itemView;
        }

    }
}

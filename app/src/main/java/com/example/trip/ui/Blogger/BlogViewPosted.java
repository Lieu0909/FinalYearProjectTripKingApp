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

public class BlogViewPosted extends AppCompatActivity {
TextView t_name,s_date,e_date,likes,passID,description;
ImageView tripImage;
RecyclerView recyclerView;
private int likescount;
private Integer dayNo;
private String passBID,uID,Name,destinationN;
private DatabaseReference blogRef,likesRef;
FirebaseRecyclerOptions<BlogDetailsInfo> options;
FirebaseAuth auth;
private FirebaseRecyclerAdapter<BlogDetailsInfo, BlogViewPosted.SharedViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_view_posted);

        Intent intent=getIntent();
        passBID=getIntent().getStringExtra("blogID");
        Toolbar toolbar=findViewById(R.id.toolbar_discover);
        setSupportActionBar(toolbar);

        passID=findViewById(R.id.s_passID);
        t_name=findViewById(R.id.s_tripName);
        tripImage=findViewById(R.id.s_imagePic);
        likes=findViewById(R.id.s_likes_textview);
        s_date=findViewById(R.id.s_tripStart);
        e_date=findViewById(R.id.s_tripEnd);
        description=findViewById(R.id.s_tripDescription);
        auth=FirebaseAuth.getInstance();
        uID=auth.getUid();

        blogRef= FirebaseDatabase.getInstance().getReference();
        likesRef= FirebaseDatabase.getInstance().getReference();
        //retrieve likes details
        likesRef.child("Likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(passBID)){
                    likescount = (int)dataSnapshot.child(passBID).getChildrenCount();
                    likes.setText(Integer.toString(likescount));
                }else {
                    likescount = (int)dataSnapshot.child(passBID).getChildrenCount();
                    likes.setText(Integer.toString(likescount)+"likes");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //retrieve blog details
        blogRef.child("Blog").child(passBID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Name=dataSnapshot.child("tripName").getValue().toString();
                    String Description=dataSnapshot.child("description").getValue().toString();
                    String Start=dataSnapshot.child("dayStart").getValue().toString();
                    String End=dataSnapshot.child("dayEnd").getValue().toString();
                    String image=dataSnapshot.child("imageURL").getValue().toString();
                    dayNo= dataSnapshot.child("dayNo").getValue(Integer.class);

                    Picasso.with(getApplicationContext()).load(image).into(tripImage);
                    t_name.setText(Name);
                    description.setText(Description);
                    s_date.setText(Start);
                    e_date.setText(End);

            }}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView=findViewById(R.id.rcv_s_Blog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        setUpRecyclerView("");


    }
    //toolbar/
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_s,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if(id == R.id.drafback){
            final AlertDialog.Builder builder=new AlertDialog.Builder(BlogViewPosted.this);
            TextView title = new TextView(getApplicationContext());
            title.setText("Draf Back");
            title.setPadding(10, 10, 10, 10);
            title.setTextSize(28);
            title.setTypeface(title.getTypeface(), Typeface.BOLD);
            builder.setCustomTitle(title);
            builder.setMessage("Confirm to draf back?");

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    DatabaseReference blogUpdate = FirebaseDatabase.getInstance().getReference().child("Blog").child(passBID);
                    blogUpdate.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                blogUpdate.child("status").setValue("No_" + uID);
                                //blogRef.child("Blog").child(passBID).child("shareTime").removeValue();

                                dialog.dismiss();


                            } else {
                                Toast.makeText(getApplicationContext(), "Draft Back Successfully ", Toast.LENGTH_SHORT).show();
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
        adapter=new FirebaseRecyclerAdapter<BlogDetailsInfo, BlogViewPosted.SharedViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BlogViewPosted.SharedViewHolder holder, final int position, @NonNull BlogDetailsInfo model) {
                holder.s_name.setText(model.getDestinationName());
                holder.s_descript.setText(model.getDestinationDescription());
                holder.s_budget.setText("RM"+ String.valueOf(model.getDestinationBudget()));
                Picasso.with(holder.s_Pic.getContext()).load(model.getDestinationPic()).into(holder.s_Pic);
                destinationN=model.destinationName;


            }

            @NonNull
            @Override
            public BlogViewPosted.SharedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.b_view_blog,parent,false);
                return new SharedViewHolder(v);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }
    public class SharedViewHolder extends RecyclerView.ViewHolder {
        private ImageView s_Pic;
        private TextView s_name,s_descript,s_budget;
        private View v;

        public SharedViewHolder(View view) {
            super(view);
            s_Pic = view.findViewById(R.id.vv_Pic);
            s_name = view.findViewById(R.id.vv_name);
            s_descript = view.findViewById(R.id.vv_description);
            s_budget = view.findViewById(R.id.vv_budget);
            v=itemView;
        }

    }


}
